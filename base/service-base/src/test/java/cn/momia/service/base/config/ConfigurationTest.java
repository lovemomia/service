package cn.momia.service.base.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigurationTest {
    @Test
    public void testGetValue() throws ConfigurationException {
        Configuration conf = new Configuration();
        conf.setXmlConf(new XMLConfiguration("conf/configuration-test.xml"));

        Assert.assertTrue(conf.getBoolean("Boolean"));
        Assert.assertEquals(conf.getInt("Integer"), 10);
        Assert.assertEquals(conf.getLong("Long"), 12345678901234L);
        Assert.assertEquals(conf.getFloat("Float"), 0.5F);
        Assert.assertEquals(conf.getDouble("Double"), 0.0000000005);
        Assert.assertEquals(conf.getString("String"), "configuration");
    }
}
