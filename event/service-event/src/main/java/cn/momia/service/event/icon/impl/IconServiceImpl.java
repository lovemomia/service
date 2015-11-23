package cn.momia.service.event.icon.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.event.icon.Icon;
import cn.momia.service.event.icon.IconService;

import java.util.List;

public class IconServiceImpl extends AbstractService implements IconService {
    @Override
    public List<Icon> list(int cityId, int count) {
        String sql = "SELECT Title, Img, Action FROM SG_Icon WHERE (CityId=? OR CityId=0) AND Status<>0 ORDER BY Weight DESC, AddTime DESC LIMIT ?";
        return queryObjectList(sql, new Object[] { cityId, count }, Icon.class);
    }
}
