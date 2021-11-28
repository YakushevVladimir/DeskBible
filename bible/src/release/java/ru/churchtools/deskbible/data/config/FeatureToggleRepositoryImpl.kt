package ru.churchtools.deskbible.data.config

import okhttp3.OkHttpClient
import okhttp3.Request
import ru.churchtools.deskbible.data.cache.Cache
import ru.churchtools.deskbible.domain.config.FeatureToggleRepository
import ru.churchtools.deskbible.domain.logger.StaticLogger
import java.io.InputStream

class FeatureToggleRepositoryImpl(
        private val httpClient: OkHttpClient,
        private val togglesUrl: String,
        private val cache: Cache<String>
) : FeatureToggleRepository() {

    override fun getTogglesStream(): InputStream? {
        val request = Request.Builder().url(togglesUrl).build()
        var result: InputStream? = null
        try {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()?.also { toggles ->
                    cache.put(CACHE_KEY, toggles)
                    result = toggles.toByteArray().inputStream()
                }
            }
        } catch (ex: Exception) {
            StaticLogger.error(this, "Get toggles request failed", ex)
        }

        return result ?: cache.getOrNull(CACHE_KEY)?.byteInputStream()
    }

    private companion object {
        private const val CACHE_KEY = "toggles_stream"
    }
}