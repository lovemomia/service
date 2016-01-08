package cn.momia.api.operate.dto;

import cn.momia.common.core.platform.Platform;
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

    public boolean isInvalid(int platform, String version) {
        if (this.platform != 0 && this.platform != platform) return true;
        if (platform == Platform.APP &&
                !StringUtils.isBlank(this.version) &&
                !StringUtils.isBlank(version) &&
                this.version.compareTo(version) > 0) return true;
        return false;
    }
}
