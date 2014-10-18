/*
 * Copyright (C) 2011 Scripture Software (http://scripturesoftware.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.BibleQuote.ui.base;

import android.support.v7.app.ActionBarActivity;
import com.BibleQuote.managers.GoogleAnalyticsHelper;

/**
 * @author Vladimir Yakushev
 * @version 1.0
 */
public class BibleQuoteActivity extends ActionBarActivity {
    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalyticsHelper.getInstance(this).startActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalyticsHelper.getInstance(this).stopActivity(this);
    }
}
