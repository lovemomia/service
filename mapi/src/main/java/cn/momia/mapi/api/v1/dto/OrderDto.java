package cn.momia.mapi.api.v1.dto;

public class OrderDto implements Dto {
    private int count;
    private float totalFee;

    public int getCount() {
        return count;
    }

    public float getTotalFee() {
        return totalFee;
    }

    public OrderDto(int count, float totalFee) {
        this.count = count;
        this.totalFee = totalFee;
    }
}
