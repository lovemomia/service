package cn.momia.api.base.dto;

public class SortType {
    public static final SortType DEFAULT = new SortType();
    static {
        DEFAULT.setId(0);
        DEFAULT.setText("默认");
    }

    private int id;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
