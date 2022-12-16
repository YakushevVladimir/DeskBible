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
 * File: HistoryActivity.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package ru.churchtools.deskbible.presentation.history

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.viewModels
import com.BibleQuote.R
import com.BibleQuote.di.component.ActivityComponent
import com.BibleQuote.entity.ItemList
import com.BibleQuote.presentation.ui.base.BQActivity
import javax.inject.Inject

class HistoryActivity : BQActivity() {

    private val vHistoryList: ListView by lazy {
        findViewById(R.id.FavoritsLV)
    }

    private val viewModel: HistoryViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var viewModelFactory: HistoryViewModel.Factory

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewModel.historyState.observe(this) {
            when (it) {
                is HistoryViewResult.HistoryList -> setListAdapter(it.list)
                is HistoryViewResult.OpenLink -> sendResult(it.link)
            }
        }

        vHistoryList.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                viewModel.onClickList(position)
            }

        viewModel.onActivityCreate()
    }

    private fun sendResult(link: String) {
        val intent = Intent()
        intent.putExtra("linkOSIS", link)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val infl = menuInflater
        infl.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_bar_history_clear -> {
                viewModel.onClickClearHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setListAdapter(list: List<ItemList>) {
        vHistoryList.adapter = SimpleAdapter(
            this,
            list,
            R.layout.item_list_no_id,
            arrayOf(ItemList.ID, ItemList.Name),
            intArrayOf(R.id.id, R.id.name))
    }
}