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
 * File: DevicesKeyCodes.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.utils;

import android.view.KeyEvent;

public final class DevicesKeyCodes {

	// additional key codes for Nook
	private static final int NOOK_KEY_PREV_LEFT = 96;
	private static final int NOOK_KEY_PREV_RIGHT = 98;
	private static final int NOOK_KEY_NEXT_RIGHT = 97;
	private static final int NOOK_KEY_SHIFT_UP = 101;
	private static final int NOOK_KEY_SHIFT_DOWN = 100;

	// nook 1 & 2
	private static final int NOOK_12_KEY_NEXT_LEFT = 95;

	// Nook touch buttons
	private static final int KEYCODE_PAGE_BOTTOMLEFT = 0x5d; // fwd = 93 (
	private static final int KEYCODE_PAGE_BOTTOMRIGHT = 158; // 0x5f; // fwd = 95
	private static final int KEYCODE_PAGE_TOPLEFT = 0x5c; // back = 92
	private static final int KEYCODE_PAGE_TOPRIGHT = 0x5e; // back = 94

	// Sony eReader 
	private static final int SONY_DPAD_UP_SCANCODE = 105;
	private static final int SONY_DPAD_DOWN_SCANCODE = 106;

	private DevicesKeyCodes() {
	}

	public static boolean keyCodeUp(int keyCode) {
		return keyCode == KeyEvent.KEYCODE_DPAD_UP
				|| keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				|| keyCode == KeyEvent.KEYCODE_2
				|| keyCode == NOOK_KEY_PREV_LEFT
				|| keyCode == NOOK_KEY_PREV_RIGHT
				|| keyCode == NOOK_KEY_SHIFT_UP
				|| keyCode == KEYCODE_PAGE_BOTTOMLEFT
				|| keyCode == KEYCODE_PAGE_BOTTOMRIGHT
				|| keyCode == SONY_DPAD_UP_SCANCODE;
	}

	public static boolean keyCodeDown(int keyCode) {
		return keyCode == KeyEvent.KEYCODE_DPAD_DOWN
				|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
				|| keyCode == KeyEvent.KEYCODE_8
				|| keyCode == NOOK_KEY_NEXT_RIGHT
				|| keyCode == NOOK_KEY_SHIFT_DOWN
				|| keyCode == NOOK_12_KEY_NEXT_LEFT
				|| keyCode == KEYCODE_PAGE_TOPLEFT
				|| keyCode == KEYCODE_PAGE_TOPRIGHT
				|| keyCode == SONY_DPAD_DOWN_SCANCODE;
	}
}
