package ru.churchtools.deskbible.domain.config

import androidx.annotation.WorkerThread

/**
 * Рубильники для фичей
 */
interface FeatureToggle {

    /**
     * Инициализация рубильников
     */
    fun initToggles()

    /**
     * Доступность функционала нового UI для библиотеки
     */
    @WorkerThread
    fun newLibraryUiEnabled(): Boolean
}