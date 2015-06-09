package cn.momia.service.web.ctrl;

import cn.momia.common.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {
    @Autowired
    protected Configuration conf;
}
