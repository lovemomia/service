package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.favorite.FavoriteService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage addFavorite(@RequestParam String utoken, @RequestParam(value = "pid") long productId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FORBIDDEN, "user not login");

        long favoriteId = favoriteService.add(user.getId(), productId);
        if (favoriteId <= 0) return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to add favorite");

        return new ResponseMessage("add favorite successfully");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseMessage deleteFavorite(@PathVariable long id, @RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FORBIDDEN, "user not login");

        if (!favoriteService.delete(user.getId(), id)) return new ResponseMessage(ErrorCode.FORBIDDEN, "fail to delete favorite");
        return new ResponseMessage("delete favorite successfully");
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getFavoritesOfUser(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return new ResponseMessage(ErrorCode.FORBIDDEN, "user not login");

        List<Long> productIds = favoriteService.queryFavoredProductsByUser(user.getId(), start, count);
        List<Product> products = productService.get(productIds);
        Map<Long, List<Sku>> skusOfProducts = skuService.queryByProducts(productIds);

        return new ResponseMessage(buildProductsResponse(products, skusOfProducts));
    }

    private JSONArray buildProductsResponse(List<Product> products, Map<Long, List<Sku>> skusOfProducts) {
        JSONArray data = new JSONArray();
        for (Product product : products) {
            JSONObject productData = new JSONObject();
            productData.put("product", product);
            productData.put("skus", skusOfProducts.get(product.getId()));

            data.add(productData);
        }

        return data;
    }
}
