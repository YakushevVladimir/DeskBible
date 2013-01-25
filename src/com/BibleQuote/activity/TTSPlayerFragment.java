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

package com.BibleQuote.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.BibleQuote.R;
import com.BibleQuote.controls.PlayerView;
import com.actionbarsherlock.app.SherlockFragment;

/**
 * User: Vladimir
 * Date: 25.01.13
 * Time: 2:40
 */
public class TTSPlayerFragment extends SherlockFragment implements PlayerView.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.tts_player_layout, null);

        PlayerView player = (PlayerView) result.findViewById(R.id.tts_player);
        player.setOnClickListener(this);

        return result;
    }

    @Override
    public void onClick(PlayerView.Event ev) {
        String message;
        if (ev == PlayerView.Event.ReplayClick) {
            message = "Click replay";
        } else if (ev == PlayerView.Event.PreviousClick){
            message = "Click previous";
        } else if (ev == PlayerView.Event.PlayClick){
            message = "Click play";
        } else if (ev == PlayerView.Event.PauseClick){
            message = "Click pause";
        } else if (ev == PlayerView.Event.NextClick){
            message = "Click next";
        } else if (ev == PlayerView.Event.StopClick){
            message = "Click stop";
        } else {
            message = "Unknow command";
        }
        Toast.makeText(getSherlockActivity(), message, Toast.LENGTH_LONG).show();
    }
}
