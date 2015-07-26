package cn.momia.service.feed;

import cn.momia.service.feed.comment.FeedComment;

import java.util.List;

public interface FeedServiceFacade {
    Feed get(long id);
    long queryCommentsCount(long id);
    List<FeedComment> queryComments(long id, int start, int count);
    long queryStaredUsersCount(long id);
    List<Long> queryStaredUserIds(long id, int start, int count);
}
