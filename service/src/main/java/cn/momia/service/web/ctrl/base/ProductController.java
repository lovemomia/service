package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.comment.Comment;
import cn.momia.service.base.comment.CommentService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductQuery;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.product.sku.SkuService;
import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
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
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController extends AbstractController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getProducts(@RequestParam int start, @RequestParam int count, @RequestParam(required = false) String query) {
        // TODO validate limit
        List<Product> products = productService.queryProducts(start, count, new ProductQuery(query));
        List<List<Sku>> skusOfProducts = new ArrayList<List<Sku>>();
        for (Product product : products) {
            skusOfProducts.add(skuService.queryByProduct(product.getId()));
        }

        return new ResponseMessage(buildProductsResponse(products, skusOfProducts));
    }

    private JSONArray buildProductsResponse(List<Product> products, List<List<Sku>> skusOfProducts) {
        JSONArray data = new JSONArray();
        for (int i = 0; i < products.size(); i++) {
            JSONObject productData = new JSONObject();
            productData.put("product", products.get(i));
            productData.put("skus", skusOfProducts.get(i));

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

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        List<Sku> skus = skuService.queryByProduct(id);

        return new ResponseMessage(skus);
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public ResponseMessage getProductComments(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        List<Comment> comments = commentService.queryByProduct(id, start, count);

        return new ResponseMessage(comments);
    }

    @RequestMapping(value = "/{id}/server", method = RequestMethod.GET)
    public ResponseMessage getProductServerInfo(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        List<Integer> customers = orderService.queryCustomerByProduct(id, start, count);
        List<User> users = new ArrayList<User>();
        for (int customerId : customers) {
            users.add(userService.get(customerId));
        }

        return new ResponseMessage(users);
    }
}
