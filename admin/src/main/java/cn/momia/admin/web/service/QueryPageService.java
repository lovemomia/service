package cn.momia.admin.web.service;

import cn.momia.admin.web.common.QueryPage;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/30.
 */
public interface QueryPageService {
    public QueryPage formEntity(int type,int pageNo);

    public QueryPage getEntitys(QueryPage queryPage);
}
