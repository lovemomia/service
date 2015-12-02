package cn.momia.service.event.icon;

import cn.momia.api.event.dto.Icon;

import java.util.List;

public interface IconService {
    List<Icon> list(int cityId, int count);
}
