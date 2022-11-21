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
 * Project: DeskBible
 * File: SplashActivity.kt
 *
 * Created by Vladimir Yakushev at 11/2022
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */
package ru.churchtools.deskbible.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import com.BibleQuote.R
import com.BibleQuote.di.component.ActivityComponent
import com.BibleQuote.presentation.ui.base.BQActivity
import com.BibleQuote.presentation.ui.reader.ReaderActivity
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.churchtools.deskbible.domain.logger.StaticLogger.info
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : BQActivity() {

    private val updateDescriptionView: TextView by lazy {
        findViewById(R.id.update_description)
    }
    private val rootLayout: ViewGroup by lazy {
        findViewById(R.id.root_layout)
    }
    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var viewModelFactory: SplashViewModel.Factory

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel.result.observe(this) { result ->
            when (result) {
                is SplashViewResult.UpdateResult -> showUpdateMessage(result.message)
                is SplashViewResult.InitFailure -> showErrorMessage()
                is SplashViewResult.InitSuccess -> gotoReaderActivity()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewStarted()
    }

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    private fun showUpdateMessage(message: Int) {
        info(this, getString(message))
        updateDescriptionView.setText(message)
    }

    private fun showErrorMessage() {
        Snackbar.make(rootLayout, R.string.error_initialization_failed, LENGTH_INDEFINITE)
            .setAction(R.string.retry) { viewModel.onViewStarted() }
            .show()
    }

    private fun gotoReaderActivity() {
        startActivity(Intent(this, ReaderActivity::class.java))
        finish()
    }
}