package cn.momia.mapi.api.v1.dto;

public class OrderDto implements Dto {
    public static class Product implements Dto {
        private String title;
        private String time;
        private String participants;

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

    private int count;
    private float totalFee;
    private Product product;

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

    public OrderDto(int count, float totalFee) {
        this.count = count;
        this.totalFee = totalFee;
    }
}
