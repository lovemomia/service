package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProductDetailDto implements Dto {
    private ProductDto productDto;
    private JSONObject customers;

    public long getId() {
        return productDto.getId();
    }

    public String getCover() {
        return productDto.getCover();
    }

    public String getTitle() {
        return productDto.getTitle();
    }

    public int getJoined() {
        return productDto.getJoined();
    }

    public BigDecimal getPrice() {
        return productDto.getPrice();
    }

    public String getCrowd() {
        return productDto.getCrowd();
    }

    public String getScheduler() {
        return productDto.getScheduler();
    }

    public String getAddress() {
        return productDto.getAddress();
    }

    public String getPoi() {
        return productDto.getPoi();
    }

    public JSONArray getTags() {
        return productDto.getTags();
    }

    @JSONField(format = "yyyy-MM-dd hh:mm:ss") public Date getStartTime() {
        return productDto.getStartTime();
    }

    @JSONField(format = "yyyy-MM-dd hh:mm:ss") public Date getEndTime() {
        return productDto.getEndTime();
    }

    public boolean isSoldOut() {
        return productDto.isSoldOut();
    }

    public List<String> getImgs() {
        return productDto.getImgs();
    }

    public JSONArray getContent() {
        return productDto.getContent();
    }

    public JSONObject getCustomers() {
        return customers;
    }

    public ProductDetailDto(JSONObject productJson, JSONObject customersJson) {
        this.productDto = ProductUtil.extractProductData(productJson, true);
        this.customers = processImages(customersJson);
    }

    private JSONObject processImages(JSONObject customersJson) {
        JSONArray avatarsJson = customersJson.getJSONArray("avatars");
        if (avatarsJson != null) {
            for (int i = 0; i < avatarsJson.size(); i++) {
                String avatar = avatarsJson.getString(i);
                avatarsJson.set(i, ImageFile.url(avatar));
            }
        }

        return customersJson;
    }
}
