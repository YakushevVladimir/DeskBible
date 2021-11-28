package ru.churchtools.deskbible.domain.config

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Test

class FeatureToggleImplTest {

    private val repository: FeatureToggleRepository = mockk{
        every { initToggles() } just runs
    }
    private val featureToggle = FeatureToggleImpl(repository)

    @Test
    fun testInitToggles() {
        featureToggle.initToggles()
        verify { repository.initToggles() }
    }

    @Test
    fun testNewLibraryUiEnabled_withEnabled() {
        every { repository.isEnabled(KEY_NEW_LIBRARY_UI) } returns true
        assertThat(featureToggle.newLibraryUiEnabled()).isTrue()
    }

    @Test
    fun testNewLibraryUiEnabled_withDisabled() {
        every { repository.isEnabled(KEY_NEW_LIBRARY_UI) } returns false
        assertThat(featureToggle.newLibraryUiEnabled()).isFalse()
    }

    companion object {
        private const val KEY_NEW_LIBRARY_UI = "new_library_ui"
    }
}