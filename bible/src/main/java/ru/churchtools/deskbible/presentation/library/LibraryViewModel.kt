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
 * File: LibraryViewModel.kt
 *
 * Created by Vladimir Yakushev at 1/2023
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.presentation.library

import android.net.Uri
import androidx.lifecycle.*
import com.BibleQuote.di.scope.PerFragment
import com.BibleQuote.entity.ItemList
import com.BibleQuote.managers.Librarian
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.churchtools.deskbible.domain.library.ImportModuleHandler
import ru.churchtools.deskbible.domain.library.ImportModuleHandler.StatusCode
import ru.churchtools.deskbible.domain.logger.StaticLogger
import javax.inject.Inject

class LibraryViewModel(
    private val librarian: Librarian,
    private val importModuleHandler: ImportModuleHandler
) : ViewModel() {

    private val _moduleList = MutableLiveData<List<ItemList>>()
    val moduleList: LiveData<List<ItemList>>
        get() = _moduleList

    private val _importErrorStatus = MutableLiveData<StatusCode>()
    val importErrorStatus: LiveData<StatusCode>
        get() = _importErrorStatus

    fun onCreated() {
        viewModelScope.launch(Dispatchers.IO) {
            _moduleList.postValue(librarian.modulesList)
        }
    }

    fun onSelectModuleForImport(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            importModuleHandler.loadModule(uri).let {
                when(it) {
                    StatusCode.Success -> {
                        _moduleList.postValue(librarian.modulesList)
                    }
                    else -> _importErrorStatus.postValue(it)
                }
            }
        }
    }

    fun onClickDeleteModule(item: ItemList) {
        viewModelScope.launch(Dispatchers.IO) {
            item[ItemList.ID]?.let {
                try {
                    if (librarian.removeModule(it)) {
                        _moduleList.postValue(librarian.modulesList)
                    }
                } catch (ex: Exception) {
                    StaticLogger.error(this, "Remove module failed $it", ex)
                }
            }
        }
    }

    @PerFragment
    class Factory @Inject constructor(
        private val librarian: Librarian,
        private val importModuleHandler: ImportModuleHandler
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LibraryViewModel(librarian, importModuleHandler) as T
        }
    }
}