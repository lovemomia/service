package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.customer.Customer;
import cn.momia.service.base.place.Place;
import cn.momia.service.base.place.PlaceService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductQuery;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController extends AbstractController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam int start, @RequestParam int count, @RequestParam(required = false) String query) {
        List<Product> products = productService.query(start, count, new ProductQuery(query));
        List<Long> productIds = new ArrayList<Long>();
        for (Product product : products) productIds.add(product.getId());
        Map<Long, Place> placesOfProducts = placeService.queryByProducts(productIds);
        Map<Long, List<Sku>> skusOfProducts = skuService.queryByProducts(productIds);

        return new ResponseMessage(buildProductsResponse(products, placesOfProducts, skusOfProducts));
    }

    private JSONArray buildProductsResponse(List<Product> products, Map<Long, Place> placesOfProducts, Map<Long, List<Sku>> skusOfProducts) {
        JSONArray data = new JSONArray();
        for (Product product : products) {
            JSONObject productData = new JSONObject();
            productData.put("product", product);
            productData.put("place", placesOfProducts.get(product.getId()));
            productData.put("skus", skusOfProducts.get(product.getId()));

            data.add(productData);
        }

        return data;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        Product product = productService.get(id);
        if (!product.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "product not exists");

        return new ResponseMessage(product);
    }

    @RequestMapping(value = "/{id}/place", method = RequestMethod.GET)
    public ResponseMessage getProductPlace(@PathVariable long id) {
        Place place = placeService.queryByProduct(id);
        if (!place.exists()) return new ResponseMessage(ErrorCode.NOT_FOUND, "place not found");

        return new ResponseMessage(place);
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        List<Sku> skus = skuService.queryByProduct(id);

        return new ResponseMessage(skus);
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        List<Order> orders = orderService.queryDistinctCustomerOrderByProduct(id, start, count);
        List<Customer> customers = new ArrayList<Customer>();
        List<Long> customerIds = new ArrayList<Long>();
        List<Long> participantIds = new ArrayList<Long>();
        Map<Long, List<Long>> participantsMap = new HashMap<Long, List<Long>>();
        for (Order order : orders) {
            Customer customer = new Customer();
            customer.setUserId(order.getCustomerId());
            customer.setOrderDate(order.getAddTime());
            customer.setOrderStatus(order.getStatus());
            customers.add(customer);
            customerIds.add(order.getCustomerId());
            participantIds.addAll(order.getParticipants());
            participantsMap.put(order.getCustomerId(), order.getParticipants());
        }

        Map<Long, User> users = userService.get(customerIds);
        Map<Long, Participant> participants = participantService.get(participantIds);

        List<Customer> validCustomers = new ArrayList<Customer>();
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            User user = users.get(customer.getUserId());
            if (user == null || !user.exists()) continue;
            customer.setAvatar(user.getAvatar());
            customer.setName(user.getName());

            List<Participant> participantList = new ArrayList<Participant>();
            for (long participantId : participantsMap.get(customer.getUserId())) {
                participantList.add(participants.get(participantId));
            }
            customer.setParticipants(participantList);

            validCustomers.add(customer);
        }

        return new ResponseMessage(validCustomers);
    }
}
