package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.AbstractApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/region")
public class RegionApi extends AbstractApi {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllRegions() {
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("region"));

        return executeRequest(request);
    }
}
