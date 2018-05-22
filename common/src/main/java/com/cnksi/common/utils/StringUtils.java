package com.cnksi.common.utils;

import static com.cnksi.core.utils.StringUtils.isEmpty;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/22 10:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class StringUtils {

    public static boolean isEmptys(String... strings) {
        for (String string : strings) {
            if (isEmpty(string)) {
                return true;
            }
        }
        return false;
    }
}
