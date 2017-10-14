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
 * File: ReaderWebView.java
 *
 * Created by Vladimir Yakushev at 10/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.presentation.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Verse;
import com.BibleQuote.domain.textFormatters.ITextFormatter;
import com.BibleQuote.domain.textFormatters.StripTagsTextFormatter;
import com.BibleQuote.entity.TextAppearance;
import com.BibleQuote.presentation.ui.reader.IReaderViewListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeSet;

@SuppressLint("SetJavaScriptEnabled")
public class ReaderWebView extends WebView
        implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    public static final String TAG = ReaderWebView.class.getSimpleName();
    public static final int MIN_FLING_VELOCITY = 400;
    public static final int MIN_SWIPE_X = 100;
    public static final int MAX_SWIPE_Y = 200;

    public boolean mPageLoaded;
    private String baseUrl;
    private String content;
    private Mode currMode = Mode.Read;
    private int currVerse;
    private ITextFormatter formatter = new StripTagsTextFormatter();
    private boolean isBible;
    private JavaScriptInterface jsInterface;
    private GestureDetector mGestureScanner;
    private int maxSwipeY = MAX_SWIPE_Y;
    private int minSwipeX = MIN_SWIPE_X;
    private float minVelocity = MIN_FLING_VELOCITY;
    private TreeSet<Integer> selectedVerse = new TreeSet<>();
    private ReaderTaskHandler taskHandler;
    private TextAppearance textAppearance;
    private ViewHandler viewHandler;

    @SuppressLint("AddJavascriptInterface")
    public ReaderWebView(Context mContext, AttributeSet attributeSet) {
        super(mContext, attributeSet);

        viewHandler = new ViewHandler();
        taskHandler = new ReaderTaskHandler(this);
        if (!isInEditMode()) {
            getSettings().setJavaScriptEnabled(true);
            setWebViewClient(new webClient());
            setWebChromeClient(new ChromeClient());

            this.jsInterface = new JavaScriptInterface();
            addJavascriptInterface(this.jsInterface, "reader");

            setVerticalScrollbarOverlay(true);

            mGestureScanner = new GestureDetector(mContext, this);
            mGestureScanner.setIsLongpressEnabled(true);
            mGestureScanner.setOnDoubleTapListener(this);
        }
    }

    public int getCurrVerse() {
        return currVerse;
    }

    public Mode getReaderMode() {
        return currMode;
    }

    public TreeSet<Integer> getSelectedVerses() {
        return this.selectedVerse;
    }

    public boolean isScrollToBottom() {
        int scrollPos = getScrollY() + computeVerticalScrollExtent();
        final int scrollRange = computeVerticalScrollRange();
        return (scrollPos >= (scrollRange - 10));
    }

    public void setFormatter(@NonNull ITextFormatter formatter) {
        this.formatter = formatter;
    }

    public void setMode(Mode mode) {
        currMode = mode;
        if (currMode != Mode.Study) {
            clearSelectedVerse();
        }
        notifyListeners(IReaderViewListener.ChangeCode.onChangeReaderMode);
    }

    public void setOnReaderViewListener(IReaderViewListener listener) {
        viewHandler.setListener(listener);
    }

    public void setSelectedVerse(TreeSet<Integer> selectedVerse) {
        jsInterface.clearSelectedVerse();
        this.selectedVerse = selectedVerse;
        for (Integer verse : selectedVerse) {
            jsInterface.selectVerse(verse);
        }
    }

    public void setTextApearance(TextAppearance textApearence) {
        this.textAppearance = textApearence;
        update();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        minSwipeX = metrics.widthPixels / 3;
        maxSwipeY = metrics.heightPixels / 3;
        minVelocity = MIN_FLING_VELOCITY * metrics.density;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mPageLoaded) {
            taskHandler.addSingleDelayedMessage(ReaderTaskHandler.MSG_HANDLE_SCROLL, 100);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureScanner.onTouchEvent(event) || (event != null && super.onTouchEvent(event));
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mPageLoaded) {
            notifyListeners(IReaderViewListener.ChangeCode.onScroll);
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (currMode == Mode.Study) {
            float density = getContext().getResources().getDisplayMetrics().density;
            x = (int) (x / density);
            y = (int) (y / density);

            loadUrl("javascript:handleClick(" + x + ", " + y + ");");
            notifyListeners(IReaderViewListener.ChangeCode.onChangeSelection);
        } else if (currMode == Mode.Read) {
            int width = this.getWidth();
            int height = this.getHeight();

            if (((float) y / height) <= 0.33) {
                notifyListeners(IReaderViewListener.ChangeCode.onUpNavigation);
            } else if (((float) y / height) > 0.67) {
                notifyListeners(IReaderViewListener.ChangeCode.onDownNavigation);
            } else if (((float) x / width) <= 0.33) {
                notifyListeners(IReaderViewListener.ChangeCode.onLeftNavigation);
            } else if (((float) x / width) > 0.67) {
                notifyListeners(IReaderViewListener.ChangeCode.onRightNavigation);
            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float dx = e1.getX() - e2.getX();
        float dy = e1.getY() - e2.getY();
        if (Math.abs(dy) < maxSwipeY && Math.abs(dx) > minSwipeX
                && Math.abs(velocityX) > minVelocity) {
            if (dx < 0) {
                notifyListeners(IReaderViewListener.ChangeCode.onLeftNavigation);
            } else {
                notifyListeners(IReaderViewListener.ChangeCode.onRightNavigation);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        notifyListeners(IReaderViewListener.ChangeCode.onLongPress);
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        if (currMode != Mode.Speak) {
            setMode(currMode == Mode.Study ? Mode.Read : Mode.Study);
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    public void clearSelectedVerse() {
        if (selectedVerse.size() == 0) {
            return;
        }
        jsInterface.clearSelectedVerse();
        if (currMode == Mode.Study) {
            notifyListeners(IReaderViewListener.ChangeCode.onChangeSelection);
        }
    }

    public void gotoVerse(int verse) {
        jsInterface.gotoVerse(verse);
    }

    public void setContent(String baseUrl, Chapter chapter, int currVerse, Boolean isBible) {
        this.baseUrl = baseUrl;
        this.content = getContent(chapter);
        this.currVerse = currVerse;
        this.isBible = isBible;
        update();
    }

    public void update() {
        Log.d(TAG, "update");
        mPageLoaded = false;
        taskHandler.removeMessages(ReaderTaskHandler.MSG_HANDLE_SCROLL);

        String modStyle = isBible ? "bible_style.css" : "book_style.css";
        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder html = new StringBuilder()
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n")
                .append("<html>\r\n")
                .append("<head>\r\n")
                .append("<meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">\r\n")
                .append("<script language=\"JavaScript\" src=\"file:///android_asset/reader.js\" type=\"text/javascript\"></script>\r\n")
                .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/")
                .append(modStyle)
                .append("\">\r\n")
                .append(getStyle())
                .append("</head>\r\n")
                .append("<body>\r\n")
                .append(content == null ? "" : content)
                .append("</body>\r\n")
                .append("</html>");

        loadDataWithBaseURL("file://" + baseUrl, html.toString(), "text/html", "UTF-8", "about:config");
        jsInterface.clearSelectedVerse();
    }

    private String getContent(Chapter chapter) {
        if (chapter == null) {
            return "";
        }

        ArrayList<Verse> verses = chapter.getVerseList();
        StringBuilder chapterHTML = new StringBuilder();
        for (int verse = 1; verse <= verses.size(); verse++) {
            String verseText = formatter.format(verses.get(verse - 1).getText());
            chapterHTML.append("<div id=\"verse_").append(verse).append("\" class=\"verse\">")
                    .append(verseText.replaceAll("<(/)*div(.*?)>", "<$1p$2>"))
                    .append("</div>")
                    .append("\r\n");
        }

        return chapterHTML.toString();
    }

    private String getStyle() {
        String textColor;
        String backColor;
        String selTextColor;
        String selTextBack;

        getSettings().setStandardFontFamily(textAppearance.getTypeface());

        if (textAppearance.isNightMode()) {
            textColor = "#EEEEEE";
            backColor = "#000000";
            selTextColor = "#EEEEEE";
            selTextBack = "#562000";
        } else {
            backColor = textAppearance.getBackground();
            textColor = textAppearance.getTextColor();
            selTextColor = textAppearance.getSelectedTextColor();
            selTextBack = textAppearance.getSelectedBackgroung();
        }
        String textSize = textAppearance.getTextSize();
        int lineSpacing = textAppearance.getLineSpacing();

        return "<style type=\"text/css\">\r\n" +
                "body {\r\n" +
                "padding: 5px 5px 50px;\r\n" +
                "text-align: " + textAppearance.getTextAlign() + ";\r\n" +
                "color: " + textColor + ";\r\n" +
                "font-size: " + textSize + "pt;\r\n" +
                "line-height: " + lineSpacing + "%;\r\n" +
                "background: " + backColor + ";\r\n" +
                "}\r\n" +
                ".verse {\r\n" +
                "background: " + backColor + ";\r\n" +
                "}\r\n" +
                ".selectedVerse {\r\n" +
                "color: " + selTextColor + ";\r\n" +
                "background: " + selTextBack + ";\r\n" +
                "}\r\n" +
                "img {\r\n" +
                "max-width: 100%;\r\n" +
                "}\r\n" +
                "</style>\r\n";
    }

    private void notifyListeners(IReaderViewListener.ChangeCode code) {
        Message msg = Message.obtain(viewHandler, ViewHandler.MSG_OTHER, code);
        viewHandler.sendMessage(msg);
    }

    private void onScrollComplete() {
        Log.d(TAG, "onScrollComplete");
        if (isScrollToBottom()) {
            notifyListeners(IReaderViewListener.ChangeCode.onScroll);
        }
        loadUrl("javascript: getCurrentVerse();");
    }

    public enum Mode {
        Read, Study, Speak
    }

    private static final class ChromeClient extends WebChromeClient {

        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            if (result != null) {
                result.confirm();
            }
            return true;
        }
    }

    private static class ReaderTaskHandler extends Handler {

        private static final int MSG_HANDLE_SCROLL = 1;

        private WeakReference<ReaderWebView> reader;

        ReaderTaskHandler(ReaderWebView readerWebView) {
            this.reader = new WeakReference<>(readerWebView);
        }

        @Override
        public void handleMessage(Message msg) {
            ReaderWebView readerWebView = reader.get();
            if (readerWebView == null) {
                return;
            }

            switch (msg.what) {
                case MSG_HANDLE_SCROLL:
                    readerWebView.onScrollComplete();
                    break;
                default:
                    // nothing
            }
            super.handleMessage(msg);
        }

        void addSingleDelayedMessage(int what, long delay) {
            removeMessages(what);
            sendEmptyMessageDelayed(what, delay);
        }
    }

    private final class webClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            Log.d(TAG, "onPageFinished");
            if (!mPageLoaded) {
                if (currVerse == 1) {
                    loadUrl("javascript: window.scrollTo(0, 0);");
                } else {
                    gotoVerse(currVerse);
                }
            }
            mPageLoaded = true;
            viewHandler.sendMessage(Message.obtain(viewHandler, ViewHandler.MSG_OTHER,
                    IReaderViewListener.ChangeCode.onUpdateText));
        }
    }

    private final class JavaScriptInterface {

        JavaScriptInterface() {
            clearSelectedVerse();
        }

        @JavascriptInterface
        public void setCurrentVerse(String id) {
            Log.d(TAG, "setCurrentVerse " + id);
            if (id.matches("verse_\\d+?")) {
                try {
                    currVerse = Integer.parseInt(id.split("_")[1]);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }

        @JavascriptInterface
        public void onClickImage(String path) {
            Message msg = new Message();
            msg.what = ViewHandler.MSG_ON_CLICK_IMAGE;
            msg.obj = path;
            viewHandler.sendMessage(msg);
        }

        @JavascriptInterface
        public void onClickVerse(String id) {
            if (currMode != Mode.Study || !id.contains("verse")) {
                return;
            }

            try {
                Integer verse = Integer.parseInt(id.split("_")[1]);
                if (selectedVerse.contains(verse)) {
                    selectedVerse.remove(verse);
                    deselectVerse(verse);
                } else {
                    selectedVerse.add(verse);
                    selectVerse(verse);
                }
                final Message msg = Message.obtain(viewHandler, ViewHandler.MSG_OTHER, IReaderViewListener.ChangeCode.onChangeSelection);
                viewHandler.sendMessage(msg);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        private void clearSelectedVerse() {
            for (Integer verse : selectedVerse) {
                deselectVerse(verse);
            }
            selectedVerse.clear();
        }

        private void deselectVerse(final Integer verse) {
            viewHandler.post(() -> loadUrl("javascript: deselectVerse('verse_" + verse + "');"));
        }

        private void gotoVerse(final int verse) {
            Log.d(TAG, "gotoVerse " + verse);
            viewHandler.post(() -> loadUrl("javascript: gotoVerse(" + verse + ");"));
        }

        private void selectVerse(final int verse) {
            viewHandler.post(() -> loadUrl("javascript: selectVerse('verse_" + verse + "');"));
        }
    }
}
