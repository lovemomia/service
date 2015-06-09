package cn.momia.mapi.api.v1;

import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/product")
public class ProductApi extends AbstractApi {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseMessage getProduct(HttpServletRequest request) {
        return new ResponseMessage("TODO");
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public ResponseMessage getProductSkus(HttpServletRequest request) {
        return new ResponseMessage("TODO");
    }
}
