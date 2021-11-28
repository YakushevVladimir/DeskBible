package ru.churchtools.deskbible.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ru.churchtools.deskbible.data.cache.SharedPreferencesCache
import ru.churchtools.deskbible.data.config.FeatureToggleRepositoryImpl
import ru.churchtools.deskbible.domain.config.FeatureToggle
import ru.churchtools.deskbible.domain.config.FeatureToggleImpl
import javax.inject.Singleton

@Module
class ConfigModule {

    @Singleton
    @Provides
    fun provideFeatureToggle(context: Context, httpClient: OkHttpClient): FeatureToggle {
        return FeatureToggleImpl(FeatureToggleRepositoryImpl(
                httpClient,
                "https://static.churchtools.ru/deskbible_config.json",
                SharedPreferencesCache(context, "feature_toggles")
        ))
    }
}