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
 * File: LibraryAdapter.kt
 *
 * Created by Vladimir Yakushev at 1/2023
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.presentation.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.BibleQuote.R
import com.BibleQuote.entity.ItemList

class LibraryAdapter(
    private val selectListener: (item: ItemList) -> Unit,
    private val deleteListener: (item: ItemList) -> Unit
): ListAdapter<ItemList, LibraryAdapter.ViewHolder>(ItemDiffCallback()) {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val idView: TextView by lazy { itemView.findViewById(R.id.id) }
        private val nameView: TextView by lazy { itemView.findViewById(R.id.name) }
        private val deleteView: View by lazy { itemView.findViewById(R.id.btn_delete) }

        fun bindView(item: ItemList,
                     selectListener: (item: ItemList) -> Unit,
                     deleteListener: (item: ItemList) -> Unit
        ) {
            idView.text = item[ItemList.ID]
            nameView.text = item[ItemList.Name]
            itemView.setOnClickListener { selectListener(item) }
            deleteView.setOnClickListener { deleteListener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_library, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position), selectListener, deleteListener)
    }

    class ItemDiffCallback: DiffUtil.ItemCallback<ItemList>() {
        override fun areItemsTheSame(oldItem: ItemList, newItem: ItemList): Boolean =
            oldItem[ItemList.ID] == newItem[ItemList.ID]

        override fun areContentsTheSame(oldItem: ItemList, newItem: ItemList): Boolean =
            oldItem == newItem
    }
}