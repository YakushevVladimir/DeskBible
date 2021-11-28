package ru.churchtools.deskbible.di.app

import android.content.Context
import com.BibleQuote.R
import dagger.Module
import dagger.Provides
import ru.churchtools.deskbible.data.config.FeatureToggleRepositoryImpl
import ru.churchtools.deskbible.domain.config.FeatureToggle
import ru.churchtools.deskbible.domain.config.FeatureToggleImpl
import javax.inject.Singleton

@Module
class ConfigModule {

    @Singleton
    @Provides
    fun provideFeatureToggle(context: Context): FeatureToggle {
        return FeatureToggleImpl(FeatureToggleRepositoryImpl(context, R.raw.deskbible_config))
    }
}