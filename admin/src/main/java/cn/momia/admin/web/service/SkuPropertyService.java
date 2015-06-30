package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.SkuProperty;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface SkuPropertyService {
    public SkuProperty get(int id);
    public List<SkuProperty> getEntitys();
    public List<SkuProperty> getEntitysByKey(int categoryId);
    public int insert(SkuProperty entity);
    public int update(SkuProperty entity);
    public int delete(int id);
    public SkuProperty formEntity(HttpServletRequest req,int id);
}
