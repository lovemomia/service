package cn.momia.service.feed.facade;

import cn.momia.service.feed.comment.FeedComment;

import java.util.List;

public interface FeedServiceFacade {
    Feed get(long feedId);
    long queryFollowedCountByUser(long userId);
    List<Feed> queryFollowedByUser(long userId, int start, int count);

    long queryCommentsCount(long feedId);
    List<FeedComment> queryComments(long feedId, int start, int count);
    long queryStaredUsersCount(long feedId);
    List<Long> queryStaredUserIds(long feedId, int start, int count);

    long queryCountByTopic(long topicId);
    List<Feed> queryByTopic(long topicId, int start, int count);

    boolean addComment(long userId, long feedId, String content);
    boolean deleteComment(long userId, long feedId, long commentId);

    void increaseCommentCount(long feedId);
    void decreaseCommentCount(long feedId);

    boolean star(long userId, long feedId);
    boolean unstar(long userId, long feedId);

    void increaseStarCount(long feedId);
    void decreaseStarCount(long feedId);
}
