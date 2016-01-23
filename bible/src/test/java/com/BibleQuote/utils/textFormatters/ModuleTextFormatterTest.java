package com.BibleQuote.utils.textFormatters;

import android.os.Build;
import com.BibleQuote.BuildConfig;
import com.BibleQuote.modules.FsModule;
import com.BibleQuote.modules.Module;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN,
        manifest = "src/main/AndroidManifest.xml",
        constants = BuildConfig.class)
public class ModuleTextFormatterTest {

    private String testVerses =
            "<p>12 Услышав же Иисус, что Иоанн отдан <I>под</I> <I>стражу,</I> удалился в Галилею\n" +
            "<p>13 и, оставив Назарет, пришел и поселился в Капернауме приморском, в пределах Завулоновых и Неффалимовых,";

    private String testVersesWithStrong =
            "<p>12 Услышав же Иисус G1234 G59, что Иоанн отдан <I>под</I> <I>стражу,</I> удалился в Галилею\n" +
            "<p>13 и, оставив Назарет, пришел 1234 59 и поселился в Капернауме приморском, в пределах Завулоновых и Неффалимовых,";

    private Module mModule;

    @Before
    public void testBefore() {
        mModule = new FsModule("base/biblequote.ini");
        mModule.containsStrong = false;
        mModule.isBible = true;
    }

    @After
    public void testAfter() {
        mModule = null;
    }

    @Test
    public void testFullTagsClean() throws Exception {
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule, new StripTagsTextFormatter());
        formatter.setVisibleVerseNumbers(false);

        String result = formatter.format(testVerses);
        Assert.assertFalse(result.contains("<I>"));
        Assert.assertFalse(result.contains("</I>"));
        Assert.assertFalse(result.contains("<p>"));
        Assert.assertFalse(result.contains("</p>"));
    }

    @Test
    public void testSetVisibleVerseNumbers() throws Exception {
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule);
        formatter.setVisibleVerseNumbers(false);

        String result = formatter.format(testVerses);
        Assert.assertFalse(result.contains("12"));
        Assert.assertFalse(result.contains("13"));
    }

    @Test
    public void testFormatCleanTags() throws Exception {
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule);

        String result = formatter.format(testVerses);
        Assert.assertTrue(result.contains("<I>"));
        Assert.assertTrue(result.contains("<p>"));
    }

    @Test
    public void testFormatModuleWithStrong() throws Exception {
        mModule.containsStrong = true;
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule);

        String result = formatter.format(testVersesWithStrong);
        Assert.assertFalse(result.contains("G1234"));
        Assert.assertFalse(result.contains("G59"));
        Assert.assertFalse(result.contains("1234"));
        Assert.assertFalse(result.contains("59"));

        Pattern pattern = Pattern.compile("\\s{2,}");
        Matcher matcher = pattern.matcher(result);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testFormatModuleWithoutStrong() throws Exception {
        mModule.containsStrong = false;
        ModuleTextFormatter formatter = new ModuleTextFormatter(mModule);

        String result = formatter.format(testVersesWithStrong);
        Assert.assertTrue(result.contains("G1234"));
        Assert.assertTrue(result.contains("G59"));
        Assert.assertTrue(result.contains("1234"));
        Assert.assertTrue(result.contains("59"));
    }
}