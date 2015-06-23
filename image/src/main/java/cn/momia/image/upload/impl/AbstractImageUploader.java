package cn.momia.image.upload.impl;

import cn.momia.common.config.Configuration;
import cn.momia.image.upload.ImageUploader;

public abstract class AbstractImageUploader implements ImageUploader {
    protected Configuration conf;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }
}
