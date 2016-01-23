package com.BibleQuote.ui.handlers;

import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 01.2016
 */
public class ReaderHandler extends Handler {

    public static final int HIDE_NAV = 0;

    private View btnChapterNav;

    public ReaderHandler(View btnChapterNav) {
        super();
        this.btnChapterNav = btnChapterNav;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HIDE_NAV:
                btnChapterNav.setVisibility(View.GONE);
                break;
        }
        super.handleMessage(msg);
    }
}
