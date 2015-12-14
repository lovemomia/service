package cn.momia.api.course.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class Favorite {
    public static class Type {
        public static final int COURSE = 1;
        public static final int SUBJECT = 2;
    }

    private long id;
    private int type;
    @JSONField(serialize = false) private long userId;
    @JSONField(serialize = false) private long refId;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public JSONObject getRef() {
        return ref;
    }

    public void setRef(JSONObject ref) {
        this.ref = ref;
    }
}
