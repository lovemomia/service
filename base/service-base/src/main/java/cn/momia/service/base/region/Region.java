package cn.momia.service.base.region;

public class Region {
    public static final Region NOT_EXIST_REGION = new Region();
    static {
        NOT_EXIST_REGION.setId(0);
        NOT_EXIST_REGION.setName("不存在");
    }

    private int id;
    private int cityId;
    private String name;
    private int parentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;

        Region region = (Region) o;

        return getId() == region.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_REGION);
    }
}
