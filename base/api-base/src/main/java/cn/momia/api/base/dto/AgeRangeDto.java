package cn.momia.api.base.dto;

public class AgeRangeDto {
    public static final AgeRangeDto DEFAULT = new AgeRangeDto();
    static {
        DEFAULT.setId(0);
        DEFAULT.setMin(3);
        DEFAULT.setMax(6);
    }

    private int id;
    private int min;
    private int max;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
