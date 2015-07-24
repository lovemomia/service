package cn.momia.service.feed;

import cn.momia.service.feed.comment.FeedComment;

import java.util.List;

public interface FeedServiceFacade {
    Feed get(long id);
    List<FeedComment> getComments(long id, int start, int count);
}
