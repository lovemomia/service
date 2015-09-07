package cn.momia.service.product.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.product.facade.Product;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.product.topic.Topic;
import cn.momia.service.product.topic.TopicGroup;
import cn.momia.service.product.web.ctrl.dto.BaseProductDto;
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
public class TopicController extends BaseController {
    @Autowired private ProductServiceFacade productServiceFacade;

    @RequestMapping(value = "/banner", method = RequestMethod.GET)
    public MomiaHttpResponse listBanners(@RequestParam(value = "city") int cityId, @RequestParam int count) {
        return MomiaHttpResponse.SUCCESS(productServiceFacade.getBanners(cityId, count));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MomiaHttpResponse topic(@PathVariable long id) {
        Topic topic = productServiceFacade.getTopic(id);
        if (!topic.exists()) return MomiaHttpResponse.BAD_REQUEST;

        JSONObject topicJson = new JSONObject();
        topicJson.put("id", topic.getId());
        topicJson.put("cover", topic.getCover());
        topicJson.put("title", topic.getTitle());

        List<TopicGroup> topicGroups = productServiceFacade.getTopicGroups(id);
        List<Long> groupIds = new ArrayList<Long>();
        for (TopicGroup topicGroup : topicGroups) groupIds.add(topicGroup.getId());

        Map<Long, List<Product>> groupedProducts = productServiceFacade.queryByTopicGroups(groupIds);

        JSONArray groupedProductsJson = new JSONArray();
        for (TopicGroup topicGroup : topicGroups) {
            List<Product> products = groupedProducts.get(topicGroup.getId());
            if (products == null) continue;

            JSONObject productsJson = new JSONObject();
            productsJson.put("title", topicGroup.getTitle());
            productsJson.put("products", BaseProductDto.toDtos(products));

            groupedProductsJson.add(productsJson);
        }

        topicJson.put("groups", groupedProductsJson);

        return MomiaHttpResponse.SUCCESS(topicJson);
    }
}
