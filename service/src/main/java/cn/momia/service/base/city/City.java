package cn.momia.service.base.city;

public class City {
    public static final City NOT_EXIST_CITY = new City();
    static {
        NOT_EXIST_CITY.setId(0);
        NOT_EXIST_CITY.setName("不存在");
    }

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;

        City city = (City) o;

        return getId() == city.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_CITY);
    }
}
