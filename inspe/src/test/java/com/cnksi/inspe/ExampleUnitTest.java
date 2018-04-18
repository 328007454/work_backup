package com.cnksi.inspe;

import android.text.TextUtils;

import org.junit.Test;

import java.text.BreakIterator;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        test();
    }

    public static void test() {
        String sentence = "采购技术协议或技术规范书";
        final ArrayList<String> result = new ArrayList<String>();
//        if (!TextUtils.isEmpty(sentence)) {
            final BreakIterator boundary = BreakIterator.getWordInstance();
            boundary.setText(sentence);
//            try {
                int start = boundary.first();
                for (int end = boundary.next();
                     end != BreakIterator.DONE;
                     start = end, end = boundary.next()) {
                    String word = sentence.substring(start, end);
                    if (!TextUtils.isEmpty(word)) {
                        result.add(word);
                    }
                }
//            } catch (IndexOutOfBoundsException e) {
//                e.printStackTrace();
//                result.clear();
//            }
//        }
        for (String s : result) {
            System.out.println(s);
        }
    }


}