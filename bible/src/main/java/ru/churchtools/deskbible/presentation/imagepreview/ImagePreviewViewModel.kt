package ru.churchtools.deskbible.presentation.imagepreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.BibleQuote.di.scope.PerActivity
import com.BibleQuote.managers.Librarian
import javax.inject.Inject

class ImagePreviewViewModel(
    private val myLibrarian: Librarian,
) : ViewModel() {

    private val _imageState = MutableLiveData<ImagePreviewViewResult>()
    val imageState: LiveData<ImagePreviewViewResult>
        get() = _imageState

    fun onActivityCreate(imagePath: String) {
        val imageBitmap = myLibrarian.getModuleImage(imagePath)
        _imageState.value = ImagePreviewViewResult.DrawImage(imageBitmap)
    }

    @PerActivity
    class Factory @Inject constructor(
        private val myLibrarian: Librarian,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ImagePreviewViewModel(myLibrarian) as T
    }
}