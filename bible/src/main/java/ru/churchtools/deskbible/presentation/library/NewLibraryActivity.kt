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
 * File: LibraryActivity.kt
 *
 * Created by Vladimir Yakushev at 1/2023
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.presentation.library

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.BibleQuote.R
import ru.churchtools.deskbible.presentation.library.LibraryFragment.Companion.EXTRA_MODULE_ID
import ru.churchtools.deskbible.presentation.library.LibraryFragment.Companion.REQUEST_SELECT_MODULE

class NewLibraryActivity : AppCompatActivity(R.layout.activity_lybrary_new) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.setFragmentResultListener(
            REQUEST_SELECT_MODULE,
            this) { _, bundle ->
            val intent = Intent().putExtra(EXTRA_MODULE_ID, bundle.getString(EXTRA_MODULE_ID))
            setResult(RESULT_OK, intent)
            finish()
        }
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<LibraryFragment>(R.id.fragment_container_view)
        }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context) = Intent(context, NewLibraryActivity::class.java)
    }
}