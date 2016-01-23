package com.BibleQuote.listeners;

public interface IReaderViewListener {
	enum ChangeCode {
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

	void onReaderViewChange(ChangeCode code);
}
