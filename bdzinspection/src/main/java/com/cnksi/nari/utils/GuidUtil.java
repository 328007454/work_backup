package com.cnksi.nari.utils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 主键ID生成工具 源代码来源于NARI
 */
public final class GuidUtil {
    private static Pattern REGEX = Pattern.compile("^[a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}$");
    private static String _seqGUID = null;
    private static int _seqNumber = 0;

    public static String newGuid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    public static String nextSequence() {
        _seqNumber++;
        if (_seqGUID == null || _seqNumber > 99999) {
            _seqGUID = newGuid();
            _seqNumber = 1;
        }
        return _seqGUID + "-" + String.format("%1$05d", Integer.valueOf(_seqNumber));
    }

    public static boolean validateGuid(String str) {
        return REGEX.matcher(str).find();
    }
}