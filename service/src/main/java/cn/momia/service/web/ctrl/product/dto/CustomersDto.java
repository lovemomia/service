package cn.momia.service.web.ctrl.product.dto;

import java.io.Serializable;
import java.util.List;

public class CustomersDto implements Serializable {
    private String text;
    private List<String> avatars;

    public String getText() {
        return text;
    }

    public List<String> getAvatars() {
        return avatars;
    }

    public CustomersDto(String text, List<String> avatars) {
        this.text = text;
        this.avatars = avatars;
    }
}
