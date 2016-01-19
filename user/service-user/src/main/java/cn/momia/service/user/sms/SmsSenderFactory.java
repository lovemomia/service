package cn.momia.service.user.sms;

import cn.momia.common.core.exception.MomiaErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SmsSenderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderFactory.class);

    private Map<String, SmsSender> prototypes;

    public void setPrototypes(Map<String, SmsSender> prototypes) {
        this.prototypes = prototypes;
    }

    public SmsSender getSmsSender(String name) {
        if (prototypes.containsKey(name)) return prototypes.get(name);

        LOGGER.error("invalid sms sender: {}", name);
        throw new MomiaErrorException("invalid sms sender: " + name);
    }
}
