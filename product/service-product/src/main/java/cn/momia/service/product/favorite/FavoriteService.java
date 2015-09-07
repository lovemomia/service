package cn.momia.service.product.favorite;

import java.util.List;

public interface FavoriteService {
    boolean isFavoried(long userId, long productId);
    boolean favor(long userId, long productId);
    boolean unFavor(long userId, long productId);

    long queryCount(long userId);
    List<Long> query(long userId, int start, int count);
}
