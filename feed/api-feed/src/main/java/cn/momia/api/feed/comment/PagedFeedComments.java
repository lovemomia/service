package cn.momia.api.feed.comment;

import java.util.ArrayList;
import java.util.List;

public class PagedFeedComments {
    private long totalCount;
    private Integer nextIndex;
    private List<FeedComment> list = new ArrayList<FeedComment>();

    public List<FeedComment> getList() {
        return list;
    }

    public void setList(List<FeedComment> list) {
        this.list = list;
    }

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
}
