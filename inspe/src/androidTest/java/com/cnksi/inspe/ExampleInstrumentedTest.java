package com.cnksi.inspe;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.ui.InspePlustekIssueActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(appContext, InspePlustekIssueActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(InspePlustekIssueActivity.IntentKey.PLUSTEK_TYPE, PlustekType.pmsjc.name());
        intent.putExtra(InspePlustekIssueActivity.IntentKey.TASK_ID, "677049857717932032");
        intent.putExtra(InspePlustekIssueActivity.IntentKey.DEVICE_ID, "2c9082925f6aeda2015f6aeeaa91001e");
        intent.putExtra(InspePlustekIssueActivity.IntentKey.CONTENT, "Text Content");

//        assertEquals("com.cnksi.inspe", appContext.getPackageName());
        appContext.startActivity(intent);
    }
}
