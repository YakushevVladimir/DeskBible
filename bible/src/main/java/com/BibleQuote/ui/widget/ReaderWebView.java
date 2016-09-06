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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.TreeSet;

@SuppressLint("SetJavaScriptEnabled")
public class ReaderWebView extends WebView
		implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    public static final int MIN_SWIPE_VELOCITY = 2000;
    public static final int MIN_SWIPE_X = 100;
    public static final int MAX_SWIPE_Y = 200;

	private static final String TAG = "ReaderWebView";
    private final PreferenceHelper preferenceHelper;

    public boolean mPageLoaded;

    protected TreeSet<Integer> selectedVerse = new TreeSet<Integer>();

    private GestureDetector mGestureScanner;
	private JavaScriptInterface jsInterface;
	private Mode currMode = Mode.Read;
	private ArrayList<IReaderViewListener> listeners = new ArrayList<IReaderViewListener>();
    private boolean isNightMode;
    private String baseUrl;
    private String content;
    private int currVerse;
    private boolean isBible;
    private int minSwipeX = MIN_SWIPE_X;
    private int maxSwipeY = MAX_SWIPE_Y;
    private int minVelocity = MIN_SWIPE_VELOCITY;
    private ITextFormatter formatter = new StripTagsTextFormatter();

    @SuppressLint("AddJavascriptInterface")
    public ReaderWebView(Context mContext, AttributeSet attributeSet) {
		super(mContext, attributeSet);

		WebSettings settings = getSettings();
		settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setNeedInitialFocus(false);
        settings.setBuiltInZoomControls(false);
		settings.setSupportZoom(false);

		setFocusable(true);
		setFocusableInTouchMode(true);
		setWebViewClient(new webClient());
		setWebChromeClient(new chromeClient());

		this.jsInterface = new JavaScriptInterface();
		addJavascriptInterface(this.jsInterface, "reader");

		setVerticalScrollbarOverlay(true);

		mGestureScanner = new GestureDetector(mContext, this);
		mGestureScanner.setIsLongpressEnabled(true);
		mGestureScanner.setOnDoubleTapListener(this);
        preferenceHelper = PreferenceHelper.getInstance();
    }

    public Mode getMode() {
        return currMode;
    }

    public TreeSet<Integer> getSelectedVerses() {
        return this.selectedVerse;
    }

    public boolean isScrollToBottom() {
        int scrollY = getScrollY();
        int scrollExtent = computeVerticalScrollExtent();
        int scrollPos = scrollY + scrollExtent;
        return (scrollPos >= (computeVerticalScrollRange() - 10));
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
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
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
        minSwipeX = metrics.widthPixels / 2;
        maxSwipeY = metrics.heightPixels / 4;
        minVelocity = metrics.widthPixels * 2;
    }

    public void computeScroll() {
        super.computeScroll();
        if (mPageLoaded && isScrollToBottom()) {
            notifyListeners(IReaderViewListener.ChangeCode.onScroll);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mGestureScanner.onTouchEvent(event) || (event != null && super.onTouchEvent(event));
    }

    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

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

    public boolean onDown(MotionEvent event) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distX = e1.getX() - e2.getX();
        float distY = e1.getY() - e2.getY();
        if (Math.abs(distY) < maxSwipeY && Math.abs(distX) > minSwipeX
                && Math.abs(velocityX) > minVelocity) {
            if (distX < 0) {
                notifyListeners(IReaderViewListener.ChangeCode.onLeftNavigation);
            } else {
                notifyListeners(IReaderViewListener.ChangeCode.onRightNavigation);
            }
            return true;
        } else {
            Log.d(TAG, String.format("distX: %f, distY: %f, velocityX: %f", distX, distY, velocityX));
        }
        return false;
    }

    public void onLongPress(MotionEvent event) {
        notifyListeners(IReaderViewListener.ChangeCode.onLongPress);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        notifyListeners(IReaderViewListener.ChangeCode.onScroll);
        return false;
    }

    public void onShowPress(MotionEvent event) {
    }

    public boolean onDoubleTap(MotionEvent event) {
        if (currMode != Mode.Speak) {
            setMode(currMode == Mode.Study ? Mode.Read : Mode.Study);
        }
        return false;
    }

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

    private void notifyListeners(IReaderViewListener.ChangeCode code) {
        for (IReaderViewListener listener : listeners) {
            listener.onReaderViewChange(code);
        }
    }

    private void notifyListenersOnClickImage(String path) {
        for (IReaderViewListener listener : listeners) {
            listener.onReaderClickImage(path);
        }
	}

	public enum Mode {
		Read, Study, Speak
	}

	private final class webClient extends WebViewClient {
		webClient() {
		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(TAG, "shouldOverrideUrlLoading(" + url + ")");
			return true;
		}

		public void onPageFinished(WebView paramWebView, String paramString) {
			super.onPageFinished(paramWebView, paramString);
			mPageLoaded = true;
		}
	}

    private final class chromeClient extends WebChromeClient {
		chromeClient() {
		}

		public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
			Log.i(TAG, message);
			if (result != null)
				result.confirm();
			return true;
		}
	}

    private final class JavaScriptInterface {

		public JavaScriptInterface() {
			clearSelectedVerse();
		}

        public void alert(final String message) {
            Log.i(TAG, "JavaScriptInterface.alert()");
        }

        @JavascriptInterface
		public void clearSelectedVerse() {
            for (Integer verse : selectedVerse) {
                deselectVerse(verse);
            }
			selectedVerse.clear();
		}

        public void gotoVerse(int verse) {
            loadUrl("javascript: gotoVerse(" + verse + ");");
        }

        @JavascriptInterface
        public void onClickImage(String path) {
            Log.d(TAG, "OnClickImage: " + path);
            notifyListenersOnClickImage(path);
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

                try {
                    Handler mHandler = getHandler();
                    mHandler.post(new Runnable() {
                        public void run() {
                            notifyListeners(IReaderViewListener.ChangeCode.onChangeSelection);
                        }
                    });
                } catch (NullPointerException e) {
                    Log.e(TAG, "Error when notifying clients ReaderWebView");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
		}

        private void deselectVerse(final Integer verse) {
            ReaderWebView.this.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: deselectVerse('verse_" + verse + "');");
                }
            });
        }

        private void selectVerse(final int verse) {
            ReaderWebView.this.post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript: selectVerse('verse_" + verse + "');");
                }
            });
		}
	}
}
