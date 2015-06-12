package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.base.product.Product;
import cn.momia.service.base.product.ProductService;
import cn.momia.service.base.product.impl.ProductServiceImpl;
import cn.momia.service.base.product.sku.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
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
        for(Sku sku :skus)
                skus.add(sku);
        return new ResponseMessage(skus);
    }

    @RequestMapping(value = "/{id}/place", method = RequestMethod.GET)
    public ResponseMessage getProductPlace(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public ResponseMessage getProductComments(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/server", method = RequestMethod.GET)
    public ResponseMessage getProductServerInfo(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id) {
        // TODO
        return new ResponseMessage("TODO");
    }
}
