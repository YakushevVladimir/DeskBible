package ru.churchtools.deskbible.presentation.imagepreview

import android.graphics.Bitmap

sealed class ImagePreviewViewResult {

    data class SaveImagePath(val imagePath: String) : ImagePreviewViewResult()
    data class DrawImage(val image: Bitmap) : ImagePreviewViewResult()

}