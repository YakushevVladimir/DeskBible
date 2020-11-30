package com.BibleQuote.data.logger;

import androidx.annotation.NonNull;
import android.util.Log;

import com.BibleQuote.domain.logger.Logger;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * Логгирование событий в Crashlytics
 *
 * @author Vladimir Yackushev <Yakushev.V.V@sberbank.ru>
 * @since 07/01/2019
 */
public class CrashlyticsLogger extends Logger {

    private final FirebaseCrashlytics mCrashlytics = FirebaseCrashlytics.getInstance();

    @Override
    public void debug(@NonNull Object tag, @NonNull String message) {
        mCrashlytics.log(message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message) {
        mCrashlytics.log(message);
    }

    @Override
    public void error(@NonNull Object tag, @NonNull String message, @NonNull Throwable th) {
        mCrashlytics.recordException(th);
    }

    @Override
    public void info(@NonNull Object tag, @NonNull String message) {
        mCrashlytics.log(message);
    }
}
