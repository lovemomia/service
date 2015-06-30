package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.SkuPropertyValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface SkuPropertyValueService {

    public SkuPropertyValue get(int id);
    public List<SkuPropertyValue> getEntitys();
    public List<SkuPropertyValue> getEntitysByKey(int propertyId);
    public int insert(SkuPropertyValue entity);
    public int update(SkuPropertyValue entity);
    public int delete(int id);
    public SkuPropertyValue formEntity(HttpServletRequest req, int id);
}
