package cn.momia.api.deal.entity;

import java.util.ArrayList;
import java.util.List;

public class PagedCoupons {
    private long totalCount;
    private Integer nextIndex;
    private List<Coupon> list = new ArrayList<Coupon>();

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

    public List<Coupon> getList() {
        return list;
    }

    public void setList(List<Coupon> list) {
        this.list = list;
    }
}
