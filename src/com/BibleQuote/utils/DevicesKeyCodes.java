package com.BibleQuote.utils;

import android.view.KeyEvent;

public class DevicesKeyCodes {

	// additional key codes for Nook
	public static final int NOOK_KEY_PREV_LEFT = 96;
	public static final int NOOK_KEY_PREV_RIGHT = 98;
	public static final int NOOK_KEY_NEXT_RIGHT = 97;
	public static final int NOOK_KEY_SHIFT_UP = 101;
	public static final int NOOK_KEY_SHIFT_DOWN = 100;

	// nook 1 & 2
	public static final int NOOK_12_KEY_NEXT_LEFT = 95;

	// Nook touch buttons
	public static final int KEYCODE_PAGE_BOTTOMLEFT = 0x5d; // fwd = 93 (
	public static final int KEYCODE_PAGE_BOTTOMRIGHT = 158; // 0x5f; // fwd = 95
	public static final int KEYCODE_PAGE_TOPLEFT = 0x5c; // back = 92
	public static final int KEYCODE_PAGE_TOPRIGHT = 0x5e; // back = 94

	// Sony eReader 
	public static final int SONY_DPAD_UP_SCANCODE = 105;
	public static final int SONY_DPAD_DOWN_SCANCODE = 106;
	public static final int SONY_DPAD_LEFT_SCANCODE = 125;
	public static final int SONY_DPAD_RIGHT_SCANCODE = 126;

	private DevicesKeyCodes() {
	}

	public static boolean KeyCodeUp(int keyCode) {
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

	public static boolean KeyCodeDown(int keyCode) {
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
