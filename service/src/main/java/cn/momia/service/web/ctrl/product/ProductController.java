package cn.momia.service.web.ctrl.product;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.product.dto.CustomersDto;
import cn.momia.service.web.ctrl.product.dto.PlaymateDto;
import cn.momia.service.product.Product;
import cn.momia.service.web.ctrl.product.dto.SkuPlaymatesDto;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.deal.order.Order;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam(value = "city") int cityId,
                                       @RequestParam int start,
                                       @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(buildProductsPack(0, new ArrayList<Product>()));

        long totalCount = productServiceFacade.queryCount(cityId);
        List<Product> products = totalCount > 0 ? productServiceFacade.query(cityId, start, count) : new ArrayList<Product>();

        return new ResponseMessage(buildProductsPack(totalCount, products));
    }

    private JSONObject buildProductsPack(long totalCount, List<Product> products) {
        JSONObject productsPackJson = new JSONObject();
        productsPackJson.put("totalCount", totalCount);
        productsPackJson.put("products", products);

        return productsPackJson;
    }

    @RequestMapping(value = "/weekend", method = RequestMethod.GET)
    public ResponseMessage getProductsByWeekend(@RequestParam(value = "city") int cityId,
                                                @RequestParam int start,
                                                @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(buildProductsPack(0, new ArrayList<Product>()));

        long totalCount = productServiceFacade.queryCountByWeekend(cityId);
        List<Product> products = totalCount > 0 ? productServiceFacade.queryByWeekend(cityId, start, count) : new ArrayList<Product>();

        return new ResponseMessage(buildProductsPack(totalCount, products));
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public ResponseMessage getProductsByMonth(@RequestParam(value = "city") int cityId, @RequestParam int month) {
        long totalCount = productServiceFacade.queryCountByMonth(cityId, month);
        List<Product> products = totalCount > 0 ? productServiceFacade.queryByMonth(cityId, month) : new ArrayList<Product>();

        return new ResponseMessage(buildProductsPack(totalCount, products));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        Product product = productServiceFacade.get(id);
        if (!product.exists()) return ResponseMessage.FAILED("活动不存在");

        return new ResponseMessage(product);
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        List<Sku> skus = productServiceFacade.getSkus(id);
        return new ResponseMessage(Sku.filter(skus));
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (id <= 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        List<Order> orders = dealServiceFacade.queryDistinctCustomerOrderByProduct(id, start, count);
        if (orders.isEmpty()) return new ResponseMessage(new CustomersDto("目前还没有人参加", null));

        List<Long> customerIds = new ArrayList<Long>();
        for (Order order : orders) customerIds.add(order.getCustomerId());
        List<User> customers = userServiceFacade.getUsers(customerIds);
        Map<Long, User> customersMap = new HashMap<Long, User>();
        for (User customer : customers) {
            customersMap.put(customer.getId(), customer);
        }

//        int adultCount = 0;
//        int childCount = 0;
        List<String > avatars = new ArrayList<String>();
        for (Order order : orders) {
            long customerId = order.getCustomerId();
            User customer = customersMap.get(customerId);
            if (customer == null) continue;

//            adultCount += order.getAdultCount();
//            childCount += order.getChildCount();
            avatars.add(customer.getAvatar());
        }

//        StringBuilder builder = new StringBuilder();
//        if (adultCount <= 0 && childCount <= 0) builder.append("目前还没有人参加");
//        else if (adultCount > 0 && childCount <= 0) builder.append(adultCount).append("个大人参加");
//        else if (adultCount <= 0 && childCount > 0) builder.append(childCount).append("个孩子参加");
//        else builder.append(childCount).append("个孩子，").append(adultCount).append("个大人参加");

        return new ResponseMessage(new CustomersDto("玩伴信息", avatars));
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

        Map<Long, User> customersMap = new HashMap<Long, User>();
        for (User customer : userServiceFacade.getUsers(customerIds)) {
            customersMap.put(customer.getId(), customer);
        }

        Map<Long, Participant> participantsMap = new HashMap<Long, Participant>();
        for (Participant participant : userServiceFacade.getParticipants(participantIds)) {
            participantsMap.put(participant.getId(), participant);
        }

        return new ResponseMessage(buildPlaymates(skus, skuOrdersMap, skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));
    }

    private List<Sku> extractSkus(long id, int start, int count) {
        List<Sku> skus = productServiceFacade.getSkus(id);
        skus = Sku.sortByStartTime(skus);

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
        List<Order> orders = dealServiceFacade.queryAllCustomerOrderByProduct(id);
        for (Order order : orders) {
            if (skuIds.contains(order.getSkuId())) result.add(order);
        }

        return result;
    }

    private List<SkuPlaymatesDto> buildPlaymates(List<Sku> skus,
                                              Map<Long, List<Order>> skuOrdersMap,
                                              Map<Long, Set<Long>> skuCustomerIdsMap,
                                              Map<Long, Set<Long>> customerPrticipantsIdsMap,
                                              Map<Long, User> customersMap,
                                              Map<Long, Participant> participantsMap) {
        List<SkuPlaymatesDto> skuPlaymates = new ArrayList<SkuPlaymatesDto>();
        for (Sku sku : skus) {
            try {
                SkuPlaymatesDto skuPlaymate = new SkuPlaymatesDto();
                skuPlaymate.setTime(sku.time());
                skuPlaymate.setJoined(formatJoined(skuOrdersMap.get(sku.getId())));
                skuPlaymate.setPlaymates(extractPlayMates(sku.getId(), skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));

                skuPlaymates.add(skuPlaymate);
            } catch (Exception e) {
                LOGGER.error("fail to build playmate for sku: {}", sku.getId(), e);
            }
        }

        return skuPlaymates;
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

    private List<PlaymateDto> extractPlayMates(long skuId,
                                            Map<Long, Set<Long>> skuCustomerIdsMap,
                                            Map<Long, Set<Long>> customerPrticipantsIdsMap,
                                            Map<Long, User> customersMap,
                                            Map<Long, Participant> participantsMap) {
        List<PlaymateDto> playmates = new ArrayList<PlaymateDto>();
        Set<Long> customerIds = skuCustomerIdsMap.get(skuId);
        if (customerIds != null) {
            for (long customerId : customerIds) {
                PlaymateDto playmate = new PlaymateDto();
                User customer = customersMap.get(customerId);
                if (customer == null) continue;
                playmate.setId(customer.getId());
                playmate.setNickName(customer.getNickName());
                playmate.setAvatar(customer.getAvatar());

                List<String> children = new ArrayList<String>();
                Set<Long> customerPrticipantsIds = customerPrticipantsIdsMap.get(customerId);
                if (customerPrticipantsIds != null) {
                    for (long participantId : customerPrticipantsIds) {
                        Participant participant = participantsMap.get(participantId);
                        if (participant != null && participant.child()) {
                            children.add(participant.desc());
                        }
                    }
                }
                playmate.setChildren(children);

                playmates.add(playmate);
            }
        }

        return playmates;
    }
}
