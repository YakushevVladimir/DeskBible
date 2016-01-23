package com.BibleQuote.utils.textFormatters;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vladimir Yakushev
 * @version 1.0 of 01.2016
 */
public class NoStrongTextFormatterTest {

    @Test
    public void testFormat() throws Exception {
        NoStrongTextFormatter formatter = new NoStrongTextFormatter();

        String result = formatter.format(
                "<p><sup>1</sup> НектоH376 из племениH1004 ЛевиинаH3878 пошелH3212 и " +
                        "взялH3947 себе женуH1323 из того же племениH3878.");
        Assert.assertFalse(result.contains("H3947"));

        result = formatter.format(
                "<p><sup>1</sup> Некто H376 из племени H1004 Левиина H3878 пошел H3212 и " +
                        "взял H3947 себе жену H1323 из того же племени H3878.");
        Assert.assertFalse(result.contains("H3947"));
    }
}