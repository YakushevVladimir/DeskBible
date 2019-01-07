package com.BibleQuote.data.logger.file

/**
 * Сообщение для логгирования
 *
 * @param tag метка для сообщения
 * @param message сообщение для лога
 *
 * @author Vladimir Yakushev <ru.phoenix@gmail.com>
 * @since 07/01/2019
 */
data class LogMessage(
        val tag: String,
        val message: String
)
