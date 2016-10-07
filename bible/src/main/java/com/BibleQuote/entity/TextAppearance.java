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
 * Created by Vladimir Yakushev at 10/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.entity;

/**
 * @author Vladimir Yakushev (ru.phoenix@gmail.com)
 */

public class TextAppearance {

    private String typeface;
    private String textColor;
    private String selectedTextColor;
    private String backgroung;
    private String selectedBackgroung;
    private String textSize;
    private String textAlign;
    private boolean nightMode;

    public TextAppearance(String typeface, String textSize, String textColor, String backgroung, String selectedTextColor, String selectedBackgroung, String textAlign, boolean nightMode) {
        this.typeface = typeface;
        this.textSize = textSize;
        this.textColor = textColor;
        this.backgroung = backgroung;
        this.selectedTextColor = selectedTextColor;
        this.selectedBackgroung = selectedBackgroung;
        this.textAlign = textAlign;
        this.nightMode = nightMode;
    }

    public String getBackgroung() {
        return backgroung;
    }

    public String getSelectedBackgroung() {
        return selectedBackgroung;
    }

    public String getSelectedTextColor() {
        return selectedTextColor;
    }

    public String getTextAlign() {
        return textAlign;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getTextSize() {
        return textSize;
    }

    public String getTypeface() {
        return typeface;
    }

    public boolean isNightMode() {
        return nightMode;
    }
}
