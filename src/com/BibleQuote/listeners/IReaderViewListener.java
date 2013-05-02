package com.BibleQuote.listeners;

public interface IReaderViewListener {
	public static enum ChangeCode {
		onUpdateText,
		onChangeSelection,
		onLongPress,
		onScroll,
		onChangeReaderMode,
		onUpNavigation,
		onDownNavigation,
		onLeftNavigation,
		onRightNavigation
	}

	public void onReaderViewChange(ChangeCode code);
}
