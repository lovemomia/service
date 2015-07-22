package cn.momia.mapi.api.v1.dto.deal;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import com.alibaba.fastjson.JSONObject;

public class PaymentResultDto implements Dto {
    private String thumb;
    private String title;
    private String abstracts;
    private String url;

    public String getThumb() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public String getUrl() {
        return url;
    }

    public PaymentResultDto(JSONObject productJson) {
        this.thumb = ImageFile.url(productJson.getString("thumb"));
        this.title = productJson.getString("title");
        this.abstracts = productJson.getString("abstracts");
        this.url = ProductUtil.buildUrl(productJson.getLong("id"));
    }
}
