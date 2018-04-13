// ISpeakCallback.aidl
package com.cnksi.tts;

// Declare any non-default types here with import statements

oneway interface ISpeakCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        void onSpeakBegin();
        void onSpeakPaused();
        void onSpeakResumed();
        void onCompleted(String error);
}
