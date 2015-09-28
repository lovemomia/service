package cn.momia.service.product.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.banner.Banner;
import cn.momia.service.banner.BannerService;
import cn.momia.service.product.facade.Product;
import cn.momia.service.topic.Topic;
import cn.momia.service.topic.TopicGroup;
import cn.momia.service.topic.TopicService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topic")
public class TopicController extends ProductRelatedController {
    private static final int MAX_BANNER_COUNT = 20;

    @Autowired private BannerService bannerService;
    @Autowired private TopicService topicService;

    @RequestMapping(value = "/banner", method = RequestMethod.GET)
    public MomiaHttpResponse listBanners(@RequestParam(value = "city") int cityId, @RequestParam int count) {
        if (cityId < 0 || count <= 0 || count > MAX_BANNER_COUNT) return MomiaHttpResponse.EMPTY_ARRAY;
        return MomiaHttpResponse.SUCCESS(bannerService.list(cityId, count));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse topic(@PathVariable long id) {
        Topic topic = topicService.get(id);
        if (!topic.exists()) return MomiaHttpResponse.FAILED("无效的专题");

        JSONObject topicJson = new JSONObject();
        topicJson.put("id", topic.getId());
        topicJson.put("cover", topic.getCover());
        topicJson.put("title", topic.getTitle());

        List<TopicGroup> topicGroups = topicService.listTopicGroups(id);
        List<Long> groupIds = new ArrayList<Long>();
        for (TopicGroup topicGroup : topicGroups) groupIds.add(topicGroup.getId());

        Map<Long, List<Long>> groupedProductIds = topicService.queryProductIds(groupIds);
        Map<Long, List<Product>> groupedProducts = productServiceFacade.listGrouped(groupedProductIds);

        JSONArray groupedProductsJson = new JSONArray();
        for (TopicGroup topicGroup : topicGroups) {
            List<Product> products = groupedProducts.get(topicGroup.getId());
            if (products == null) continue;

            JSONObject productsJson = new JSONObject();
            productsJson.put("title", topicGroup.getTitle());
            productsJson.put("products", buildProductDtos(products, Product.Type.BASE, false));

            groupedProductsJson.add(productsJson);
        }

        topicJson.put("groups", groupedProductsJson);

        return MomiaHttpResponse.SUCCESS(topicJson);
    }
}
