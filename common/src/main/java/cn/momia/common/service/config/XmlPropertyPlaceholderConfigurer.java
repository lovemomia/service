package cn.momia.common.service.config;

import org.apache.commons.configuration.XMLConfiguration;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

public class XmlPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private XMLConfiguration conf;

    public void setConf(XMLConfiguration conf) {
        this.conf = conf;
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String propVal = null;

        if (systemPropertiesMode == PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE) propVal = resolveSystemProperty(placeholder);
        if (propVal == null) propVal = conf.getString(placeholder);
        if (propVal == null) propVal = resolvePlaceholder(placeholder, props);
        if (propVal == null && systemPropertiesMode == PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK) propVal = resolveSystemProperty(placeholder);

        return propVal;
    }
}
