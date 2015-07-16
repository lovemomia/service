package cn.momia.service.web.ctrl;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController extends BaseController {
    @Autowired
    protected Configuration conf;

    protected boolean isInvalidLimit(int start, int count) {
        int maxPage = conf.getInt("Limit.MaxPage");
        int maxPageSize = conf.getInt("Limit.MaxPageSize");

        return start < 0 || count < 0 || start > maxPage * maxPageSize || count > maxPageSize;
    }
}
