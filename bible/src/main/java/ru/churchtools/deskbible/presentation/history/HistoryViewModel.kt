package ru.churchtools.deskbible.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.BibleQuote.di.scope.PerActivity
import com.BibleQuote.entity.ItemList
import com.BibleQuote.managers.Librarian
import javax.inject.Inject

class HistoryViewModel(
    private val myLibrarian: Librarian,
) : ViewModel() {

    private val _historyState = MutableLiveData<HistoryViewResult>()
    val historyState: LiveData<HistoryViewResult>
        get() = _historyState

    fun onActivityCreate() {
        updateHistoryList()
    }

    fun onClickList(item: ItemList) {
        item[ItemList.ID]?.let { link ->
            _historyState.value = HistoryViewResult.OpenLink(link)
        }
    }

    fun onClickClearHistory() {
        myLibrarian.clearHistory()
        updateHistoryList()
    }

    private fun updateHistoryList() {
        val historyList = myLibrarian.historyList
        _historyState.value = HistoryViewResult.HistoryList(historyList)
    }

    @PerActivity
    class Factory @Inject constructor(
        private val myLibrarian: Librarian,
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass : Class<T>): T =
            HistoryViewModel(myLibrarian) as T
    }
}