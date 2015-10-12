package cn.momia.api.base.dto;

public class SortTypeDto {
    public static final SortTypeDto DEFAULT = new SortTypeDto();
    static {
        DEFAULT.setId(0);
        DEFAULT.setText("无");
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
