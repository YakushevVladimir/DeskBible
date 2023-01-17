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
 * File: LibraryFragment.kt
 *
 * Created by Vladimir Yakushev at 1/2023
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.presentation.library

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.BibleQuote.BibleQuoteApp
import com.BibleQuote.R
import com.BibleQuote.di.module.FragmentModule
import com.BibleQuote.entity.ItemList
import com.BibleQuote.presentation.dialogs.NotifyDialog
import ru.churchtools.deskbible.domain.library.ImportModuleHandler.StatusCode
import javax.inject.Inject

class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val viewModel: LibraryViewModel by viewModels { viewModelFactory }
    private val listView: RecyclerView by lazy { requireView().findViewById(R.id.list) }
    private val adapter = LibraryAdapter(this::sendResult, this::deleteModule)

    @Inject
    lateinit var viewModelFactory: LibraryViewModel.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectDependencies()

        requireActivity().apply {
            addMenuProvider(LibraryMenuProvider(), this)
        }

        listView.adapter = adapter
        listView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        viewModel.moduleList.observe(viewLifecycleOwner, adapter::submitList)
        viewModel.importErrorStatus.observe(viewLifecycleOwner, this::onImportError)
        viewModel.onCreated()
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && requestCode == ACTION_CODE_GET_FILE) {
            intent?.data?.let { viewModel.onSelectModuleForImport(it) }
        }
    }

    private fun sendResult(item: ItemList) {
        setFragmentResult(
            REQUEST_SELECT_MODULE,
            bundleOf(EXTRA_MODULE_ID to item[ItemList.ID])
        )
    }

    private fun deleteModule(item: ItemList) {
        viewModel.onClickDeleteModule(item)
    }

    private fun onImportError(statusCode: StatusCode) {
        val errorMessage = when (statusCode) {
            StatusCode.FileNotExist -> getString(R.string.file_not_exist)
            StatusCode.FileNotSupported -> getString(R.string.file_not_supported)
            StatusCode.MoveFailed -> getString(R.string.file_not_moved)
            StatusCode.LibraryNotFound -> getString(R.string.file_not_moved)
            else -> getString(R.string.err_load_module_unknown)
        }
        NotifyDialog(errorMessage, requireContext()).show()
    }

    @Suppress("DEPRECATION")
    private fun choiceModuleFromFile() {
        val target = Intent(Intent.ACTION_GET_CONTENT)
            .setType("application/zip")
            .addCategory(Intent.CATEGORY_OPENABLE)
        if (target.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(target, ACTION_CODE_GET_FILE)
        } else {
            Toast.makeText(
                requireContext(),
                R.string.exception_add_module_from_file,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun injectDependencies() {
        val component = BibleQuoteApp.instance(requireContext())
            .appComponent
            .fragmentComponent(FragmentModule())
        component.inject(this)
    }

    private inner class LibraryMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_library, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            if (menuItem.itemId == R.id.menu_library_add) {
                choiceModuleFromFile()
                return true
            }

            return false
        }
    }

    companion object {
        /**
         * Константа ключа в Bundle для передачи выбранной книги
         */
        const val EXTRA_MODULE_ID = "extra_module_id"

        /**
         * Ключ для получения результата выбора модуля
         */
        const val REQUEST_SELECT_MODULE = "request_select_module"

        /**
         * Список кодов для вызова дополнительных операций получения результата
         */
        const val ACTION_CODE_GET_FILE = 1
    }
}