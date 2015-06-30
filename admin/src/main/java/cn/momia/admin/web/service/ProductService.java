package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.DataBean;
import cn.momia.admin.web.entity.Product;
import cn.momia.admin.web.entity.ProductImg;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
public interface ProductService {
    public Product get(int id);
    public List<Product> getEntitys();
    public List<Product> getEntities(List<Product> products);
    public List<Product> getQueryPages(int start_row,int end_row);
    public List<Product> getEntitysByKey(int categoryId);
    public int insert(Product entity);
    public int update(Product entity);
    public int delete(int id);
    public Product formEntity(HttpServletRequest request,int id);
    public String getContentJsonStr(HttpServletRequest req);
    public int update_content(int pid,String contentJson);
    public Map<String, String> getContentJsontoMap(String jsonStr);
    public String getPreviewInfo(int id);
}
