package com.BibleQuote.utils.textFormatters;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 11.2015
 */
public class NoStrongTextFormatter implements ITextFormatter {
    @Override
    public String format(String text) {
        return text.replaceAll("((\\s(G|H)+?\\d+)|(\\s\\d+))", "");
    }
}
