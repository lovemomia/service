package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;

public class PagedListDto<T extends Dto> implements Dto {
    private long totalCount;
    private Integer nextIndex;
    private ListDto list = new ListDto();

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

    public ListDto getList() {
        return list;
    }

    public void setList(ListDto list) {
        this.list = list;
    }

    public void add(T dto) {
        list.add(dto);
    }

    public void addAll(ListDto dtos) {
        list.addAll(dtos);
    }
}
