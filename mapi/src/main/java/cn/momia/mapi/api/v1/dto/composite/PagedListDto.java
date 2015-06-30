package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;

import java.util.ArrayList;
import java.util.List;

public class PagedListDto<T extends Dto> implements Dto {
    private long totalCount;
    private Integer nextIndex;
    private List<T> list = new ArrayList<T>();

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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void add(T dto) {
        list.add(dto);
    }

    public void addAll(List<T> dtos) {
        list.addAll(dtos);
    }
}
