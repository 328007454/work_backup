package com.cnksi.inspe.config;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 13:00
 */

@Deprecated
public final class Config {
    private static String host = "http://127.0.0.1:58888";

    public static String getHost() {
        return Config.host;
    }

    public static void setHost(String host) {
        Config.host = host;
    }
}
