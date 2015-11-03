package cn.momia.api.course.dto;

public class CourseSkuDto {
    private long id;
    private CoursePlaceDto place;
    private int stock;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CoursePlaceDto getPlace() {
        return place;
    }

    public void setPlace(CoursePlaceDto place) {
        this.place = place;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
