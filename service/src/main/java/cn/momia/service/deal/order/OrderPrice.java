package cn.momia.service.deal.order;

public class OrderPrice {
    private float price;
    private int count;

    public float getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public OrderPrice(float price, int count) {
        this.price = price;
        this.count = count;
    }
}
