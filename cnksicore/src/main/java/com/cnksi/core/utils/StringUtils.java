package com.cnksi.core.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 */
public class StringUtils {

    private StringUtils() {
        throw new AssertionError();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 判断是否是手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * @param current 当前值
     * @param total   总值
     * @return 当前百分比
     * @Description:返回百分之值
     */
    public static String getPercent(int current, int total) {
        // 接受百分比的值
        double x_double = current * 1.0;
        double tempresult = x_double / total;
        // 百分比格式，后面不足2位的用0补齐 ##.00%
        return new DecimalFormat("0.00%").format(tempresult);
    }

    /**
     * 检查是否是数字
     *
     * @param str
     * @return
     */
    public static final boolean isNumeric(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[0-9]*$");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查是否是邮箱
     *
     * @param str
     * @return
     */
    public static final boolean isEmailAddress(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[\\w-\\.+]*[\\w-\\.]\\@([\\w-]+\\.)+[\\w]+[\\w]{1}");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    public static CharSequence changeTextColor(CharSequence content, @ColorInt int color) {
        if (content.length() > 0)
            return changePartTextColor(content, color, 0, content.length());
        else return "";
    }

    /**
     * 改变部分字体的颜色
     *
     * @param content     需要改变的内容
     * @param color       颜色值
     * @param startOffset 开始位置
     * @param endOffset   结束位置
     * @return
     */
    public static SpannableStringBuilder changePartTextColor(CharSequence content, @ColorInt int color, int startOffset, int endOffset) {
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(color), startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符是否是中文
     *
     * @param content 字符
     * @return 是否是中文
     */
    public static boolean isNumberAndLetter(String content) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9]+$");
        java.util.regex.Matcher m = pattern.matcher(content);
        return m.matches();
    }

    /**
     * 字符串中是否含有中文
     *
     * @param s
     * @return
     */
    public static boolean containsChinese(String s) {
        if (null == s || "".equals(s.trim())) return false;
        for (int i = 0; i < s.length(); i++) {
            if (isChinese(s.charAt(i))) return true;
        }
        return false;
    }

    /**
     * 字符串是否是字母
     *
     * @param str
     * @return
     */
    public static boolean isLetters(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z]+");
        java.util.regex.Matcher m = pattern.matcher(str);
        return m.matches();
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase(Locale.CHINA);
            }
        }
        return filename.toLowerCase(Locale.CHINA);
    }

    /**
     * 复制内容
     *
     * @param context
     * @param content
     */
    public static final void copyContent(Context context, String content) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("simple text", content);
        clip.setPrimaryClip(clipData);
    }

    /**
     * 粘贴内容
     *
     * @return
     */
    public static final String pastContent(Context context) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clip.getPrimaryClip().toString(); // 粘贴
    }

    /**
     * 在字符串中加入特定符号
     *
     * @param offset 位置
     * @param str    源字符串
     * @param symbol 符号
     * @return
     */
    public static String insertSymbolToString(int offset, String str, String symbol) {
        if (!TextUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder(str);
            for (int i = offset; i < sb.length(); i += offset) {
                sb.insert(i++, symbol);
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 在字符串中加入特定符号
     *
     * @param offset 位置
     * @param str    源字符串
     * @param symbol 符号
     * @return
     */
    public static String insertOneSymbolToString(int offset, String str, String symbol) {
        if (!TextUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder(str);
            sb.insert(offset, symbol);
            return sb.toString();
        }
        return "";
    }

    /**
     * 判断是否字段的值是否为空或null
     *
     * @param str
     * @return
     */
    public static String cleanString(String str) {
        if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 删除最后一个字符
     *
     * @param str
     * @param lastStr
     * @return
     */
    public static String deleteLastChar(String str, String lastStr) {
        if (!TextUtils.isEmpty(str)) {
            if (str.endsWith(lastStr)) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }

    /**
     * 改变部分字体的颜色
     *
     * @param context     上下文
     * @param content     需要改变的内容
     * @param colorResid  颜色值
     * @param startOffset 开始位置
     * @param endOffset   结束位置
     * @return
     */
    public static SpannableStringBuilder changePartTextColor(Context context, String content, int colorResid, int startOffset, int endOffset) {
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorResid)), startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }

    /**
     * 将List<String>转换成String
     *
     * @param strList
     * @return
     */
    public static String ArrayListToString(List<String> strList) {
        return ArrayListToString(strList, "", "");
    }

    /**
     * 将List<String>转换成String
     *
     * @param strList
     * @return
     */
    public static String ArrayListToString(List<String> strList, String replaceTarget) {
        return ArrayListToString(strList, replaceTarget, "");
    }

    /**
     * 将List<String>转换成String
     *
     * @param strList
     * @param target      需要替换的字符串
     * @param replacement 替换后的字符串
     * @return
     */
    public static String ArrayListToString(List<String> strList, CharSequence target, CharSequence replacement) {
        StringBuffer sb = new StringBuffer("");
        if (strList != null && !strList.isEmpty()) {
            for (int i = 0, count = strList.size(); i < count; i++) {
                String content = strList.get(i);
                if (!TextUtils.isEmpty(target) || !TextUtils.isEmpty(replacement)) {
                    content = content.replace(target, replacement);
                }
                sb.append(content);
                if (i != count - 1) {
                    sb.append(CoreConfig.COMMA_SEPARATOR);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 保留几位小数
     *
     * @param content
     * @param count
     * @return
     */
    public static String keepValueCount(Object content, int count) {
        return String.valueOf(new BigDecimal(String.valueOf(content)).setScale(count, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * 在List<String> 集合中的每个item 添加 字符串
     *
     * @param strList   源集合 a.jpg,b.jpg
     * @param addTarget 添加的字符串 123
     * @param isFront   是否在前面添加
     * @return 123a.jpg, 123b.jpg;
     */
    public static ArrayList<String> addStrToListItem(List<String> strList, String addTarget, boolean isFront) {
        ArrayList<String> targetList = new ArrayList<String>();
        if (strList != null && !strList.isEmpty()) {
            for (int i = 0, count = strList.size(); i < count; i++) {
                if (isFront) {
                    targetList.add(addTarget + strList.get(i));
                } else {
                    targetList.add(strList.get(i) + addTarget);
                }
            }
        }
        return targetList;
    }

    /**
     * 在List<String> 集合中的每个item 前面添加 字符串
     *
     * @param strList   源集合 a.jpg,b.jpg
     * @param addTarget 添加的字符串 123
     */
    public static ArrayList<String> addStrToListItem(List<String> strList, String addTarget) {
        return addStrToListItem(strList, addTarget, true);
    }

    /**
     * 多个关键字关键字变色
     *
     * @param color
     * @param text
     * @param keyWord
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text, String[] keyWord) {
        SpannableString s = new SpannableString(text);
        for (int i = 0, count = keyWord.length; i < count; i++) {
            Pattern p = Pattern.compile(keyWord[i]);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    /**
     * 一个关键字关键字变色
     *
     * @param color
     * @param text
     * @param keyWord
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text, String keyWord) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyWord);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return d;
    }

    /**
     * 将给定的字符串中所有给定的关键字标红
     *
     * @param sourceString 给定的字符串
     * @param keyword      给定的关键字
     * @return 返回的是带Html标签的字符串，在使用时要通过Html.fromHtml()转换为Spanned对象再传递给TextView对象
     */
    public static String keywordMadeRed(String sourceString, String keyword) {
        String result = "";
        if (sourceString != null && !"".equals(sourceString.trim())) {
            if (keyword != null && !"".equals(keyword.trim())) {
                result = sourceString.replaceAll(keyword, "<font color=\"red\">" + keyword + "</font>");
            } else {
                result = sourceString;
            }
        }
        return result;
    }

    /**
     * 为给定的字符串添加HTML红色标记，当使用Html.fromHtml()方式显示到TextView 的时候其将是红色的
     *
     * @param string 给定的字符串
     * @return
     */
    public static String addHtmlRedFlag(String string) {
        return "<font color=\"red\">" + string + "</font>";
    }


    /**
     * 将str分割转换成List
     *
     * @param str
     * @return
     */
    public static ArrayList<String> string2List(String str) {
        ArrayList<String> list = null;
        if (!TextUtils.isEmpty(str)) {
            String[] strArray = str.split(CoreConfig.MUTI_SYMBOL_SEPARATOR);
            if (strArray != null) {
                list = new ArrayList<>(Arrays.asList(strArray));
            }
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 将str分割转换成List
     *
     * @param str           x/x.jpg,y/y.jpg
     * @param isGetFileName 是否只得到文件名的集合
     * @return {x.jpg，y.jpg}
     */
    public static ArrayList<String> string2List(String str, boolean isGetFileName) {
        ArrayList<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(str)) {
            String[] strArray = str.split(CoreConfig.MUTI_SYMBOL_SEPARATOR);
            if (strArray != null && strArray.length > 0) {
                for (String content : strArray) {
                    if (isGetFileName) {
                        list.add(getFileNameFromPath(content));
                    } else {
                        list.add(content);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 将字符串拼接在另一个字符串后面并以symbol分割
     *
     * @param str
     * @param appendStr
     * @param symbol
     * @return
     */
    public static String appendStrToStr(String str, String appendStr, String symbol) {
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        StringBuilder sb = new StringBuilder(str);
        if (!TextUtils.isEmpty(appendStr)) {
            if (sb.toString().isEmpty()) {
                sb.append(appendStr);
            } else {
                if (sb.toString().endsWith(symbol)) {
                    sb.append(appendStr);
                } else {
                    sb.append(symbol).append(appendStr);
                }
            }
        }
        return sb.toString();
    }


    /**
     * 从文件路径中得到文件名称
     *
     * @param filePath x/x.jpg
     * @return x.jpg
     */
    public static String getFileNameFromPath(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            String temp[] = filePath.trim().split("/");
            final String fileName = temp[temp.length - 1];
            return fileName;
        }
        return "";
    }

}
