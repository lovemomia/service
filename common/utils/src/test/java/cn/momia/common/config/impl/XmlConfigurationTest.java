package cn.momia.common.config.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

public class XmlConfigurationTest {
    @Test
    public void testGetValue() {
        XmlConfiguration conf = new XmlConfiguration();
        conf.setFileName("conf/configuration-test.xml");
        conf.init();

        Assert.assertTrue(conf.getBoolean("Boolean"));
        Assert.assertEquals(conf.getInt("Integer"), 10);
        Assert.assertEquals(conf.getLong("Long"), 12345678901234L);
        Assert.assertEquals(conf.getFloat("Float"), 0.5F);
        Assert.assertEquals(conf.getDouble("Double"), 0.0000000005);
        Assert.assertEquals(conf.getString("String"), "configuration");
    }
}
