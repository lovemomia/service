package cn.momia.mapi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/product")
public class ProductController extends AbstractController {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getProduct(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }

    @RequestMapping(value = "/{id}/sku", method = RequestMethod.GET)
    public String getProductSkus(HttpServletRequest request) {
        return forward(request, request.getRequestURI());
    }
}
