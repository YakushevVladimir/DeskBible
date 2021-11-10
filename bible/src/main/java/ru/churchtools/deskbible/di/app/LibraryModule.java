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
 * File: LibraryModule.java
 *
 * Created by Vladimir Yakushev at 11/2021
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.churchtools.ru
 */

package ru.churchtools.deskbible.di.app;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.churchtools.deskbible.data.library.DefaultLibraryContext;
import ru.churchtools.deskbible.data.library.LibraryContext;

/**
 * Dagger-модуль с зависимостями для библиотеки приложения
 *
 * @author Yakushev Vladimir <ru.phoenix@gmail.com>
 */
@Module
public interface LibraryModule {

    @Provides
    static LibraryContext provideLibraryContext(Context context) {
        return new DefaultLibraryContext(context.getFilesDir());
    }
}
