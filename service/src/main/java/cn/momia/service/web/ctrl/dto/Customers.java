package cn.momia.service.web.ctrl.dto;

import java.io.Serializable;
import java.util.List;

public class Customers implements Serializable {
    private String text;
    private List<String> avatars;

    public String getText() {
        return text;
    }

    public List<String> getAvatars() {
        return avatars;
    }

    public Customers(String text, List<String> avatars) {
        this.text = text;
        this.avatars = avatars;
    }
}
