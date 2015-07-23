package cn.momia.mapi.api.v1.dto.product;

import cn.momia.common.web.img.ImageFile;
import cn.momia.mapi.api.v1.dto.misc.ProductUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailDto extends BaseProductDto {
    private List<String> imgs;
    private JSONArray content;

    private JSONObject customers;
    private String url;

    public List<String> getImgs() {
        return imgs;
    }

    public JSONArray getContent() {
        return content;
    }

    public JSONObject getCustomers() {
        return customers;
    }

    public String getUrl() {
        return url;
    }

    public void setExtraInfo(JSONObject productJson, JSONObject customersJson) {
        this.imgs = processImgs(productJson);
        this.content = processContent(productJson);

        // 1.0版本根据soldOut来判断是否可以购买，为了兼容1.0版本
        if (!isOpened()) setSoldOut(true);

        this.customers = processAvatars(customersJson);
        this.url = ProductUtil.buildUrl(getId());
    }

    private static List<String> processImgs(JSONObject productJson) {
        List<String> imgs = new ArrayList<String>();
        JSONArray imgJson = productJson.getJSONArray("imgs");
        for (int i = 0; i < imgJson.size(); i++) {
            imgs.add(ImageFile.url(imgJson.getJSONObject(i).getString("url")));
        }

        return imgs;
    }

    private static JSONArray processContent(JSONObject productJson) {
        JSONArray contentJson = productJson.getJSONArray("content");
        for (int i = 0; i < contentJson.size(); i++) {
            JSONObject contentBlockJson = contentJson.getJSONObject(i);
            JSONArray bodyJson = contentBlockJson.getJSONArray("body");
            for (int j = 0; j < bodyJson.size(); j++) {
                JSONObject bodyBlockJson = bodyJson.getJSONObject(j);
                String img = bodyBlockJson.getString("img");
                if (!StringUtils.isBlank(img)) bodyBlockJson.put("img", ImageFile.url(img));
            }
        }

        return contentJson;
    }

    private JSONObject processAvatars(JSONObject customersJson) {
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
