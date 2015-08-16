package cn.momia.api.feed;

import java.util.ArrayList;
import java.util.List;

public class PagedFeeds {
    private long totalCount;
    private Integer nextIndex;
    private List<Feed> list = new ArrayList<Feed>();

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

    public List<Feed> getList() {
        return list;
    }

    public void setList(List<Feed> list) {
        this.list = list;
    }
}
