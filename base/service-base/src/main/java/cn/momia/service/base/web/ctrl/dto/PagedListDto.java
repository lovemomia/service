package cn.momia.service.base.web.ctrl.dto;

public class PagedListDto implements Dto {
    public static final PagedListDto EMPTY = new PagedListDto(0, 0, 0);

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
    public void add(Object obj) {
        this.list.add(obj);
    }

    public void addAll(ListDto list) {
        this.list.addAll(list);
    }

    public PagedListDto(long totalCount, int start, int count) {
        this.totalCount = totalCount;
        if (start + count < totalCount) this.nextIndex = start + count;
    }
}
