package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;

import java.util.ArrayList;
import java.util.List;

public class PagedListDto<T extends Dto> implements Dto {
    private long totalcount;
    private Integer nextindex;
    private List<T> list = new ArrayList<T>();

    public long getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(long totalcount) {
        this.totalcount = totalcount;
    }

    public Integer getNextindex() {
        return nextindex;
    }

    public void setNextindex(Integer nextindex) {
        this.nextindex = nextindex;
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
