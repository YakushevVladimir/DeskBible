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

package com.BibleQuote.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.BibleQuote.R;
import com.BibleQuote.activity.ReaderActivity;
import com.BibleQuote.managers.Librarian;
import com.BibleQuote.tts.controllers.TTSPlayerController;
import com.BibleQuote.widget.PlayerView;
import com.BibleQuote.widget.ReaderWebView;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.TreeSet;

/**
 * User: Vladimir
 * Date: 25.01.13
 * Time: 2:40
 */
public class TTSPlayerFragment extends SherlockFragment implements PlayerView.OnClickListener, TTSPlayerController.OnEventListener {

	TTSPlayerController ttsController;
	ReaderWebView webView;

	onTTSStopSpeakListener listener;
	private PlayerView player;

	public interface onTTSStopSpeakListener {
		public void onStopSpeak();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (onTTSStopSpeakListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onTTSStopSpeakListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.tts_player_layout, null);

		player = (PlayerView) result.findViewById(R.id.tts_player);
		player.setOnClickListener(this);

		webView = (ReaderWebView) getActivity().findViewById(R.id.readerView);

		Librarian librarian = ((ReaderActivity) getActivity()).getLibrarian();
		ttsController = new TTSPlayerController(getActivity(), librarian.getTextLocale(), librarian.getVersesText());
		ttsController.setOnInitListener(this);

		return result;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ttsController.destroy();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			ttsController = null;
		}
	}

	@Override
	public void onClick(PlayerView.Event ev) {
		if (ev == PlayerView.Event.ReplayClick) {
			ttsController.replay();
		} else if (ev == PlayerView.Event.PreviousClick) {
			ttsController.movePrevious();
		} else if (ev == PlayerView.Event.PlayClick) {
			ttsController.play();
		} else if (ev == PlayerView.Event.PauseClick) {
			ttsController.pause();
		} else if (ev == PlayerView.Event.NextClick) {
			ttsController.moveNext();
		} else if (ev == PlayerView.Event.StopClick) {
			ttsController.stop();
			listener.onStopSpeak();
		} else {
			Toast.makeText(getActivity(), "Unknown command", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onEvent(TTSPlayerController.Event ev) {
		if (ev == TTSPlayerController.Event.Error) {
			Toast.makeText(getActivity(), ttsController.getError().getMessage(), Toast.LENGTH_LONG).show();
		} else if (ev == TTSPlayerController.Event.ChangeTextIndex) {
			int nextTextIndex = ttsController.getCurrText();
			TreeSet<Integer> selected = new TreeSet<Integer>();
			selected.add((Integer.valueOf(++nextTextIndex)));
			webView.setSelectedVerse(selected);
			webView.gotoVerse(nextTextIndex);
		} else if (ev == TTSPlayerController.Event.PauseSpeak) {
			TreeSet<Integer> selected = new TreeSet<Integer>();
			webView.setSelectedVerse(selected);
			player.viewPlayButton();
		}
	}
}
