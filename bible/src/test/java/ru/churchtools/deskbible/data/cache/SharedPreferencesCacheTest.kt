package ru.churchtools.deskbible.data.cache

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SharedPreferencesCacheTest {

    private val editor: SharedPreferences.Editor = mockk {
        every { putBoolean(any(), any()) } returns this
        every { apply() } just runs
    }
    private val sharedPreferences: SharedPreferences = mockk {
        every { edit() } returns editor
    }
    private val context: Context = mockk {
        every { getSharedPreferences(NAME, MODE_PRIVATE) } returns sharedPreferences
    }

    private lateinit var cache: SharedPreferencesCache<Boolean>

    @Before
    fun setUp() {
        cache = SharedPreferencesCache(context, NAME)
    }

    @Test
    fun testGetOrNull_withNotFound() {
        every { sharedPreferences.all } returns emptyMap()
        assertThat(cache.getOrNull(KEY1)).isNull()
        assertThat(cache.getOrNull(KEY2)).isNull()
    }

    @Test
    fun testGetOrNull_withFound() {
        every { sharedPreferences.all } returns mapOf(KEY1 to true, KEY2 to false)
        assertThat(cache.getOrNull(KEY1)).isTrue()
        assertThat(cache.getOrNull(KEY2)).isFalse()
    }

    @Test
    fun testGetOrDefault_withNotFound() {
        every { sharedPreferences.all } returns emptyMap()
        assertThat(cache.getOrDefault(KEY1, true)).isTrue()
        assertThat(cache.getOrDefault(KEY2, false)).isFalse()
    }

    @Test
    fun testGetOrDefault_withFound() {
        every { sharedPreferences.all } returns mapOf(KEY1 to false, KEY2 to true)
        assertThat(cache.getOrDefault(KEY1, true)).isFalse()
        assertThat(cache.getOrDefault(KEY2, false)).isTrue()
    }

    @Test
    fun testPut() {
        cache.apply {
            put(KEY1, false)
            put(KEY2, true)
        }
        verify {
            editor.putBoolean(KEY1, false)
            editor.putBoolean(KEY2, true)
        }
    }

    companion object {
        private const val NAME = "name"
        private const val KEY1 = "key1"
        private const val KEY2 = "key2"
    }
}