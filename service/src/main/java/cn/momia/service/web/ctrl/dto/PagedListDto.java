package cn.momia.service.web.ctrl.dto;

public class PagedListDto {
    public static final PagedListDto EMPTY = new PagedListDto();

    private Integer nextIndex;
    private ListDto list = new ListDto();

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
}
