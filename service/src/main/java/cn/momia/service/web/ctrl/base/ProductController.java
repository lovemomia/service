package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.comment.Comment;
import cn.momia.service.base.comment.CommentService;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.impl.ProductServiceImpl;
import cn.momia.service.base.product.sku.Sku;
import cn.momia.service.base.user.User;
import cn.momia.service.deal.order.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        Product product = productService.get(id);
        if(!product.exists())
            return new ResponseMessage(ErrorCode.NOT_FOUND, "product not exists");
        return new ResponseMessage(product);

    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
       List<Sku> skus = productService.getSkus(id);
        if(skus.isEmpty())
            return new ResponseMessage(ErrorCode.NOT_FOUND, "skus not exists");
        return new ResponseMessage(skus);
    }

    @RequestMapping(value = "/{id}/place", method = RequestMethod.GET)
    public ResponseMessage getProductPlace(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "{id}/comment/add", method = RequestMethod.POST)
    public ResponseMessage addComment(@PathVariable long id, @RequestParam long skuId, @RequestParam String commentJson){
        JSONObject jsonObject = JSON.parseObject(commentJson);
        Comment comment = new Comment(jsonObject);
       long commentId = commentService.add(comment,id,skuId);
        if(commentId != 0)
            return new ResponseMessage(comment);
        else
            return new ResponseMessage(ErrorCode.FAILED, "add comment failed");

    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public ResponseMessage getProductComments(@PathVariable long id, @RequestParam int star, @RequestParam("start") int start, @RequestParam int count) {
        List<Comment> comments = commentService.queryByProduct(id, star, start, count);
        if(comments.size() == 0)
            return new ResponseMessage(ErrorCode.NOT_FOUND, "Comments with star "+ star+" start from "+start+ " not exists");
        return new ResponseMessage(comments);
    }

    @RequestMapping(value = "/{id}/server", method = RequestMethod.GET)
    public ResponseMessage getProductServerInfo(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id, @RequestParam int start, @RequestParam int count) {
        List<User> users = orderService.queryUserByProduct(id, start, count);
        if(users==null)
            return new ResponseMessage(ErrorCode.NOT_FOUND, "user in product not exists");
        return new ResponseMessage(users);
    }
}
