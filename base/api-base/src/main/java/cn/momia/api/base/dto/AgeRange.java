package cn.momia.api.base.dto;

public class AgeRange {
    public static final AgeRange DEFAULT = new AgeRange();
    static {
        DEFAULT.setId(0);
        DEFAULT.setMin(1);
        DEFAULT.setMax(100);
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

    public String getText() {
        if (id == 0) return "全部";
        if (min <= 0 && max <= 0) return "";
        if (min <= 0) return max + "岁";
        if (max <= 0) return min + "岁";
        if (min == max) return min + "岁";
        return min + "-" + max + "岁";
    }
}
