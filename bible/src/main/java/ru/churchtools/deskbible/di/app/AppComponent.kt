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
 * File: AppComponent.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package ru.churchtools.deskbible.di.app

import android.content.Context
import com.BibleQuote.BibleQuoteApp
import com.BibleQuote.di.component.ActivityComponent
import com.BibleQuote.di.component.FragmentComponent
import com.BibleQuote.di.module.ActivityModule
import com.BibleQuote.di.module.AppModule
import com.BibleQuote.di.module.DataModule
import com.BibleQuote.di.module.FragmentModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    DataModule::class,
    ConfigModule::class,
    LibraryModule::class,
    MigrationModule::class
])
@Singleton
interface AppComponent {

    fun activityComponent(module: ActivityModule): ActivityComponent

    fun fragmentComponent(module: FragmentModule): FragmentComponent

    fun inject(application: BibleQuoteApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}