package cn.momia.service.feed.comment;

import cn.momia.service.base.Service;

import java.util.List;

public interface FeedCommentService extends Service {
    int getCount(long feedId);
    List<FeedComment> get(long feedId, int start, int count);
}
