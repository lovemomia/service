package cn.momia.mapi.api.v1.dto.misc;

import cn.momia.common.config.Configuration;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
import cn.momia.common.web.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProductUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductUtil.class);
    private static Configuration conf;

    public void setConf(Configuration conf) {
        ProductUtil.conf = conf;
    }

    public static ListDto extractProductsData(JSONArray productsJson) {
        ListDto products = new ListDto();

        for (int i = 0; i < productsJson.size(); i++) {
            try {
                products.add(extractProductData(productsJson.getJSONObject(i), false));
            } catch (Exception e) {
                LOGGER.error("fail to parse product: ", productsJson.getJSONObject(i), e);
            }
        }

        return products;
    }

    public static ProductDto extractProductData(JSONObject productJson, boolean extractExtraInfo) {
        ProductDto product = new ProductDto();

        JSONObject placeJson = productJson.getJSONObject("place");

        product.setId(productJson.getLong("id"));
        product.setCover(ImageFile.url(productJson.getString("cover")));
        product.setThumb(productJson.getString("thumb"));
        product.setTitle(productJson.getString("title"));
        product.setAbstracts(productJson.getString("abstracts"));
        product.setJoined(productJson.getInteger("joined"));
        product.setSales(productJson.getInteger("sales"));
        product.setSoldOut(productJson.getBoolean("sales"));
        product.setPrice(productJson.getBigDecimal("minPrice"));
        product.setCrowd(productJson.getString("crowd"));
        product.setScheduler(productJson.getString("scheduler"));
        product.setAddress(placeJson.getString("address"));
        product.setPoi(StringUtils.join(new Object[] { placeJson.getDouble("lng"), placeJson.getDouble("lat") }, ":"));
        product.setTags(productJson.getJSONArray("tags"));
        product.setStartTime(productJson.getDate("startTime"));
        product.setEndTime(productJson.getDate("endTime"));

        if (extractExtraInfo) {
            product.setImgs(extractProductImgs(productJson));
            product.setContent(extractProductContent(productJson));
        }

        return product;
    }

    private static List<String> extractProductImgs(JSONObject productJson) {
        List<String> imgs = new ArrayList<String>();
        JSONArray imgJson = productJson.getJSONArray("imgs");
        for (int i = 0; i < imgJson.size(); i++) {
            imgs.add(ImageFile.url(imgJson.getJSONObject(i).getString("url")));
        }

        return imgs;
    }

    private static JSONArray extractProductContent(JSONObject productJson) {
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

    public static String buildUrl(long id) {
        return conf.getString("Product.Url") + "?id=" + id;
    }
}
