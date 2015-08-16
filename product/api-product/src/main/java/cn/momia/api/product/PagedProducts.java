package cn.momia.api.product;

import java.util.ArrayList;
import java.util.List;

public class PagedProducts {
    private long totalCount;
    private Integer nextIndex;
    private List<Product> list = new ArrayList<Product>();

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

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }
}
