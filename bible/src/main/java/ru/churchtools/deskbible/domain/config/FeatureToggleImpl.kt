package ru.churchtools.deskbible.domain.config

/**
 * Реализация для получения значения рубильников фичей
 *
 * @param repository репозиторий для чтений значений рубильников
 */
class FeatureToggleImpl(
        private val repository: FeatureToggleRepository
) : FeatureToggle {

    override fun initToggles() {
        repository.initToggles()
    }

    override fun newLibraryUiEnabled(): Boolean = repository.isEnabled("new_library_ui")
}