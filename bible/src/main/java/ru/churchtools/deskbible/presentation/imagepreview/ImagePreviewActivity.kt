/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: ImagePreviewActivity.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package ru.churchtools.deskbible.presentation.imagepreview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.BibleQuote.R
import com.BibleQuote.di.component.ActivityComponent
import com.BibleQuote.presentation.ui.base.BQActivity
import com.BibleQuote.presentation.widget.TouchImageView
import javax.inject.Inject

class ImagePreviewActivity : BQActivity() {

    private val imageView: TouchImageView by lazy {
        findViewById(R.id.image)
    }

    private val viewModel: ImagePreviewViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var viewModelFactory: ImagePreviewViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        intent.getStringExtra(IMAGE_PATH)?.let { path ->
            viewModel.imageState.observe(this) { result ->
                when (result) {
                    is ImagePreviewViewResult.DrawImage -> updatePreviewDrawable(result.image)
                    is ImagePreviewViewResult.UnsuccessfulSearch -> imageNotFound()
                }
            }
            viewModel.onActivityCreate(path)
        }
            ?: let {
                Log.e(TAG, "No params")
                imageNotFound()
            }
    }

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    private fun imageNotFound() {
        Toast.makeText(this, R.string.image_not_found, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun updatePreviewDrawable(value: Bitmap) {
        imageView.maxZoom = DEFAULT_ZOOM.toFloat()
        imageView.setImageDrawable(BitmapDrawable(resources, value))
    }

    companion object {
        private val TAG: String = ImagePreviewActivity::class.java.simpleName
        private const val IMAGE_PATH: String = "image_path"
        private const val DEFAULT_ZOOM: Int = 10

        @JvmStatic
        fun getIntent(context: Context, imagePath: String): Intent {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(IMAGE_PATH, imagePath)
            return intent
        }
    }
}