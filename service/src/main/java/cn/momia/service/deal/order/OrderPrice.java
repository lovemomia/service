package cn.momia.service.deal.order;

public class OrderPrice {
    private float price;
    private int count;
    private int adult;
    private int child;

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

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public OrderPrice() {}

    public OrderPrice(float price, int count, int adult, int child) {
        this.price = price;
        this.count = count;
        this.adult = adult;
        this.child = child;
    }
}
