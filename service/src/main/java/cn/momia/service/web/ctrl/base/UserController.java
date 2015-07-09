package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.city.CityService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.Participant;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.web.ctrl.AbstractController;
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
public class UserController extends AbstractController {
    @Autowired private CityService cityService;
    @Autowired private UserService userService;
    @Autowired private ParticipantService participantService;

    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);

        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return new ResponseMessage(buildUserResponse(user));
    }

    private JSONObject buildUserResponse(User user) {
        JSONObject userPackJson = new JSONObject();
        userPackJson.put("user", user);
        userPackJson.put("children", participantService.get(user.getChildren()).values());

        return userPackJson;
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken,
                                           @RequestParam int status,
                                           @RequestParam(defaultValue = "eq") String type,
                                           @RequestParam int start,
                                           @RequestParam int count) {
        if (StringUtils.isBlank(utoken) || isInvalidLimit(start, count)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        long totalCount = orderService.queryCountByUser(user.getId(), status, type);
        List<Order> orders = orderService.queryByUser(user.getId(), status, type, start, count);
        List<Long> productIds = new ArrayList<Long>();
        for (Order order : orders) {
            productIds.add(order.getProductId());
        }

        List<Product> products = productService.get(productIds);

        return new ResponseMessage(buildUserOrders(totalCount, orders, products));
    }

    private JSONObject buildUserOrders(long totalCount, List<Order> orders, List<Product> products) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        Map<Long, Sku> skuMap = new HashMap<Long, Sku>();
        for (Product product : products) {
            if (product.exists()) {
                productMap.put(product.getId(), product);
                for (Sku sku : product.getSkus()) {
                    if (sku.exists()) skuMap.put(sku.getId(), sku);
                }
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
            if (sku != null) orderJson.put("scheduler", sku.scheduler());

            ordersJson.add(orderJson);
        }
        ordersPackJson.put("orders", ordersJson);

        return ordersPackJson;
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public ResponseMessage updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(nickName)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if(userService.getByNickName(nickName).exists()) return new ResponseMessage(ErrorCode.EXIST_NICKNAME, "用户昵称已存在");

        boolean successful = userService.updateNickName(user.getId(), nickName);
        if (!successful) return ResponseMessage.FAILED("更新用户昵称失败");

        user.setNickName(nickName);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(avatar)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userService.updateAvatar(user.getId(), avatar);

        if (!successful) return ResponseMessage.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(name)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userService.updateName(user.getId(), name);

        if (!successful) return ResponseMessage.FAILED("更新用户名字失败");

        user.setName(name);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(sex)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userService.updateSex(user.getId(), sex);

        if (!successful) return ResponseMessage.FAILED("更新用户性别失败");

        user.setSex(sex);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        if(StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userService.updateBirthday(user.getId(), birthday);

        if (!successful) return ResponseMessage.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam int city) {
        if(StringUtils.isBlank(utoken) || city < 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userService.updateCityId(user.getId(), city);

        if (!successful) return ResponseMessage.FAILED("更新用户城市失败");

        user.setCity(cityService.get(city).getName());
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        if(StringUtils.isBlank(utoken) || StringUtils.isBlank(address)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        boolean successful = userService.updateAddress(user.getId(), address);

        if (!successful) return ResponseMessage.FAILED("更新用户地址失败");

        user.setAddress(address);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/child", method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addChild(@RequestBody Participant[] children) {
        long userId = 0;
        Set<Long> childrenIds = new HashSet<Long>();
        for(Participant child : children) {
            if (child.isInvalid()) return ResponseMessage.BAD_REQUEST;
            long childId = participantService.add(child);
            if (childId <= 0) return ResponseMessage.FAILED("添加孩子信息失败");

            userId = child.getUserId();
            childrenIds.add(childId);
        }

        childrenIds.addAll(userService.get(userId).getChildren());

        if (userId > 0 && !userService.updateChildren(userId, childrenIds)) return ResponseMessage.FAILED("添加孩子信息失败");

        return new ResponseMessage(buildUserResponse(userService.get(userId)));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.DELETE)
    public ResponseMessage deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        if (StringUtils.isBlank(utoken) || childId <= 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();
        children.remove(childId);
        if (!userService.updateChildren(user.getId(), children)) return ResponseMessage.FAILED("删除孩子信息失败");

        user.setChildren(children);
        return new ResponseMessage(buildUserResponse(user));
    }

    @RequestMapping(value = "/child/{cid}", method = RequestMethod.GET)
    public ResponseMessage getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        if (StringUtils.isBlank(utoken) || childId < 0) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();
        if (!children.contains(childId)) return ResponseMessage.BAD_REQUEST;

        return new ResponseMessage(participantService.get(childId));
    }

    @RequestMapping(value = "/child", method = RequestMethod.GET)
    public ResponseMessage getChildren(@RequestParam String utoken) {
        if (StringUtils.isBlank(utoken)) return ResponseMessage.BAD_REQUEST;

        User user = userService.getByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Set<Long> children = user.getChildren();

      return new ResponseMessage(participantService.get(children).values());
    }
}
