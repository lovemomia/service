package cn.momia.service.deal.api.order;

import java.util.ArrayList;
import java.util.List;

public class PagedOrders {
    private long totalCount;
    private Integer nextIndex;
    private List<Order> list = new ArrayList<Order>();

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

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }
}
