// ISpeakInterface.aidl
package com.cnksi.tts;
import com.cnksi.tts.ISpeakCallback;
// Declare any non-default types here with import statements

interface ISpeakInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     int speak(String content,ISpeakCallback callback);
     int stopSpeak();
     boolean setSpeaker(String speaker);
}
