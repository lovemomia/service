package cn.momia.service.web.ctrl.product;

import cn.momia.common.service.util.TimeUtil;
import cn.momia.common.service.config.Configuration;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.dto.ListDto;
import cn.momia.service.web.ctrl.dto.PagedListDto;
import cn.momia.service.web.ctrl.product.dto.BaseProductDto;
import cn.momia.service.web.ctrl.product.dto.BaseSkuDto;
import cn.momia.service.web.ctrl.product.dto.CustomersDto;
import cn.momia.service.web.ctrl.product.dto.FullProductDto;
import cn.momia.service.web.ctrl.product.dto.PlaymateDto;
import cn.momia.service.product.facade.Product;
import cn.momia.service.web.ctrl.product.dto.ProductDto;
import cn.momia.service.web.ctrl.product.dto.ProductsOfDayDto;
import cn.momia.service.web.ctrl.product.dto.FullSkuDto;
import cn.momia.service.web.ctrl.product.dto.SkuPlaymatesDto;
import cn.momia.service.product.sku.Sku;
import cn.momia.service.user.base.User;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.deal.order.Order;
import cn.momia.service.web.ctrl.AbstractController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
        if (cityId < 0 || isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCount(cityId);
        List<Product> products = productServiceFacade.query(cityId, start, count);

        return ResponseMessage.SUCCESS(buildProductsDto(totalCount, products, start, count));
    }

    private PagedListDto buildProductsDto(long totalCount, List<Product> products, int start, int count) {
        PagedListDto productsDto = new PagedListDto(totalCount, start, count);
        ListDto baseProductsDto = new ListDto();
        for (Product product : products) {
            baseProductsDto.add(new BaseProductDto(product));
        }
        productsDto.addAll(baseProductsDto);

        return productsDto;
    }

    @RequestMapping(value = "/weekend", method = RequestMethod.GET)
    public ResponseMessage getProductsByWeekend(@RequestParam(value = "city") int cityId,
                                                @RequestParam int start,
                                                @RequestParam int count) {
        if (cityId <0 || isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCountByWeekend(cityId);
        List<Product> products = productServiceFacade.queryByWeekend(cityId, start, count);

        return ResponseMessage.SUCCESS(buildWeekendProductsDto(totalCount, products, start, count));
    }

    private PagedListDto buildWeekendProductsDto(long totalCount, List<Product> products, int start, int count) {
        PagedListDto productsDto = new PagedListDto(totalCount, start, count);
        ListDto baseProductsDto = new ListDto();
        for (Product product : products) {
            BaseProductDto baseProductDto = new BaseProductDto(product);
            baseProductDto.setScheduler(product.getWeekendScheduler());
            baseProductsDto.add(baseProductDto);
        }
        productsDto.addAll(baseProductsDto);

        return productsDto;
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public ResponseMessage getProductsByMonth(@RequestParam(value = "city") int cityId, @RequestParam int month) {
        List<Product> products = productServiceFacade.queryByMonth(cityId, month);

        return ResponseMessage.SUCCESS(buildGroupedProductsDto(month, products));
    }

    private ListDto buildGroupedProductsDto(int month, List<Product> products) {
        try {
            ListDto productsDto = new ListDto();

            DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date now = new Date();
            Date currentMonth = monthFormat.parse(TimeUtil.formatMonth(month));

            Date start = now.before(currentMonth) ? currentMonth : now;
            Date end = monthFormat.parse(TimeUtil.formatNextMonth(month));

            int pageSize = Configuration.getInt("Product.Month.PageSize");
            Map<String, ProductsOfDayDto> productsOfDayDtoMap = new HashMap<String, ProductsOfDayDto>();
            for (Product product : products) {
                for (Sku sku : product.getSkus()) {
                    Date startTime = sku.getStartTime();
                    if (startTime.after(start) && startTime.before(end)) {
                        String day = dayFormat.format(startTime);
                        ProductsOfDayDto productsOfDayDto = productsOfDayDtoMap.get(day);
                        if (productsOfDayDto == null) {
                            productsOfDayDto = new ProductsOfDayDto();
                            productsOfDayDtoMap.put(day, productsOfDayDto);

                            productsOfDayDto.setDate(startTime);
                            productsDto.add(productsOfDayDto);
                        }
                        BaseProductDto baseProductDto = new BaseProductDto(product);
                        baseProductDto.setScheduler(sku.getFormatedTime());
                        if (productsOfDayDto.getProducts().size() < pageSize) productsOfDayDto.addProduct(baseProductDto);
                    }
                }
            }

            Collections.sort(productsDto, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    ProductsOfDayDto productsOfDayDto1 = (ProductsOfDayDto) o1;
                    ProductsOfDayDto productsOfDayDto2 = (ProductsOfDayDto) o2;

                    return productsOfDayDto1.getDate().compareTo(productsOfDayDto2.getDate());
                }
            });

            return productsDto;
        } catch (ParseException e) {
            throw new MomiaFailedException("获取数据失败");
        }
    }

    @RequestMapping(value = "/leader", method = RequestMethod.GET)
    public ResponseMessage getProductsNeedLeader(@RequestParam(value = "city") int cityId,
                                                 @RequestParam int start,
                                                 @RequestParam int count) {
        if (cityId < 0 || isInvalidLimit(start, count)) return ResponseMessage.SUCCESS(PagedListDto.EMPTY);

        long totalCount = productServiceFacade.queryCountNeedLeader(cityId);
        List<Product> products = productServiceFacade.queryNeedLeader(cityId, start, count);

        return ResponseMessage.SUCCESS(buildProductsDto(totalCount, products, start, count));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@RequestParam(defaultValue = "") String utoken,
                                      @PathVariable long id,
                                      @RequestParam(defaultValue = "true") boolean full) {
        Product product = productServiceFacade.get(id);
        if (!product.exists()) return ResponseMessage.FAILED("活动不存在");

        ProductDto productDto = full ? new FullProductDto(product) : new BaseProductDto(product);

        if (!StringUtils.isBlank(utoken)) {
            User user = userServiceFacade.getUserByToken(utoken);
            if (user.exists()) {
                ((BaseProductDto) productDto).setFavored(productServiceFacade.isFavoried(user.getId(), id));
            }
        }

        return ResponseMessage.SUCCESS(productDto);
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        List<Sku> skus = productServiceFacade.getSkus(id);
        return ResponseMessage.SUCCESS(buildSkusDto(skus));
    }

    private ListDto buildSkusDto(List<Sku> skus) {
        ListDto skusDto = new ListDto();

        skus = Sku.filter(skus);
        for (Sku sku : skus) {
            skusDto.add(new FullSkuDto(sku));
        }

        return skusDto;
    }

    @RequestMapping(value = "/{id}/sku/leader", method = RequestMethod.GET)
    public ResponseMessage getProductSkusWithLeaders(@PathVariable long id) {
        List<Sku> skus = Sku.filter(productServiceFacade.getSkus(id));

        ListDto skusDto = new ListDto();
        for (Sku sku : skus) {
            skusDto.add(new BaseSkuDto(sku, userServiceFacade.getLeaderInfo(sku.getLeaderUserId())));
        }

        return ResponseMessage.SUCCESS(skusDto);
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (id <= 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        List<Order> orders = dealServiceFacade.queryDistinctCustomerOrderByProduct(id, start, count);
        if (orders.isEmpty()) return ResponseMessage.SUCCESS(new CustomersDto("目前还没有人参加", null));

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

        return ResponseMessage.SUCCESS(new CustomersDto("玩伴信息", avatars));
    }

    @RequestMapping(value = "/{id}/playmate", method = RequestMethod.GET)
    public ResponseMessage getProductPlaymates(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        if (id <= 0 || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        List<Sku> skus = querySkus(id, start, count);
        if (skus.isEmpty()) return ResponseMessage.EMPTY_ARRAY;

        List<Order> orders = queryOrders(id, skus);
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

        return ResponseMessage.SUCCESS(buildPlaymates(skus, skuOrdersMap, skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));
    }

    private List<Sku> querySkus(long id, int start, int count) {
        List<Sku> skus = productServiceFacade.getSkus(id);
        skus = Sku.sortByStartTime(skus);

        List<Sku> result = new ArrayList<Sku>();
        for (int i = start; i < Math.min(skus.size(), start + count); i++) {
            result.add(skus.get(i));
        }

        return result;
    }

    private List<Order> queryOrders(long id, List<Sku> skus) {
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
        List<SkuPlaymatesDto> skusPlaymatesDto = new ArrayList<SkuPlaymatesDto>();
        for (Sku sku : skus) {
            try {
                SkuPlaymatesDto skuPlaymatesDto = new SkuPlaymatesDto();
                skuPlaymatesDto.setTime(sku.getFormatedTime());
                skuPlaymatesDto.setJoined(formatJoined(skuOrdersMap.get(sku.getId())));
                skuPlaymatesDto.setPlaymates(extractPlayMates(sku.getId(), skuCustomerIdsMap, customerPrticipantsIdsMap, customersMap, participantsMap));

                skusPlaymatesDto.add(skuPlaymatesDto);
            } catch (Exception e) {
                LOGGER.error("fail to build playmate for sku: {}", sku.getId(), e);
            }
        }

        return skusPlaymatesDto;
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
        int pageSize = Configuration.getInt("Product.Playmate.PageSize");
        List<PlaymateDto> playmatesDto = new ArrayList<PlaymateDto>();
        Set<Long> customerIds = skuCustomerIdsMap.get(skuId);
        if (customerIds != null) {
            for (long customerId : customerIds) {
                PlaymateDto playmateDto = new PlaymateDto();
                User customer = customersMap.get(customerId);
                if (customer == null) continue;
                playmateDto.setId(customer.getId());
                playmateDto.setNickName(customer.getNickName());
                playmateDto.setAvatar(customer.getAvatar());

                List<String> children = new ArrayList<String>();
                Set<Long> customerPrticipantsIds = customerPrticipantsIdsMap.get(customerId);
                if (customerPrticipantsIds != null) {
                    for (long participantId : customerPrticipantsIds) {
                        Participant participant = participantsMap.get(participantId);
                        if (participant != null && participant.isChild()) {
                            children.add(participant.getDesc());
                        }
                    }
                }
                playmateDto.setChildren(children);

                if (playmatesDto.size() < pageSize) playmatesDto.add(playmateDto);
            }
        }

        return playmatesDto;
    }

    @RequestMapping(value = "/{id}/favor", method = RequestMethod.POST)
    public ResponseMessage favor(@RequestParam String utoken, @PathVariable long id){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Product product = productServiceFacade.get(id, true);
        if (!product.exists()) return ResponseMessage.FAILED("添加收藏失败");

        if (!productServiceFacade.favor(user.getId(), id)) return ResponseMessage.FAILED("添加收藏失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/{id}/unfavor", method = RequestMethod.POST)
    public ResponseMessage unFavor(@RequestParam String utoken, @PathVariable long id){
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!productServiceFacade.unFavor(user.getId(), id)) return ResponseMessage.FAILED("取消收藏失败");
        return ResponseMessage.SUCCESS;
    }
}
