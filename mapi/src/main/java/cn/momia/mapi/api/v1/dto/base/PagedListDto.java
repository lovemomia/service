package cn.momia.mapi.api.v1.dto.base;

public class PagedListDto implements Dto {
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

    public void add(Dto dto) {
        list.add(dto);
    }

    public void addAll(ListDto dtos) {
        list.addAll(dtos);
    }
}
