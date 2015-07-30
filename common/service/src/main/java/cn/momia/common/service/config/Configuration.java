package cn.momia.common.service.config;

import cn.momia.common.fs.FileUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

// adapter of XMLConfiguration in commons-configuration
public class Configuration {
    private static String fileName;
    private static XMLConfiguration xmlConf;

    public void setFileName(String fileName) {
        Configuration.fileName = fileName;
    }

    public void init() {
        try {
            xmlConf = new XMLConfiguration();
            xmlConf.load(FileUtil.openFileInputStream(fileName));
        } catch (ConfigurationException e) {
            throw new RuntimeException("fail to load config file: " + fileName, e);
        }
    }

    public static void add(String key, String value) {
        xmlConf.addProperty(key, value);
    }

    public static void update(String key, String value) {
        xmlConf.setProperty(key, value);
    }

    public static void reload() {
        try {
            XMLConfiguration newConf = new XMLConfiguration();
            newConf.load(FileUtil.openFileInputStream(fileName));

            xmlConf = newConf;
        } catch (ConfigurationException e) {
            throw new RuntimeException("fail to reload config file: " + fileName, e);
        }
    }

    public static boolean contains(String key) {
        return xmlConf.containsKey(key);
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
}
