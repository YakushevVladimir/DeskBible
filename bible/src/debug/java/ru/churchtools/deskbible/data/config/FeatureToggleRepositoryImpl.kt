package ru.churchtools.deskbible.data.config

import android.content.Context
import androidx.annotation.RawRes
import ru.churchtools.deskbible.domain.config.FeatureToggleRepository

/**
 * Репозиторий для чтения рубильников из ресурсов
 *
 * @property context контекст приложения для доступа к ресурсам
 * @property configResId идентификатор raw-ресурса с тогглами
 */
class FeatureToggleRepositoryImpl(
        private val context: Context,
        @RawRes private val configResId: Int
) : FeatureToggleRepository() {

    override fun getTogglesStream() = context.resources.openRawResource(configResId)
}