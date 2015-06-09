package cn.momia.common.config;

public interface Configuration {
    void add(String key, String value);
    void update(String key, String value);
    void reload();

    boolean contains(String key);
    boolean getBoolean(String key);
    int getInt(String key);
    long getLong(String key);
    float getFloat(String key);
    double getDouble(String key);
    String getString(String key);
}
