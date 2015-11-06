package cn.momia.service.course.favorite;

import java.util.List;

public interface FavoriteService {
    boolean isFavored(long userId, int type, long refId);
    boolean favor(long userId,  int type, long refId);
    boolean unfavor(long userId,  int type, long refId);

    long queryFavoriteCount(long userId, int type);
    List<Favorite> queryFavorites(long userId, int type, int start, int count);
}
