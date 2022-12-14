package ru.churchtools.deskbible.presentation.history

import com.BibleQuote.entity.ItemList

sealed class HistoryViewResult {

    data class HistoryList(val list: List<ItemList>) : HistoryViewResult()
    data class OpenLink(val link: String) : HistoryViewResult()

}