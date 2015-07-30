package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/region")
public class RegionV1Api extends AbstractV1Api {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getAllRegions() {
        return executeRequest(MomiaHttpRequest.GET(url("region")));
    }

    @RequestMapping(value = "/district/tree", method = RequestMethod.GET)
    public ResponseMessage getDistrictTree() {
        return executeRequest(MomiaHttpRequest.GET(url("region/district/tree")));
    }
}
