package com.cnksi.inspe;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        StringBuilder sb = new StringBuilder();
        sb.append("abcdef");
        sb.delete(sb.length() - 1, sb.length());
        System.out.println(sb.toString());
    }
}