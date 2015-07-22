package cn.momia.service.web.ctrl.product.dto;

import cn.momia.common.web.img.ImageFile;

import java.io.Serializable;
import java.util.List;

public class PlaymateDto implements Serializable {
    private long id;
    private String nickName;
    private String avatar;
    private List<String> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return ImageFile.url(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }
}