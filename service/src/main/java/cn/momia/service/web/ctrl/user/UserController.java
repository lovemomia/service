package cn.momia.service.web.ctrl.user;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.user.base.User;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.promo.coupon.Coupon;
import cn.momia.service.promo.coupon.CouponService;
import cn.momia.service.promo.coupon.UserCoupon;
import cn.momia.service.user.participant.Participant;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController extends UserRelatedController {
    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;

    @Autowired private CouponService couponService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @RequestParam int status,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        if (StringUtils.isBlank(utoken) || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long totalCount = orderService.queryCountByUser(user.getId(), status);
        List<Order> orders = totalCount > 0 ? orderService.queryByUser(user.getId(), status, start, count) : new ArrayList<Order>();

        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) {
            productIds.add(order.getProductId());
        }

        List<Product> products = productIds.isEmpty() ? new ArrayList<Product>() : productService.get(productIds);

        return new ResponseMessage(buildUserOrders(totalCount, orders, products));
    }

    private JSONObject buildUserOrders(long totalCount, List<Order> orders, List<Product> products) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        Map<Long, Sku> skuMap = new HashMap<Long, Sku>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
            for (Sku sku : product.getSkus()) {
                skuMap.put(sku.getId(), sku);
            }
        }

        JSONObject ordersPackJson = new JSONObject();
        ordersPackJson.put("totalCount", totalCount);

        JSONArray ordersJson = new JSONArray();
        for (Order order : orders) {
            JSONObject orderJson = new JSONObject();
            orderJson.put("order", order);

            Product product = productMap.get(order.getProductId());
            if (product != null) {
                orderJson.put("cover", product.getCover());
                orderJson.put("title", product.getTitle());
            }

            Sku sku = skuMap.get(order.getSkuId());
            if (sku != null) orderJson.put("time", sku.time());

            ordersJson.add(orderJson);
        }
        ordersPackJson.put("orders", ordersJson);

        return ordersPackJson;
    }

    @RequestMapping(value = "/order/{oid}", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @PathVariable(value = "oid") long orderId,
                                           @RequestParam(value = "pid") long productId) {
        if (StringUtils.isBlank(utoken) || orderId <= 0 || productId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Order order = orderService.get(orderId);
        if (!order.exists()) return ResponseMessage.BAD_REQUEST;

        Product product = productService.get(productId);
        if (!product.exists()) return ResponseMessage.BAD_REQUEST;

        return new ResponseMessage(buildOrderDetail(order, product));
    }

    private JSONObject buildOrderDetail(Order order, Product product) {
        JSONObject orderDetailJson = new JSONObject();
        orderDetailJson.put("order", order);
        orderDetailJson.put("cover", product.getCover());
        orderDetailJson.put("title", product.getTitle());
        orderDetailJson.put("scheduler", product.getScheduler());
        orderDetailJson.put("address", product.getPlace().getAddress());
        orderDetailJson.put("price", product.getMinPrice());

        return orderDetailJson;
    }

    @RequestMapping(value = "/coupon", method = RequestMethod.GET)
    public ResponseMessage getCouponsOfUser(@RequestParam String utoken,
                                            @RequestParam(value = "oid") long orderId,
                                            @RequestParam int status,
                                            @RequestParam int start,
                                            @RequestParam int count) {
        if (StringUtils.isBlank(utoken) || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        int totalCount = couponService.queryCountByUser(user.getId(), orderId, status);
        List<UserCoupon> userCoupons = totalCount > 0 ? couponService.queryByUser(user.getId(), orderId, status, start, count) : new ArrayList<UserCoupon>();

        List<Integer> couponIds = new ArrayList<Integer>();
        for (UserCoupon userCoupon : userCoupons) couponIds.add(userCoupon.getCouponId());
        Map<Integer, Coupon> couponsMap = couponService.getCoupons(couponIds);

        return new ResponseMessage(buildCoupons(totalCount, userCoupons, couponsMap));
    }

    private JSONObject buildCoupons(int totalCount, List<UserCoupon> userCoupons, Map<Integer, Coupon> couponsMap) {
        JSONObject couponsPackJson = new JSONObject();
        couponsPackJson.put("totalCount", totalCount);
        couponsPackJson.put("userCoupons", userCoupons);
        couponsPackJson.put("coupons", couponsMap);

        return couponsPackJson;
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public ResponseMessage updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserNickName(user.getId(), nickName);
        if (!successful) return ResponseMessage.FAILED("更新用户昵称失败");

        user.setNickName(nickName);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserAvatar(user.getId(), avatar);
        if (!successful) return ResponseMessage.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserName(user.getId(), name);
        if (!successful) return ResponseMessage.FAILED("更新用户名字失败");

        user.setName(name);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserSex(user.getId(), sex);
        if (!successful) return ResponseMessage.FAILED("更新用户性别失败");

        user.setSex(sex);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserBirthday(user.getId(), birthday);
        if (!successful) return ResponseMessage.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam int city) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserCityId(user.getId(), city);
        if (!successful) return ResponseMessage.FAILED("更新用户城市失败");

        user.setCity(city);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userServiceFacade.updateUserAddress(user.getId(), address);
        if (!successful) return ResponseMessage.FAILED("更新用户地址失败");

        user.setAddress(address);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addChild(@RequestBody Participant[] children) {
        long userId = 0;
        Set<Long> childIds = new HashSet<Long>();
        for(Participant child : children) {
            if (child.isInvalid()) continue;

            userId = child.getUserId();

            long childId = userServiceFacade.addChild(child);
            if (childId > 0) childIds.add(childId);
        }

        User user = userServiceFacade.getUser(userId);
        if (!user.exists()) return ResponseMessage.FAILED("用户不存在");

        user.getChildren().addAll(childIds);
        if (!userServiceFacade.updateUserChildren(userId, user.getChildren())) return ResponseMessage.FAILED("添加孩子信息失败");

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        user.getChildren().remove(childId);
        if (!userServiceFacade.updateUserChildren(user.getId(), user.getChildren())) return ResponseMessage.FAILED("删除孩子信息失败");

        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.GET)
    public ResponseMessage getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();
        if (!children.contains(childId)) return ResponseMessage.FAILED("孩子信息不存在");

        Participant child = userServiceFacade.getChild(childId);
        if (!child.exists()) return ResponseMessage.FAILED("孩子不存在");

        return new ResponseMessage(child);
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public ResponseMessage getChildren(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> childIds = user.getChildren();
        return new ResponseMessage(userServiceFacade.getChildren(childIds));
    }
}
