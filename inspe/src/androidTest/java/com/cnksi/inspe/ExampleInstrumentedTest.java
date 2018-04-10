package com.cnksi.inspe;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.cnksi.inspe.ui.InspeIssueDetailActivity;
import com.cnksi.inspe.ui.InspePlustekIssueActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.cnksi.inspe", appContext.getPackageName());
        appContext.startActivity(new Intent(appContext, InspePlustekIssueActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        ));
    }
}
