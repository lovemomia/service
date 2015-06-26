package cn.momia.mapi.api.v1.dto.base;

public class BannerDto implements Dto {
    private String cover;
    private String action;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
