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
 * File: ChapterNavigator.java
 *
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.BibleQuote.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class ChapterNavigator extends LinearLayout {

    @BindView(R.id.btn_prev) ImageButton btnPreview;
    @BindView(R.id.btn_next) ImageButton btnNext;
    @BindView(R.id.btn_up) ImageButton btnUp;
    @BindView(R.id.btn_down) ImageButton btnDown;

    private OnClickListener listener;

    public ChapterNavigator(Context context) {
        super(context);
        init(context);
    }

    public ChapterNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChapterNavigator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChapterNavigator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @OnClick({R.id.btn_prev, R.id.btn_next, R.id.btn_down, R.id.btn_up})
    void OnClickButton(View button) {
        if (listener == null) {
            return;
        }
        if (button == btnPreview) {
            listener.onClick(ClickedButton.PREV);
        } else if (button == btnNext) {
            listener.onClick(ClickedButton.NEXT);
        } else if (button == btnDown) {
            listener.onClick(ClickedButton.DOWN);
        } else if (button == btnUp) {
            listener.onClick(ClickedButton.UP);
        }
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_chapter_navigator, this);
        ButterKnife.bind(this);
    }

    public enum ClickedButton {PREV, NEXT, UP, DOWN}

    public interface OnClickListener {
        void onClick(ClickedButton btn);
    }
}
