package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.entity.PlaceImg;

import java.util.List;

/**
 * Created by hoze on 15/6/15.
 */
public interface PlaceImgService {
    public PlaceImg get(int id);
    public List<PlaceImg> getEntitys();
    public List<PlaceImg> getEntitysByKey(int placeId);
    public int insert(PlaceImg entity);
    public int update(PlaceImg entity);
    public int delete(int id);
    public PlaceImg formEntity(Images img,int id);
}
