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
 * Created by Vladimir Yakushev at 9/2016
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */
package com.BibleQuote.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.BibleQuote.domain.entity.Chapter;
import com.BibleQuote.domain.entity.Verse;
import com.BibleQuote.domain.textFormatters.ITextFormatter;
import com.BibleQuote.domain.textFormatters.StripTagsTextFormatter;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.utils.PreferenceHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeSet;

@SuppressLint("SetJavaScriptEnabled")
public class ReaderWebView extends WebView
		implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    public static final int MIN_FLING_VELOCITY = 400;
    public static final int MIN_SWIPE_X = 100;
    public static final int MAX_SWIPE_Y = 200;

    public boolean mPageLoaded;

    private TreeSet<Integer> selectedVerse = new TreeSet<Integer>();
    private PreferenceHelper preferenceHelper;
    private GestureDetector mGestureScanner;
	private JavaScriptInterface jsInterface;
	private Mode currMode = Mode.Read;
    private boolean isNightMode;
    private String baseUrl;
    private String content;
    private int currVerse;
    private boolean isBible;
    private int minSwipeX = MIN_SWIPE_X;
    private int maxSwipeY = MAX_SWIPE_Y;
    private float minVelocity = MIN_FLING_VELOCITY;
    private ITextFormatter formatter = new StripTagsTextFormatter();
    private ViewHandler handler;

    @SuppressLint("AddJavascriptInterface")
    public ReaderWebView(Context mContext, AttributeSet attributeSet) {
		super(mContext, attributeSet);

        handler = new ViewHandler();
        if (!isInEditMode()) {
            WebSettings settings = getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setNeedInitialFocus(false);
            settings.setBuiltInZoomControls(false);
            settings.setSupportZoom(false);

            setFocusable(true);
            setFocusableInTouchMode(true);
            setWebViewClient(new webClient());
            setWebChromeClient(new ChromeClient());

            this.jsInterface = new JavaScriptInterface();
            addJavascriptInterface(this.jsInterface, "reader");

            setVerticalScrollbarOverlay(true);

            mGestureScanner = new GestureDetector(mContext, this);
            mGestureScanner.setIsLongpressEnabled(true);
            mGestureScanner.setOnDoubleTapListener(this);
            preferenceHelper = PreferenceHelper.getInstance();
        }
    }

    public Mode getMode() {
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

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
    }

    public void setOnReaderViewListener(IReaderViewListener listener) {
        handler.setListener(listener);
    }

	public void setSelectedVerse(TreeSet<Integer> selectedVerse) {
		jsInterface.clearSelectedVerse();
		this.selectedVerse = selectedVerse;
		for (Integer verse : selectedVerse) {
			jsInterface.selectVerse(verse);
		}
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
        if (mPageLoaded && isScrollToBottom()) {
            handler.sendEmptyMessage(ViewHandler.MSG_ON_COMPUTE_SCROLL);
        }
        loadUrl("javascript: getCurrentVerse();");
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
        notifyListeners(IReaderViewListener.ChangeCode.onScroll);
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

    public void setContent(String baseUrl, Chapter chapter, int currVerse, Boolean isNightMode, Boolean isBible) {
        this.baseUrl = baseUrl;
        this.content = getContent(chapter);
        this.currVerse = currVerse;
        this.isNightMode = isNightMode;
        this.isBible = isBible;
        update();
    }

    public void update() {
        if (baseUrl == null || content == null) {
            return;
        }

        mPageLoaded = false;
        String modStyle = isBible ? "bible_style.css" : "book_style.css";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n")
                .append("<html>\r\n")
                .append("<head>\r\n")
                .append("<meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">\r\n")
                .append("<script language=\"JavaScript\" src=\"file:///android_asset/reader.js\" type=\"text/javascript\"></script>\r\n")
                .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/").append(modStyle).append("\">\r\n")
                .append(getStyle(isNightMode))
                .append("</head>\r\n")
                .append("<body").append(currVerse > 1 ? (" onLoad=\"document.location.href='#verse_" + currVerse + "';\"") : "").append(">\r\n")
                .append(content)
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

	private String getStyle(Boolean nightMode) {
		String textColor;
		String backColor;
		String selTextColor;
		String selTextBack;

        getSettings().setStandardFontFamily(preferenceHelper.getFontFamily());

		if (!nightMode) {
            backColor = preferenceHelper.getTextBackground();
            textColor = preferenceHelper.getTextColor();
            selTextColor = preferenceHelper.getTextColorSelected();
            selTextBack = preferenceHelper.getTextBackgroundSelected();
        } else {
			textColor = "#EEEEEE";
			backColor = "#000000";
			selTextColor = "#EEEEEE";
			selTextBack = "#562000";
		}
        String textSize = preferenceHelper.getTextSize();

		StringBuilder style = new StringBuilder();
		style.append("<style type=\"text/css\">\r\n")
				.append("body {\r\n")
				.append("padding-bottom: 50px;\r\n");
        if (preferenceHelper.textAlignJustify()) {
            style.append("text-align: justify;\r\n");
		}
		style.append("color: ").append(textColor).append(";\r\n")
				.append("font-size: ").append(textSize).append("pt;\r\n")
				.append("line-height: 1.25;\r\n")
				.append("background: ").append(backColor).append(";\r\n")
				.append("}\r\n")
				.append(".verse {\r\n")
				.append("background: ").append(backColor).append(";\r\n")
				.append("}\r\n")
				.append(".selectedVerse {\r\n")
				.append("color: ").append(selTextColor).append(";\r\n")
				.append("background: ").append(selTextBack).append(";\r\n")
				.append("}\r\n")
				.append("img {\r\n")
				.append("max-width: 100%;\r\n")
				.append("}\r\n")
				.append("</style>\r\n");

		return style.toString();
	}

    private void notifyListeners(IReaderViewListener.ChangeCode code, Object... values) {
        Message msg;
        if (code != IReaderViewListener.ChangeCode.onChangeCurrentVerse || values.length == 0) {
            msg = Message.obtain(handler, ViewHandler.MSG_OTHER, code);
        } else {
            msg = Message.obtain(handler, ViewHandler.MSG_CHANGE_CURRENT_VERSE, code);
            msg.arg1 = (Integer) values[0];
        }
        handler.sendMessage(msg);
    }

	public enum Mode {
		Read, Study, Speak
	}

    private static final class ChromeClient extends WebChromeClient {
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            if (result != null)
                result.confirm();
            return true;
        }
	}

    private static class ViewHandler extends Handler {

        public static final int MSG_ON_COMPUTE_SCROLL = 1;
        public static final int MSG_ON_CLICK_IMAGE = 2;
        public static final int MSG_CHANGE_CURRENT_VERSE = 3;
        public static final int MSG_OTHER = 4;

        private WeakReference<IReaderViewListener> weakListener;

        public void setListener(IReaderViewListener listener) {
            this.weakListener = new WeakReference<IReaderViewListener>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            IReaderViewListener listener = weakListener.get();
            if (listener == null) {
                return;
            }

            switch (msg.what) {
                case MSG_ON_COMPUTE_SCROLL:
                    listener.onReaderViewChange(IReaderViewListener.ChangeCode.onScroll);
                    break;
                case MSG_ON_CLICK_IMAGE:
                    listener.onReaderClickImage((String) msg.obj);
                    break;
                case MSG_CHANGE_CURRENT_VERSE:
                    listener.onReaderViewChange(IReaderViewListener.ChangeCode.onChangeCurrentVerse, msg.arg1);
                    break;
                default:
                    listener.onReaderViewChange((IReaderViewListener.ChangeCode) msg.obj);
            }
            super.handleMessage(msg);
        }
    }

    private final class webClient extends WebViewClient {
        webClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        public void onPageFinished(WebView paramWebView, String paramString) {
            super.onPageFinished(paramWebView, paramString);
            mPageLoaded = true;
            handler.sendMessage(Message.obtain(handler, ViewHandler.MSG_OTHER, IReaderViewListener.ChangeCode.onUpdateText));
        }
    }

    private final class JavaScriptInterface {

		public JavaScriptInterface() {
			clearSelectedVerse();
		}

        @JavascriptInterface
        public void setCurrentVerse(String id) {
            if (id.matches("verse_\\d+?")) {
                try {
                    currVerse = Integer.parseInt(id.split("_")[1]);
                    final Message msg = Message.obtain(handler, ViewHandler.MSG_CHANGE_CURRENT_VERSE, IReaderViewListener.ChangeCode.onChangeSelection);
                    msg.arg1 = currVerse;
                    handler.sendMessage(msg);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }

        @JavascriptInterface
		public void clearSelectedVerse() {
            for (Integer verse : selectedVerse) {
                deselectVerse(verse);
            }
			selectedVerse.clear();
		}

        public void gotoVerse(final int verse) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: gotoVerse(" + verse + ");");
                }
            });
        }

        @JavascriptInterface
        public void onClickImage(String path) {
            Message msg = new Message();
            msg.what = ViewHandler.MSG_ON_CLICK_IMAGE;
            msg.obj = path;
            handler.sendMessage(msg);
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
                final Message msg = Message.obtain(handler, ViewHandler.MSG_OTHER, IReaderViewListener.ChangeCode.onChangeSelection);
                handler.sendMessage(msg);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
		}

        private void deselectVerse(final Integer verse) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: deselectVerse('verse_" + verse + "');");
                }
            });
        }

        private void selectVerse(final int verse) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: selectVerse('verse_" + verse + "');");
                }
            });
		}
	}
}
