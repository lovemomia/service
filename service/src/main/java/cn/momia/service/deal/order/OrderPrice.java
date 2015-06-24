package cn.momia.service.deal.order;

public class OrderPrice {
    private float price;
    private int count;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public OrderPrice() {}

    public OrderPrice(float price, int count) {
        this.price = price;
        this.count = count;
    }
}
