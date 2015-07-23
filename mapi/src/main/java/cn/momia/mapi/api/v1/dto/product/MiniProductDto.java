package cn.momia.mapi.api.v1.dto.product;

import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;

public class MiniProductDto implements Dto {
    private long id;
    private String thumb;
    private String title;
    private String abstracts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getUrl() {
        return ProductUtil.buildUrl(this.id);
    }
}
