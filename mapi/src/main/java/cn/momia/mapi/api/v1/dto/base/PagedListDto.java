package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONArray;

public class PagedListDto implements Dto {
    public static final PagedListDto EMPTY = new PagedListDto();

    private long totalCount;
    private Integer nextIndex;
    private JSONArray list = new JSONArray();

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

    public JSONArray getList() {
        return list;
    }

    public void setList(JSONArray list) {
        this.list = list;
    }
}
