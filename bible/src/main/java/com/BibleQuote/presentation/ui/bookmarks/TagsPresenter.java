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
 * File: TagsPresenter.java
 *
 * Created by Vladimir Yakushev at 11/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.presentation.ui.bookmarks;

import com.BibleQuote.domain.entity.TagWithCount;
import com.BibleQuote.managers.tags.TagsManager;
import com.BibleQuote.presentation.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class TagsPresenter extends BasePresenter<TagsView> {

    private OnTagsChangeListener changeListener;
    private List<TagWithCount> tags;
    private TagsManager tagsManager;

    @Inject
    TagsPresenter(TagsManager tagsManager) {
        this.tagsManager = tagsManager;
    }

    void setChangeListener(OnTagsChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onViewCreated() {
        if (changeListener == null) {
            throw new IllegalStateException("OnTagsChangeListener is not specified");
        }
        refreshTags();
    }

    void onDeleteTag(int pos) {
        if (pos < tags.size()) {
            TagWithCount tagWithCount = tags.get(pos);
            tagsManager.delete(tagWithCount.tag());
            refreshTags();
            changeListener.onTagsUpdate();
        }
    }

    void onTagSelected(int pos) {
        if (pos < tags.size()) {
            changeListener.onTagSelect(tags.get(pos).tag());
        }
    }

    void refreshTags() {
        tags = tagsManager.getAllWithCount();
        getView().updateTags(tags);
    }
}
