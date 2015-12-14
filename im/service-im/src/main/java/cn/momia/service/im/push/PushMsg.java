package cn.momia.service.im.push;

import com.alibaba.fastjson.JSON;

public class PushMsg {
    private String content;
    private String extra;

    public PushMsg(String content, String extra) {
        this.content = content;
        this.extra = extra;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
