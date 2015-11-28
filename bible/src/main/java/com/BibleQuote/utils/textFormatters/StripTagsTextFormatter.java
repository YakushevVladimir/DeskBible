package com.BibleQuote.utils.textFormatters;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 11.2015
 */
public class StripTagsTextFormatter implements ITextFormatter {

    private String pattern;

    public StripTagsTextFormatter() {
        this.pattern = getPattern("<(.)+?>");
    }

    public StripTagsTextFormatter(String pattern) {
        this.pattern = getPattern(pattern);
    }

    @Override
    public String format(String text) {
        return text.replaceAll(pattern, "");
    }

    private String getPattern(String pattern) {
        return "(" +
                "(<script(.)+?</script>)|" +
                "(<style(.)+?</style>)|" +
                "(<img src=\"http(.)+?>)|" +
                "(" + pattern + ")" +
                ")";
    }
}
