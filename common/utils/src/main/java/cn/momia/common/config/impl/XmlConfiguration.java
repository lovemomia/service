package cn.momia.common.config.impl;

import cn.momia.common.config.Configuration;
import cn.momia.common.fs.FileUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

// adapter of XMLConfiguration in commons-configuration
public class XmlConfiguration implements Configuration {
    private String fileName;
    private XMLConfiguration xmlConf;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void init() {
        try {
            xmlConf = new XMLConfiguration();
            xmlConf.load(FileUtil.openFileInputStream(fileName));
        } catch (ConfigurationException e) {
            throw new RuntimeException("fail to load config file: " + fileName, e);
        }
    }

    @Override
    public void add(String key, String value) {
        xmlConf.addProperty(key, value);
    }

    @Override
    public void update(String key, String value) {
        xmlConf.setProperty(key, value);
    }

    @Override
    public void reload() {
        try {
            XMLConfiguration newConf = new XMLConfiguration();
            newConf.load(FileUtil.openFileInputStream(fileName));

            xmlConf = newConf;
        } catch (ConfigurationException e) {
            throw new RuntimeException("fail to reload config file: " + fileName, e);
        }
    }

    @Override
    public boolean contains(String key) {
        return xmlConf.containsKey(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return xmlConf.getBoolean(key);
    }

    @Override
    public int getInt(String key) {
        return xmlConf.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return xmlConf.getLong(key);
    }

    @Override
    public float getFloat(String key) {
        return xmlConf.getFloat(key);
    }

    @Override
    public double getDouble(String key) {
        return xmlConf.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return xmlConf.getString(key);
    }
}
