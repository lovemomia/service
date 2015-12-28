package cn.momia.api.event.dto;

import cn.momia.common.api.client.ClientType;
import org.apache.commons.lang3.StringUtils;

public class Version {
    private String action;
    private int platform;
    private String version;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isInvalid(int clientType, String clientVersion) {
        if (platform != 0 && platform != clientType) return true;
        if (clientType == ClientType.APP &&
                !StringUtils.isBlank(version) &&
                !StringUtils.isBlank(clientVersion) &&
                version.compareTo(clientVersion) > 0) return true;
        return false;
    }
}
