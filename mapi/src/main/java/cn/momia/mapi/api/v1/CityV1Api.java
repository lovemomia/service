package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/city")
public class CityV1Api extends AbstractV1Api {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllCities() {
        return executeRequest(MomiaHttpRequest.GET(url("city")));
    }
}
