package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/place", method = RequestMethod.GET)
    public ResponseMessage getProductPlace(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/comment", method = RequestMethod.GET)
    public ResponseMessage getProductComments(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/server", method = RequestMethod.GET)
    public ResponseMessage getProductServerInfo(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/customer", method = RequestMethod.GET)
    public ResponseMessage getProductCustomersInfo(@PathVariable long id) {
        return new ResponseMessage("TODO");
    }
}
