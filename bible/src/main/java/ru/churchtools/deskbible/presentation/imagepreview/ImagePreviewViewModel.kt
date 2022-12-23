package ru.churchtools.deskbible.presentation.imagepreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.BibleQuote.di.scope.PerActivity
import com.BibleQuote.managers.Librarian
import javax.inject.Inject

class ImagePreviewViewModel(
    private val myLibrarian: Librarian,
) : ViewModel() {


    @PerActivity
    class Factory @Inject constructor(
        private val myLibrarian: Librarian,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ImagePreviewViewModel(myLibrarian) as T
    }
}