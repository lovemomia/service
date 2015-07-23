package cn.momia.mapi.api.v1.dto.misc;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ProductUtil {
    private static Configuration conf;

    public void setConf(Configuration conf) {
        ProductUtil.conf = conf;
    }

    public static String buildUrl(long id) {
        return conf.getString("Product.Url") + "?id=" + id;
    }

    public static JSONArray processImage(JSONArray productsJson) {
        for (int i = 0; i < productsJson.size(); i++) {
            JSONObject productJson = productsJson.getJSONObject(i);
            productJson.put("thumb", ImageFile.url(productJson.getString("thumb")));
            productJson.put("cover", ImageFile.url(productJson.getString("cover")));
        }

        return productsJson;
    }
}
