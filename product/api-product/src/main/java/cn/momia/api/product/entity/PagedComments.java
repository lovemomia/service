package cn.momia.api.product.entity;

import java.util.ArrayList;
import java.util.List;

public class PagedComments {
    private long totalCount;
    private Integer nextIndex;
    private List<Comment> list = new ArrayList<Comment>();

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

    public List<Comment> getList() {
        return list;
    }

    public void setList(List<Comment> list) {
        this.list = list;
    }
}
