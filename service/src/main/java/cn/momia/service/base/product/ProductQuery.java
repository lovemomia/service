package cn.momia.service.base.product;

public class ProductQuery {
    private int cityId;

    public ProductQuery(int cityId, String query) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (cityId == 0) builder.append("cityId=0");
        else builder.append("(").append("cityId=").append(cityId).append(" OR cityId=0)");

        return builder.toString();
    }
}
