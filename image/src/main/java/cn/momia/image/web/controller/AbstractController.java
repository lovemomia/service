package cn.momia.image.web.controller;

import cn.momia.common.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {
    @Autowired
    protected Configuration conf;
}
