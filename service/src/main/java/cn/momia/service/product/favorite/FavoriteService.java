package cn.momia.service.product.favorite;

import cn.momia.service.base.Service;

public interface FavoriteService extends Service {
    boolean isFavoried(long userId, long productId);
    boolean favor(long userId, long productId);
}
