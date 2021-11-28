package ru.churchtools.deskbible.data.cache

interface Cache<T> {

    fun getOrNull(key: String): T?

    fun getOrDefault(key: String, defaultValue: T): T

    fun put(key: String, value: T)
}