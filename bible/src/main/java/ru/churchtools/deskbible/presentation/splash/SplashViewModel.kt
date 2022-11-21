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
 * File: SplashViewModel.kt
 *
 * Created by Vladimir Yakushev at 11/2022
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.BibleQuote.di.scope.PerActivity
import com.BibleQuote.domain.controller.ILibraryController
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import ru.churchtools.deskbible.domain.RxSchedulers
import ru.churchtools.deskbible.domain.config.FeatureToggle
import ru.churchtools.deskbible.domain.logger.StaticLogger
import ru.churchtools.deskbible.domain.migration.UpdateManager
import javax.inject.Inject

class SplashViewModel(
    private val libraryController: ILibraryController,
    private val updateManager: UpdateManager,
    private val featureToggle: FeatureToggle,
    private val rxSchedulers: RxSchedulers
): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val resultData = MutableLiveData<SplashViewResult>()
    val result: LiveData<SplashViewResult>
        get() = resultData

    fun onViewStarted() {
        updateManager.update()
            .subscribeOn(rxSchedulers.computation)
            .observeOn(rxSchedulers.mainThread)
            .subscribe(
                { message: Int ->
                    resultData.value = SplashViewResult.UpdateResult(message)
                },
                { throwable: Throwable ->
                    StaticLogger.error(this, "Update failure", throwable)
                    resultData.value = SplashViewResult.InitFailure
                },
                { initLibrary() }
            ).let {
                compositeDisposable.add(it)
            }
    }

    private fun initLibrary() {
        Completable.fromRunnable { libraryController.init() }
            .concatWith(Completable.fromRunnable { featureToggle.initToggles() })
            .subscribeOn(rxSchedulers.computation)
            .observeOn(rxSchedulers.mainThread)
            .subscribe(
                { resultData.value = SplashViewResult.InitSuccess },
                { throwable: Throwable ->
                    StaticLogger.error(this, "Init library failure", throwable)
                    resultData.value = SplashViewResult.InitFailure
            }).let {
                compositeDisposable.add(it)
            }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    @PerActivity
    class Factory @Inject constructor(
        private val libraryController: ILibraryController,
        private val updateManager: UpdateManager,
        private val featureToggle: FeatureToggle,
        private val rxSchedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SplashViewModel(libraryController, updateManager, featureToggle, rxSchedulers) as T
    }
}