package cn.momia.service.feed.api.star;

import java.util.ArrayList;
import java.util.List;

public class PagedFeedStars {
    private long totalCount;
    private Integer nextIndex;
    private List<FeedStar> list = new ArrayList<FeedStar>();

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }

    public List<FeedStar> getList() {
        return list;
    }

    public void setList(List<FeedStar> list) {
        this.list = list;
    }
}
