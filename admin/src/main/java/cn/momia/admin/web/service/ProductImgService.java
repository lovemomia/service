package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.entity.ProductImg;

import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface ProductImgService {

    public ProductImg get(int id);
    public List<ProductImg> getEntitys();
    public List<ProductImg> getEntitysByKey(int productId);
    public int insert(ProductImg entity);
    public int update(ProductImg entity);
    public int delete(int id);
    public ProductImg formEntity(Images img, int id);

}
