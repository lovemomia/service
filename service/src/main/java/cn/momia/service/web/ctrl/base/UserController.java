package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getUser(@PathVariable long id) {
        User user = userService.get(id);

        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");
        return new ResponseMessage(user);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getUser(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);

        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");
        return new ResponseMessage(user);
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public ResponseMessage getOrdersOfUser(@RequestParam String utoken, @RequestParam int status, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return new ResponseMessage(ErrorCode.FAILED, "invalid limit params");

        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        List<Order> orders = orderService.queryByUser(user.getId(), status, start, count);
        List<Long> productIds = new ArrayList<Long>();
        List<Long> skuIds = new ArrayList<Long>();
        for (Order order : orders) {
            productIds.add(order.getProductId());
            skuIds.add(order.getSkuId());
        }

        List<Product> products = productService.get(productIds);
        List<Sku> skus = skuService.get(skuIds);

        return new ResponseMessage(buildUserOrders(orders, products, skus));
    }

    private JSONArray buildUserOrders(List<Order> orders, List<Product> products, List<Sku> skus) {
        Map<Long, Product> productMap = new HashMap<Long, Product>();
        for (Product product : products) productMap.put(product.getId(), product);

        Map<Long, Sku> skuMap = new HashMap<Long, Sku>();
        for (Sku sku : skus) skuMap.put(sku.getId(), sku);

        JSONArray jsonArray = new JSONArray();
        for (Order order : orders) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("order", order);
            jsonObject.put("product", productMap.get(order.getProductId()));
            jsonObject.put("sku", skuMap.get(order.getSkuId()));

            jsonArray.add(jsonObject);
        }

        return jsonArray;
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public ResponseMessage updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateNickName(user.getId(), nickName);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user nick name");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public ResponseMessage updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateAvatar(user.getId(), avatar);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user avatar");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public ResponseMessage updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateName(user.getId(), name);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user name");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public ResponseMessage updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateSex(user.getId(), sex);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user sex");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateBirthday(user.getId(), birthday);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user birthday");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public ResponseMessage updateDesc(@RequestParam String utoken, @RequestParam int city) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateCityId(user.getId(), city);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user city");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseMessage updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FAILED, "user not exists");

        boolean successful = userService.updateAddress(user.getId(), address);

        if (!successful) return new ResponseMessage(ErrorCode.FAILED, "fail to update user address");
        return ResponseMessage.SUCCESS;
    }
}
