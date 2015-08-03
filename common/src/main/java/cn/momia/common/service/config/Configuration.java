package cn.momia.common.service.config;

import org.apache.commons.configuration.XMLConfiguration;

public class Configuration {
    private static XMLConfiguration xmlConf;
    private static SecretKey secretKey;

    public void setXmlConf(XMLConfiguration xmlConf) {
        Configuration.xmlConf = xmlConf;
    }

    public void setSecretKey(SecretKey secretKey) {
        Configuration.secretKey = secretKey;
    }

    public static boolean getBoolean(String key) {
        return xmlConf.getBoolean(key);
    }

    public static int getInt(String key) {
        return xmlConf.getInt(key);
    }

    public static long getLong(String key) {
        return xmlConf.getLong(key);
    }

    public static float getFloat(String key) {
        return xmlConf.getFloat(key);
    }

    public static double getDouble(String key) {
        return xmlConf.getDouble(key);
    }

    public static String getString(String key) {
        return xmlConf.getString(key);
    }

    public static String getSecretKey()
    {
        return secretKey.get();
    }

    public static String getSecretKey(String biz) {
        return secretKey.get(biz);
    }

    public static String getPasswordSecretKey() {
        return secretKey.get("password");
    }
}
