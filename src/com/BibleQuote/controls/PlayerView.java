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

package com.BibleQuote.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.BibleQuote.R;

import java.util.ArrayList;

/**
 * User: Vladimir
 * Date: 25.01.13
 * Time: 0:19
 */
public class PlayerView extends LinearLayout {

    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public enum Event {
        PreviousClick,
        StopClick,
        PlayClick,
        PauseClick,
        ReplayClick,
        NextClick
    }

    public interface OnClickListener {
        public void onClick(Event ev);
    }

    private ArrayList<OnClickListener> listeners = new ArrayList<OnClickListener>();

    private void sendMessage(Event ev) {
        for (OnClickListener listener : listeners) {
            listener.onClick(ev);
        }
    }

    public void setOnClickListener (PlayerView.OnClickListener listener) {
        listeners.add(listener);
    }

    private void init() {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.player_view, this, true);

        ImageButton replay = (ImageButton) findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Event.ReplayClick);
            }
        });

        ImageButton previous = (ImageButton) findViewById(R.id.previous);
        previous.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Event.PreviousClick);
            }
        });

        ImageButton play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Event.PlayClick);
            }
        });

        ImageButton pause = (ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Event.PauseClick);
            }
        });

        ImageButton next = (ImageButton) findViewById(R.id.next);
        next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Event.NextClick);
            }
        });

        ImageButton stop = (ImageButton) findViewById(R.id.stop);
        stop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Event.StopClick);
            }
        });
    }
}
