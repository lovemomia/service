package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.dto.Customer;
import cn.momia.service.web.ctrl.dto.Playmate;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductQuery;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.web.ctrl.dto.SkuPlaymate;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired private ProductService productService;

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;
    @Autowired private ParticipantService participantService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam(value = "city") int cityId,
                                       @RequestParam int start,
                                       @RequestParam int count,
                                       @RequestParam(required = false) String query) {
        if (cityId < 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        long totalCount = productService.queryCount(new ProductQuery(cityId, query));
        List<Product> products = productService.query(start, count, new ProductQuery(cityId, query));

        JSONObject productsPackJson = new JSONObject();
        productsPackJson.put("totalCount", totalCount);
        productsPackJson.put("products", products);

        return new ResponseMessage(productsPackJson);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        if (id <= 0) return ResponseMessage.BAD_REQUEST;

        Product product = productService.get(id);
        if (!product.exists()) return ResponseMessage.BAD_REQUEST;

        return new ResponseMessage(product);
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        if (id <= 0) return ResponseMessage.BAD_REQUEST;

        List<Sku> skus = productService.getSkus(id);

        return new ResponseMessage(skus);
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (id <= 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

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

    @RequestMapping(value = "/{id}/playmate", method = RequestMethod.GET)
    public ResponseMessage getProductPlaymates(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (id <= 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        List<Sku> skus = extractSkus(id, start, count);
        if (skus.isEmpty()) return ResponseMessage.EMPTY_ARRAY;

        List<Order> orders = extractOrders(id, skus);
        if (orders.isEmpty()) return ResponseMessage.EMPTY_ARRAY;

        Map<Long, List<Order>> skuOrdersMap = new HashMap<Long, List<Order>>();
        Map<Long, Set<Long>> skuCustomerIdsMap = new HashMap<Long, Set<Long>>();
        Set<Long> customerIds = new HashSet<Long>();
        Set<Long> participantIds = new HashSet<Long>();
        Map<Long, Set<Long>> customerPrticipantsIdsMap = new HashMap<Long, Set<Long>>();

        for (Order order: orders) {
            long skuId = order.getSkuId();
            long customerId = order.getCustomerId();
            List<Long> orderParticipantIds = order.getParticipants();

            List<Order> skuOrders = skuOrdersMap.get(skuId);
            if (skuOrders == null) {
                skuOrders = new ArrayList<Order>();
                skuOrdersMap.put(skuId, skuOrders);
            }
            skuOrders.add(order);

            Set<Long> skuCustomerIds = skuCustomerIdsMap.get(skuId);
            if (skuCustomerIds == null) {
                skuCustomerIds = new HashSet<Long>();
                skuCustomerIdsMap.put(skuId, skuCustomerIds);
            }
            skuCustomerIds.add(customerId);

            customerIds.add(customerId);
            participantIds.addAll(orderParticipantIds);

            Set<Long> customerParticipantsIds = customerPrticipantsIdsMap.get(customerId);
            if (customerParticipantsIds == null) {
                customerParticipantsIds = new HashSet<Long>();
                customerPrticipantsIdsMap.put(customerId, customerParticipantsIds);
            }
            customerParticipantsIds.addAll(orderParticipantIds);
        }

        Map<Long, User> customersMap = userService.get(customerIds);
        Map<Long, Participant> participantsMap = participantService.get(participantIds);

        return new ResponseMessage(buildPlaymates(skus, skuOrdersMap, skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));
    }

    private List<SkuPlaymate> buildPlaymates(List<Sku> skus,
                                             Map<Long, List<Order>> skuOrdersMap,
                                             Map<Long, Set<Long>> skuCustomerIdsMap,
                                             Map<Long, Set<Long>> customerPrticipantsIdsMap,
                                             Map<Long, User> customersMap,
                                             Map<Long, Participant> participantsMap) {
        List<SkuPlaymate> skuPlaymates = new ArrayList<SkuPlaymate>();
        for (Sku sku : skus) {
            if (!sku.exists()) continue;

            try {
                SkuPlaymate skuPlaymate = new SkuPlaymate();
                skuPlaymate.setTime(sku.formatTime());
                skuPlaymate.setJoined(formatJoined(skuOrdersMap.get(sku.getId())));
                skuPlaymate.setPlaymates(extractPlayMates(sku.getId(), skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));

                skuPlaymates.add(skuPlaymate);
            } catch (Exception e) {
                LOGGER.error("fail to build playmate for sku: {}", sku.getId(), e);
            }
        }

        return skuPlaymates;
    }

    private List<Sku> extractSkus(long id, int start, int count) {
        List<Sku> skus = productService.getSkus(id);
        skus = Sku.sortByTime(skus);

        List<Sku> result = new ArrayList<Sku>();
        for (int i = start; i < Math.min(skus.size(), start + count); i++) {
            result.add(skus.get(i));
        }

        return result;
    }

    private List<Order> extractOrders(long id, List<Sku> skus) {
        Set<Long> skuIds = new HashSet<Long>();
        for (Sku sku : skus) {
            skuIds.add(sku.getId());
        }

        List<Order> result = new ArrayList<Order>();
        List<Order> orders = orderService.queryAllCustomerOrderByProduct(id);
        for (Order order : orders) {
            if (skuIds.contains(order.getSkuId())) result.add(order);
        }

        return result;
    }

    private String formatJoined(List<Order> orders) {
        int adultCount = 0;
        int childCount = 0;
        if (orders != null) {
            for (Order order : orders) {
                adultCount += order.getAdultCount();
                childCount += order.getChildCount();
            }
        }

        int totalCount = adultCount + childCount;

        StringBuilder builder = new StringBuilder();
        builder.append(totalCount).append("人已报名");
        if (adultCount > 0 || childCount > 0) {
            builder.append("(");
            if (adultCount > 0 && childCount <= 0) {
                builder.append(adultCount).append("成人");
            } else if (adultCount <= 0 && childCount > 0) {
                builder.append(childCount).append("儿童");
            } else {
                builder.append(adultCount).append("成人")
                        .append("，")
                        .append(childCount).append("儿童");
            }
            builder.append(")");
        }

        return builder.toString();
    }

    private List<Playmate> extractPlayMates(long skuId,
                                            Map<Long, Set<Long>> skuCustomerIdsMap,
                                            Map<Long, Set<Long>> customerPrticipantsIdsMap,
                                            Map<Long, User> customersMap,
                                            Map<Long, Participant> participantsMap) {
        List<Playmate> playmates = new ArrayList<Playmate>();
        Set<Long> customerIds = skuCustomerIdsMap.get(skuId);
        if (customerIds != null) {
            for (long customerId : customerIds) {
                Playmate playmate = new Playmate();
                User customer = customersMap.get(customerId);
                if (customer == null) continue;
                playmate.setId(customer.getId());
                playmate.setNickName(customer.getNickName());
                playmate.setAvatar(customer.getAvatar());

                List<String> children = new ArrayList<String>();
                Set<Long> customerPrticipantsIds = customerPrticipantsIdsMap.get(customerId);
                for (long participantId : customerPrticipantsIds) {
                    Participant participant = participantsMap.get(participantId);
                    if (participant.child()) {
                        children.add(participant.desc());
                    }
                }
                playmate.setChildren(children);

                playmates.add(playmate);
            }
        }

        return playmates;
    }
}
