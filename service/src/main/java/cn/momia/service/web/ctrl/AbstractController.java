package cn.momia.service.web.ctrl;

import cn.momia.common.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {
    @Autowired
    protected Configuration conf;

    protected boolean isInvalidLimit(int start, int count) {
        int maxPage = conf.getInt("Limit.MaxPage");
        int maxPageSize = conf.getInt("Limit, MaxPageSize");

        return start <= maxPage * maxPageSize && count <= maxPageSize;
    }
}
