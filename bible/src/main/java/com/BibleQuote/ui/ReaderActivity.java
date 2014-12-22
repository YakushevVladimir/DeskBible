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

package com.BibleQuote.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ActionMode;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.BibleQuote.BibleQuoteApp;
import com.BibleQuote.R;
import com.BibleQuote.async.AsyncManager;
import com.BibleQuote.async.AsyncOpenChapter;
import com.BibleQuote.entity.BibleReference;
import com.BibleQuote.exceptions.BookNotFoundException;
import com.BibleQuote.exceptions.ExceptionHelper;
import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.listeners.IReaderViewListener;
import com.BibleQuote.managers.GoogleAnalyticsHelper;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.managers.bookmarks.Bookmark;
import com.BibleQuote.ui.base.BibleQuoteActivity;
import com.BibleQuote.ui.dialogs.BookmarksDialog;
import com.BibleQuote.ui.fragments.TTSPlayerFragment;
import com.BibleQuote.ui.widget.ReaderWebView;
import com.BibleQuote.utils.*;
import com.BibleQuote.utils.share.ShareBuilder.Destination;

import java.util.TreeSet;

public class ReaderActivity extends BibleQuoteActivity implements OnTaskCompleteListener, IReaderViewListener,
        TTSPlayerFragment.onTTSStopSpeakListener {

    public static final int ID_CHOOSE_CH = 1;
    public static final int ID_SEARCH = 2;
    public static final int ID_HISTORY = 3;
    public static final int ID_BOOKMARKS = 4;
    public static final int ID_PARALLELS = 5;
    public static final int ID_SETTINGS = 6;
    private static final String TAG = "ReaderActivity";
    private static final int VIEW_CHAPTER_NAV_LENGHT = 3000;
    private static final String VIEW_REFERENCE = "org.scripturesoftware.intent.action.VIEW_REFERENCE";
    OnClickListener onClickChapterPrev = new OnClickListener() {
        public void onClick(View v) {
            prevChapter();
        }
    };
    OnClickListener onClickChapterNext = new OnClickListener() {
        public void onClick(View v) {
            nextChapter();
        }
    };
    OnClickListener onClickPageUp = new OnClickListener() {
        public void onClick(View v) {
            vWeb.pageUp(false);
            viewChapterNav();
        }
    };
    OnClickListener onClickPageDown = new OnClickListener() {
        public void onClick(View v) {
            vWeb.pageDown(false);
            viewChapterNav();
        }
    };
    private ReaderWebView.Mode oldMode;
    private Librarian myLibrarian;
    private AsyncManager mAsyncManager;
    private Task mTask;
    private ActionMode currActionMode;
    private String chapterInHTML = "";
    private boolean nightMode = false;
    private boolean exitToBackKey = false;
    private String progressMessage = "";
    private TextView vModuleName;
    private TextView vBookLink;
    private LinearLayout btnChapterNav;
    private ReaderWebView vWeb;
    private TTSPlayerFragment ttsPlayer;
    private Handler chapterNavHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.view_chapter_nav:
                    btnChapterNav.setVisibility(View.GONE);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public Librarian getLibrarian() {
        return myLibrarian;
    }

    @Override
    public void onStopSpeak() {
        hideTTSPlayer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        BibleQuoteApp app = (BibleQuoteApp) getApplication();
        myLibrarian = app.getLibrarian();

        mAsyncManager = app.getAsyncManager();
        mAsyncManager.handleRetainedTask(mTask, this);

        initialyzeViews();
        setCurrentOrientation();
        updateActivityMode();

        BibleReference osisLink;
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            osisLink = new BibleReference(intent.getData());
        } else {
            osisLink = new BibleReference(PreferenceHelper.restoreStateString("last_read"));
        }

        if (!myLibrarian.isOSISLinkValid(osisLink)) {
            onChooseChapterClick();
        } else {
            openChapterFromLink(osisLink);
        }
    }

    private void openChapterFromLink(BibleReference osisLink) {
        mTask = new AsyncOpenChapter(progressMessage, false, myLibrarian, osisLink);
        mAsyncManager.setupTask(mTask, this);
    }

    private void initialyzeViews() {
        btnChapterNav = (LinearLayout) findViewById(R.id.btn_chapter_nav);

        ImageButton btnChapterPrev = (ImageButton) findViewById(R.id.btn_reader_prev);
        btnChapterPrev.setOnClickListener(onClickChapterPrev);
        ImageButton btnChapterNext = (ImageButton) findViewById(R.id.btn_reader_next);
        btnChapterNext.setOnClickListener(onClickChapterNext);

        ImageButton btnChapterUp = (ImageButton) findViewById(R.id.btn_reader_up);
        btnChapterUp.setOnClickListener(onClickPageUp);
        ImageButton btnChapterDown = (ImageButton) findViewById(R.id.btn_reader_down);
        btnChapterDown.setOnClickListener(onClickPageDown);

        vModuleName = (TextView) findViewById(R.id.moduleName);
        vBookLink = (TextView) findViewById(R.id.linkBook);

        progressMessage = getResources().getString(R.string.messageLoad);
        nightMode = PreferenceHelper.restoreStateBoolean("nightMode");

        vWeb = (ReaderWebView) findViewById(R.id.readerView);
        vWeb.setOnReaderViewListener(this);
        vWeb.setMode(PreferenceHelper.isReadModeByDefault() ? ReaderWebView.Mode.Read : ReaderWebView.Mode.Study);

        setKeepScreen();
    }

    private void setCurrentOrientation() {
        if (!PreferenceHelper.restoreStateBoolean("DisableAutoScreenRotation")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            return;
        }

        Display display = getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            height = display.getHeight();
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }

        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_180:
                if (height > width)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_270:
                if (width > height)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default:
                if (height > width)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void setKeepScreen() {
        vWeb.setKeepScreenOn(PreferenceHelper.restoreStateBoolean("DisableTurnScreen"));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideTTSPlayer();
        switch (item.getItemId()) {
            case R.id.action_bar_chooseCh:
                onChooseChapterClick();
                break;
            case R.id.action_bar_search:
                Intent intentSearch = new Intent().setClass(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intentSearch, ID_SEARCH);
                break;
            case R.id.action_bar_bookmarks:
                Intent intentBookmarks = new Intent().setClass(getApplicationContext(), BookmarksActivity.class);
                startActivityForResult(intentBookmarks, ID_BOOKMARKS);
                break;
            case R.id.NightDayMode:
                nightMode = !nightMode;
                PreferenceHelper.saveStateBoolean("nightMode", nightMode);
                setTextInWebView();
                break;
            case R.id.action_bar_history:
                Intent intentHistory = new Intent().setClass(getApplicationContext(), HistoryActivity.class);
                startActivityForResult(intentHistory, ID_HISTORY);
                break;
            case R.id.action_speek:
                viewTTSPlayer();
                break;
            case R.id.Help:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.Settings:
                Intent intentSettings = new Intent().setClass(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intentSettings, ID_SETTINGS);
                break;
            case R.id.About:
                Intent intentAbout = new Intent().setClass(getApplicationContext(), AboutActivity.class);
                startActivity(intentAbout);
                break;
            default:
                return false;
        }
        return true;
    }

    private void viewTTSPlayer() {
        if (ttsPlayer != null) return;
        ttsPlayer = new TTSPlayerFragment();
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.tts_player_frame, ttsPlayer);
        tran.commit();
        oldMode = vWeb.getMode();
        vWeb.setMode(ReaderWebView.Mode.Speak);
    }

    private void hideTTSPlayer() {
        if (ttsPlayer == null) return;
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.remove(ttsPlayer);
        tran.commit();
        ttsPlayer = null;
        vWeb.setMode(oldMode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if ((requestCode == ID_BOOKMARKS)
                    || (requestCode == ID_SEARCH)
                    || (requestCode == ID_CHOOSE_CH)
                    || (requestCode == ID_PARALLELS)
                    || (requestCode == ID_HISTORY)) {
                Bundle extras = data.getExtras();
                BibleReference osisLink = new BibleReference(extras.getString("linkOSIS"));
                if (myLibrarian.isOSISLinkValid(osisLink)) {
                    openChapterFromLink(osisLink);
                    GoogleAnalyticsHelper.getInstance().actionOpenLink(osisLink, requestCode);
                }
            }
        } else if (requestCode == ID_SETTINGS) {
            vWeb.setMode(PreferenceHelper.isReadModeByDefault() ? ReaderWebView.Mode.Read : ReaderWebView.Mode.Study);
            updateActivityMode();
            setCurrentOrientation();
            setKeepScreen();
            openChapterFromLink(myLibrarian.getCurrentOSISLink());
        }
    }

    @Override
    public void onBackPressed() {
        if (exitToBackKey) {
            super.onBackPressed();
        } else {
            exitToBackKey = true;
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitToBackKey = false;
                }
            }, 3000);
        }
    }

    public void setTextInWebView() {
        BibleReference OSISLink = myLibrarian.getCurrentOSISLink();
        vWeb.setText(myLibrarian.getBaseUrl(), chapterInHTML, OSISLink.getFromVerse(), nightMode, myLibrarian.isBible());

        PreferenceHelper.saveStateString("last_read", OSISLink.getExtendedPath());

        vModuleName.setText(myLibrarian.getModuleName());
        vBookLink.setText(myLibrarian.getHumanBookLink());
        btnChapterNav.setVisibility(View.GONE);
    }

    public void onChooseChapterClick() {
        Intent intent = new Intent();
        intent.setClass(this, LibraryActivity.class);
        startActivityForResult(intent, ID_CHOOSE_CH);
    }

    public void prevChapter() {
        try {
            myLibrarian.prevChapter();
        } catch (OpenModuleException e) {
            Log.e(TAG, "prevChapter()", e);
        }
        viewCurrentChapter();
    }

    public void nextChapter() {
        try {
            myLibrarian.nextChapter();
        } catch (OpenModuleException e) {
            Log.e(TAG, "nextChapter()", e);
        }
        viewCurrentChapter();
    }

    private void viewCurrentChapter() {
        disableActionMode();
        openChapterFromLink(myLibrarian.getCurrentOSISLink());
    }

    public void viewChapterNav() {
        if (chapterNavHandler.hasMessages(R.id.view_chapter_nav)) {
            chapterNavHandler.removeMessages(R.id.view_chapter_nav);
        }

        if (vWeb.getMode() != ReaderWebView.Mode.Study) {
            btnChapterNav.setVisibility(View.GONE);
        } else {
            btnChapterNav.setVisibility(View.VISIBLE);
            if (!vWeb.isScrollToBottom()) {
                Message msg = new Message();
                msg.what = R.id.view_chapter_nav;
                chapterNavHandler.sendMessageDelayed(msg, VIEW_CHAPTER_NAV_LENGHT);
            }
        }
    }

    @Override
    public boolean onSearchRequested() {
        Intent intentSearch = new Intent().setClass(
                getApplicationContext(), SearchActivity.class);
        startActivityForResult(intentSearch, ID_SEARCH);
        return false;
    }

    public void updateActivityMode() {
        if (vWeb.getMode() == ReaderWebView.Mode.Read) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
        viewChapterNav();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP && PreferenceHelper.volumeButtonsToScroll())
                || DevicesKeyCodes.KeyCodeUp(keyCode)) {
            vWeb.pageUp(false);
            viewChapterNav();
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && PreferenceHelper.volumeButtonsToScroll())
                || DevicesKeyCodes.KeyCodeDown(keyCode)) {
            vWeb.pageDown(false);
            viewChapterNav();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentOrientation();
        setKeepScreen();
    }

    public void onTaskComplete(Task task) {
        if (task != null && !task.isCancelled()) {
            if (task instanceof AsyncOpenChapter) {
                AsyncOpenChapter t = ((AsyncOpenChapter) task);
                if (t.isSuccess()) {
                    chapterInHTML = myLibrarian.getChapterHTMLView();
                    setTextInWebView();
                } else {
                    Exception e = t.getException();
                    if (e instanceof OpenModuleException) {
                        ExceptionHelper.onOpenModuleException((OpenModuleException) e, this, TAG);
                    } else if (e instanceof BookNotFoundException) {
                        ExceptionHelper.onBookNotFoundException((BookNotFoundException) e, this, TAG);
                    } else {
                        ExceptionHelper.onException(e, this, TAG);
                    }
                }
            }
        }
    }

    @Override
    public void onReaderViewChange(ChangeCode code) {
        if (code == ChangeCode.onChangeReaderMode) {
            updateActivityMode();
        } else if (code == ChangeCode.onUpdateText
                || code == ChangeCode.onScroll) {
            viewChapterNav();
        } else if (code == ChangeCode.onChangeSelection) {
            TreeSet<Integer> selVerses = vWeb.getSelectedVerses();
            if (selVerses.size() == 0) {
                disableActionMode();
            } else if (currActionMode == null) {
                currActionMode = startSupportActionMode(new ActionSelectText());
            }
        } else if (code == ChangeCode.onLongPress) {
            viewChapterNav();
            if (vWeb.getMode() == ReaderWebView.Mode.Read) onChooseChapterClick();
        } else if (code == ChangeCode.onUpNavigation) {
            vWeb.pageUp(false);
        } else if (code == ChangeCode.onDownNavigation) {
            vWeb.pageDown(false);
        } else if (code == ChangeCode.onLeftNavigation) {
            prevChapter();
        } else if (code == ChangeCode.onRightNavigation) {
            nextChapter();
        }
    }

    private void disableActionMode() {
        if (currActionMode != null) {
            currActionMode.finish();
            currActionMode = null;
        }
    }

    private final class ActionSelectText implements ActionMode.Callback {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater infl = getMenuInflater();
            infl.inflate(R.menu.menu_action_text_select, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            TreeSet<Integer> selVerses = vWeb.getSelectedVerses();
            if (selVerses.size() == 0) {
                return true;
            }

            switch (item.getItemId()) {
                case R.id.action_bookmarks:
                    myLibrarian.setCurrentVerseNumber(selVerses.first());
                    DialogFragment bmDial = BookmarksDialog.newInstance(new Bookmark(myLibrarian.getCurrentOSISLink()));
                    bmDial.show(getSupportFragmentManager(), "bookmark");
                    break;

                case R.id.action_share:
                    myLibrarian.shareText(ReaderActivity.this, selVerses, Destination.ActionSend);
                    break;

                case R.id.action_copy:
                    myLibrarian.shareText(ReaderActivity.this, selVerses, Destination.Clipboard);
                    Toast.makeText(ReaderActivity.this, getString(R.string.added), Toast.LENGTH_LONG).show();
                    break;

                case R.id.action_references:
                    myLibrarian.setCurrentVerseNumber(selVerses.first());
                    Intent intParallels = new Intent(VIEW_REFERENCE);
                    intParallels.putExtra("linkOSIS", myLibrarian.getCurrentOSISLink().getPath());
                    startActivityForResult(intParallels, ID_PARALLELS);
                    break;

                default:
                    return false;
            }

            mode.finish();
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            vWeb.clearSelectedVerse();
            currActionMode = null;
        }
    }
}
