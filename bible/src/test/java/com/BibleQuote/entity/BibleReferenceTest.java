package com.BibleQuote.entity;

import android.net.Uri;
import android.os.Build;
import com.BibleQuote.BuildConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN,
        manifest = "src/main/AndroidManifest.xml",
        constants = BuildConfig.class)
public class BibleReferenceTest {

    @Test
    public void testParseLinkFromIntent() throws Exception {
        Uri link = Uri.parse("http://b-bq.eu/Exod/16_2-3/RST_Strong)");
        BibleReference ref = new BibleReference(link);
        Assert.assertTrue(ref.getPath() != null);
    }
}