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
 * File: TextAppearance.java
 *
 * Created by Vladimir Yakushev at 9/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.entity;

import com.google.auto.value.AutoValue;

/**
 * @author Vladimir Yakushev (ru.phoenix@gmail.com)
 */

@AutoValue
public abstract class TextAppearance {

    public abstract String getBackground();
    public abstract int getLineSpacing();
    public abstract String getSelectedBackgroung();
    public abstract String getSelectedTextColor();
    public abstract String getTextAlign();
    public abstract String getTextColor();
    public abstract String getTextSize();
    public abstract String getTypeface();
    public abstract boolean isNightMode();

    public static Builder builder() {
        return new AutoValue_TextAppearance.Builder();
    }

    @SuppressWarnings("WeakerAccess")
    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder background(String value);
        public abstract Builder lineSpacing(int value);
        public abstract Builder selectedBackgroung(String value);
        public abstract Builder selectedTextColor(String value);
        public abstract Builder textAlign(String value);
        public abstract Builder textColor(String value);
        public abstract Builder textSize(String value);
        public abstract Builder typeface(String value);
        public abstract Builder nightMode(boolean value);
        public abstract TextAppearance build();
    }
}
