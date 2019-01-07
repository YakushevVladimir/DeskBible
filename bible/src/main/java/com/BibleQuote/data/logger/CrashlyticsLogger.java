package com.BibleQuote.data.logger;

import android.support.annotation.NonNull;
import android.util.Log;

import com.BibleQuote.domain.logger.Logger;
import com.crashlytics.android.Crashlytics;

/**
 * Логгирование событий в Crashlytics
 *
 * @author Vladimir Yackushev <Yakushev.V.V@sberbank.ru>
 * @since 07/01/2019
 */
public class CrashlyticsLogger extends Logger {

    @Override
    public void debug(@NonNull Object tag, @NonNull String message) {
        Crashlytics.log(Log.DEBUG, getTag(tag), message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message) {
        Crashlytics.log(Log.ERROR, getTag(tag), message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message, @NonNull Throwable th) {
        String logMessage = message + "\n" + Log.getStackTraceString(th);
        Crashlytics.log(Log.ERROR, getTag(tag), logMessage);
    }

    @Override
    public void info(@NonNull Object tag, @NonNull String message) {
        Crashlytics.log(Log.INFO, getTag(tag), message);
    }
}
