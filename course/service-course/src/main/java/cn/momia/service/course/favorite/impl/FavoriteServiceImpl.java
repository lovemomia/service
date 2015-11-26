package cn.momia.service.course.favorite.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.course.favorite.Favorite;
import cn.momia.service.course.favorite.FavoriteService;

import java.util.List;

public class FavoriteServiceImpl extends AbstractService implements FavoriteService {
    @Override
    public boolean isFavored(long userId, int type, long refId) {
        String sql = "SELECT COUNT(1) FROM SG_Favorite WHERE UserId=? AND `Type`=? AND RefId=? AND Status<>0";
        return queryInt(sql, new Object[] { userId, type, refId }) > 0;
    }

    @Override
    public boolean favor(long userId, int type, long refId) {
        if (!refExists(type, refId)) return false;

        long favoretId = getFavoretId(userId, type, refId);
        if (favoretId > 0) {
            String sql = "UPDATE SG_Favorite SET Status<>0 WHERE Id=? AND UserId=? AND `Type`=? AND RefId=?";
            return update(sql, new Object[] { favoretId, userId, type, refId });
        } else {
            String sql = "INSERT INTO SG_Favorite(UserId, `Type`, RefId, AddTime) VALUES (?, ?, ?, NOW())";
            return update(sql, new Object[] { userId, type, refId });
        }
    }

    private boolean refExists(int type, long refId) {
        String sql = null;
        if (type == Favorite.Type.COURSE) {
            sql = "SELECT COUNT(1) FROM SG_Course WHERE Id=? AND Status<>0";
        } else if (type == Favorite.Type.SUBJECT) {
            sql = "SELECT COUNT(1) FROM SG_Subject WHERE Id=? AND Status<>0";
        }

        return sql == null ? false : queryInt(sql, new Object[] { refId }) > 0;
    }

    private long getFavoretId(long userId, int type, long refId) {
        String sql = "SELECT Id FROM SG_Favorite WHERE UserId=? AND `Type`=? AND RefId=?";
        return queryLong(sql, new Object[] { userId, type, refId });
    }

    @Override
    public boolean unfavor(long userId, int type, long refId) {
        String sql = "UPDATE SG_Favorite SET Status=0 WHERE UserId=? AND `Type`=? AND RefId=?";
        return update(sql, new Object[] { userId, type, refId });
    }

    @Override
    public long queryFavoriteCount(long userId, int type) {
        String sql = "SELECT COUNT(1) FROM SG_Favorite WHERE UserId=? AND `Type`=? AND Status<>0";
        return queryLong(sql, new Object[] { userId, type });
    }

    @Override
    public List<Favorite> queryFavorites(long userId, int type, int start, int count) {
        String sql = "SELECT Id, `Type`, UserId, RefId FROM SG_Favorite WHERE UserId=? AND `Type`=? AND Status<>0 ORDER BY AddTime DESC LIMIT ?,?";
        return queryObjectList(sql, new Object[] { userId, type, start, count }, Favorite.class);
    }
}
