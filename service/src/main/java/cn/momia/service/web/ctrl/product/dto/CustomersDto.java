package cn.momia.service.web.ctrl.product.dto;

import cn.momia.service.web.ctrl.dto.Dto;

import java.util.ArrayList;
import java.util.List;

public class CustomersDto implements Dto {
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
        if (avatars != null) {
            this.avatars = new ArrayList<String>();
            for (String avatar : avatars) {
                this.avatars.add(avatar);
            }
        }
    }
}
