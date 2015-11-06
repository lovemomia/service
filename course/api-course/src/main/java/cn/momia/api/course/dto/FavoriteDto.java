package cn.momia.api.course.dto;

import com.alibaba.fastjson.JSONObject;

public class FavoriteDto {
    public static class Type {
        public static final int COURSE = 1;
        public static final int SUBJECT = 2;
    }

    private long id;
    private int type;
    private JSONObject ref;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getRef() {
        return ref;
    }

    public void setRef(JSONObject ref) {
        this.ref = ref;
    }
}
