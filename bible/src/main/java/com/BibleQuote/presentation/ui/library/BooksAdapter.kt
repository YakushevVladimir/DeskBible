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
 * File: BooksAdapter.kt
 *
 * Created by Vladimir Yakushev at 1/2023
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package com.BibleQuote.presentation.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.BibleQuote.R
import com.BibleQuote.domain.entity.Book

class BooksAdapter(
    private val clickListener: Function1<Book, Void>
): ListAdapter<Book, BooksAdapter.ViewHolder>(BookDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_book, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position), clickListener)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val idView: TextView by lazy { itemView.findViewById(R.id.id) }
        private val nameView: TextView by lazy { itemView.findViewById(R.id.name) }
        fun bindView(book: Book, clickListener: Function1<Book, Void>) {
            idView.text = book.id
            nameView.text = book.name
            itemView.setOnClickListener { clickListener.invoke(book) }
        }
    }

    class BookDiffUtilCallback: DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem == newItem
    }
}