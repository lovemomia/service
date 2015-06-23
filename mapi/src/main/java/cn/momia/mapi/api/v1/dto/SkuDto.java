package cn.momia.mapi.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

public class SkuDto implements Dto {
    public static class Skus extends ArrayList<SkuDto> implements Dto {}

    public static class SkuPrice implements Dto {
        private String name;
        private float price;

        public SkuPrice(String name, float price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public float getPrice() {
            return price;
        }
    }

    private String time;
    private List<SkuPrice> prices;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<SkuPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<SkuPrice> prices) {
        this.prices = prices;
    }
}
