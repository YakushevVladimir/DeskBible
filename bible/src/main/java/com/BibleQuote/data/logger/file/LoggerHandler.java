package com.BibleQuote.data.logger.file;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.BibleQuote.BuildConfig;
import com.BibleQuote.utils.DataConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Обработчик записи событий в файл
 * <p>
 * Выполняет следующие функции:
 * <ul>
 *     <li>Записывает события в отдельном потоке, освобождая UI-поток</li>
 *     <li>Гарантированное записывает каждое сообщение. На время запроса разрешений от Андроид
 *     файловое устройство может быть недоступно какое-то время.</li>
 * </ul>
 *
 * @author Vladimir Yakushev <ru.phoenix@gmail.com>
 * @since 07/01/2019
 */
public class LoggerHandler extends Handler {

    static final int ACTION_WRITE = 0;

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final long MAX_LOG_SIZE = 10 * 1024 * 1024;

    @Nullable
    private File mLogFile;
    @NonNull
    private final String mTag;
    @NonNull
    private final LinkedList<LogMessage> mMessages = new LinkedList<>();

    LoggerHandler(Looper looper, @NonNull String tag) {
        super(looper);
        mTag = tag;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ACTION_WRITE:
                mMessages.addLast((LogMessage) msg.obj);
                write();
                break;
            default:
                super.handleMessage(msg);
        }
    }

    private void write() {
        mLogFile = getLogFile();
        if (mLogFile == null) {
            return;
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(mLogFile, true), Charset.forName("UTF-8"))) {
            LogMessage message;
            while ((message = mMessages.peekFirst()) != null) {
                writer.write(String.format("%s %s %s%n", dateFormat.format(new Date()), message.getTag(), message.getMessage()));
                mMessages.removeFirst();
            }
        } catch (Exception e) {
            Log.e(mTag, e.getMessage());
        }
    }

    /**
     * Подготовка файла-протокола событий. Создание нового файла,
     * запись текущей даты, версии программы, языка системы
     */
    @Nullable
    private File getLogFile() {
        if (mLogFile != null) {
            return mLogFile;
        } else if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        File logFile = new File(DataConstants.getFsAppDirName(), "log.txt");
        if (logFile.exists() && !logFile.canWrite()) {
            return null;
        } else if (logFile.length() > MAX_LOG_SIZE) {
            if (!logFile.delete()) {
                mMessages.add(new LogMessage(mTag, "Не удалось очистить лог-файл"));
            }
        }

        mMessages.addAll(0, Arrays.asList(
                new LogMessage(mTag, "===================================="),
                new LogMessage(mTag, "Application version: " + BuildConfig.VERSION_NAME),
                new LogMessage(mTag, "Default language: " + Locale.getDefault().getDisplayLanguage()),
                new LogMessage(mTag, "Device model: " + Build.MODEL),
                new LogMessage(mTag, "Android OS: " + Build.VERSION.RELEASE),
                new LogMessage(mTag, "------------------------------------")));

        return logFile;
    }
}
