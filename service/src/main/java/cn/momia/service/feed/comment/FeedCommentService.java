package cn.momia.service.feed.comment;

import cn.momia.service.base.Service;

public interface FeedCommentService extends Service {
    int getCount(long feedId);
}
