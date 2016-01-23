package com.BibleQuote.utils.textFormatters;

import com.BibleQuote.modules.Module;

import java.util.ArrayList;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 11.2015
 */
public class ModuleTextFormatter implements ITextFormatter {

    private static final String VERSE_NUMBER_PATTERN = "(?m)^(<[^/]+?>)*?(\\d+)(</(.)+?>){0,1}?\\s+";

    private ArrayList<ITextFormatter> formatters = new ArrayList<ITextFormatter>();
    private boolean visibleVerseNumbers;

    public ModuleTextFormatter(Module module) {
        this.visibleVerseNumbers = module.isBible;
        if (module.containsStrong) {
            formatters.add(new NoStrongTextFormatter());
        }
        formatters.add(new StripTagsTextFormatter("<(?!" + module.HtmlFilter + ")(.)*?>"));
    }

    public ModuleTextFormatter(Module module, ITextFormatter formatter) {
        this.visibleVerseNumbers = module.isBible;
        if (module.containsStrong) {
            formatters.add(new NoStrongTextFormatter());
        }
        formatters.add(formatter);
    }

    public void setVisibleVerseNumbers(boolean visible) {
        this.visibleVerseNumbers = visible;
    }

    @Override
    public String format(String text) {
        for (ITextFormatter formatter : formatters) {
            text = formatter.format(text);
        }
        if (visibleVerseNumbers) {
            text = text.replaceAll(VERSE_NUMBER_PATTERN, "$1<b>$2</b>$3 ").replaceAll("null", "");
        } else {
            text = text.replaceAll(VERSE_NUMBER_PATTERN, "");
        }
        return text;
    }
}
