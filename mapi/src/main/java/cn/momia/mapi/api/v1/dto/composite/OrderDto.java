package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;

import java.util.ArrayList;

public class OrderDto implements Dto {
    public static class Orders extends ArrayList<OrderDto> implements Dto {}

    public static class Product implements Dto {
        private long productId;
        private long skuId;
        private String title;
        private String time;
        private String participants;

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public long getSkuId() {
            return skuId;
        }

        public void setSkuId(long skuId) {
            this.skuId = skuId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getParticipants() {
            return participants;
        }

        public void setParticipants(String participants) {
            this.participants = participants;
        }
    }

    private long id;
    private int count;
    private float totalFee;
    private Product product;

    public long getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public float getTotalFee() {
        return totalFee;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderDto(long id, int count, float totalFee) {
        this.id = id;
        this.count = count;
        this.totalFee = totalFee;
    }
}
