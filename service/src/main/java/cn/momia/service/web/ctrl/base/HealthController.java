package cn.momia.service.web.ctrl.base;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.web.ctrl.AbstractController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthController extends AbstractController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage health() {
        return ResponseMessage.SUCCESS;
    }
}
