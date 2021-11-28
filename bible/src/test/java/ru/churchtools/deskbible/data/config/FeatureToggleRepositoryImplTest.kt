package ru.churchtools.deskbible.data.config

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import ru.churchtools.deskbible.data.cache.Cache
import java.io.IOException
import java.io.InputStream

/**
 * Тесты для [FeatureToggleRepositoryImpl]
 */
class FeatureToggleRepositoryImplTest {

    private val requestSlot = slot<Request>()
    private val response: Response = mockk()
    private val httpClient: OkHttpClient = mockk()
    private val cache: Cache<String> = mockk {
        every { put(CACHE_KEY, RESPONSE_STRING) } just runs
    }

    private val repository = FeatureToggleRepositoryImpl(httpClient, TOGGLES_URL, cache)

    @Before
    fun setUp() {
    }

    @Test
    fun getTogglesStream_withExceptionAndNotCached() {
        every { httpClient.newCall(capture(requestSlot)) } throws IOException()
        every { cache.getOrNull(CACHE_KEY) } returns null
        assertThat(repository.getTogglesStream()).isNull()
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify { cache.getOrNull(CACHE_KEY) }
    }

    @Test
    fun getTogglesStream_withExceptionAndCached() {
        every { httpClient.newCall(capture(requestSlot)) } throws IOException()
        every { cache.getOrNull(CACHE_KEY) } returns RESPONSE_STRING
        assertThat(repository.getTogglesStream()?.readText()).isEqualTo(RESPONSE_STRING)
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify { cache.getOrNull(CACHE_KEY) }
    }

    @Test
    fun getTogglesStream_withResponseNotSuccessfulAndNotCached() {
        every { httpClient.newCall(capture(requestSlot)).execute() } returns response
        every { response.isSuccessful } returns false
        every { cache.getOrNull(CACHE_KEY) } returns null
        assertThat(repository.getTogglesStream()).isNull()
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify { cache.getOrNull(CACHE_KEY) }
    }

    @Test
    fun getTogglesStream_withResponseNotSuccessfulAndCached() {
        every { httpClient.newCall(capture(requestSlot)).execute() } returns response
        every { response.isSuccessful } returns false
        every { cache.getOrNull(CACHE_KEY) } returns RESPONSE_STRING
        assertThat(repository.getTogglesStream()?.readText()).isEqualTo(RESPONSE_STRING)
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify { cache.getOrNull(CACHE_KEY) }
    }

    @Test
    fun getTogglesStream_withoutResponseBodyAndNotCached() {
        every { httpClient.newCall(capture(requestSlot)).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns null
        every { cache.getOrNull(CACHE_KEY) } returns null
        assertThat(repository.getTogglesStream()).isNull()
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify { cache.getOrNull(CACHE_KEY) }
    }

    @Test
    fun getTogglesStream_withoutResponseBodyAndCached() {
        every { httpClient.newCall(capture(requestSlot)).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body } returns null
        every { cache.getOrNull(CACHE_KEY) } returns RESPONSE_STRING
        assertThat(repository.getTogglesStream()?.readText()).isEqualTo(RESPONSE_STRING)
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify { cache.getOrNull(CACHE_KEY) }
   }

    @Test
    fun getTogglesStream_withResponseSuccess() {
        every { response.body } returns RESPONSE_STRING.toResponseBody()
        every { response.isSuccessful } returns true
        every { httpClient.newCall(capture(requestSlot)).execute() } returns response
        assertThat(repository.getTogglesStream()?.readText()).isEqualTo(RESPONSE_STRING)
        assertThat(requestSlot.captured.url).isEqualTo(TOGGLES_URL.toHttpUrl())
        verify(exactly = 0) { cache.getOrNull(CACHE_KEY) }
    }

    private fun InputStream.readText() = this.reader(Charsets.UTF_8).readText()

    private companion object {
        private const val TOGGLES_URL = "https://testhost.ru/toggles"
        private const val CACHE_KEY = "toggles_stream"
        private const val RESPONSE_STRING = "response"
    }
}