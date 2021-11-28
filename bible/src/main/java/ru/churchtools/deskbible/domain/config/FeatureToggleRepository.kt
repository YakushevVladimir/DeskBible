package ru.churchtools.deskbible.domain.config

import android.util.JsonReader
import androidx.annotation.WorkerThread
import ru.churchtools.deskbible.domain.logger.StaticLogger
import java.io.InputStream

/**
 * Репозиторий для получения рубильников
 */
abstract class FeatureToggleRepository {

    private lateinit var toggles: Map<String, Boolean>

    fun initToggles() {
        try {
            readToggles(getTogglesStream())
        } catch (ex: Exception) {
            StaticLogger.error(this, "Init toggles failed", ex)
        }
    }

    /**
     * Проверка доступности фичи по имени рубильника
     *
     * @param toggleKey
     */
    @WorkerThread
    fun isEnabled(toggleKey: String): Boolean = toggles.getOrDefault(toggleKey, false)

    abstract fun getTogglesStream(): InputStream?

    private fun readToggles(stream: InputStream?) {
        val result = mutableMapOf<String, Boolean>()
        stream?.use {
            JsonReader(it.bufferedReader()).use { reader ->
                reader.beginObject()
                while (reader.hasNext()) {
                    result[reader.nextName()] = reader.nextBoolean()
                }
            }
        }

        toggles = result.toMap()
    }
}