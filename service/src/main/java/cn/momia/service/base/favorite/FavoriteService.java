package cn.momia.service.base.favorite;

import java.util.List;

public interface FavoriteService {
    long add(long userId, long productId);
    boolean delete(long userId, long id);
    List<Long> queryFavoredProductsByUser(long userId, int start, int count);
}
