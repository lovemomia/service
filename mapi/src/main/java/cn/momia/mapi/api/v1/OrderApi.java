package cn.momia.mapi.api.v1;

import cn.momia.common.web.http.impl.MomiaHttpPostRequest;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/order")
public class OrderApi extends AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderApi.class);

    @RequestMapping(method = RequestMethod.POST)
    public ResponseMessage placeOrder(HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(conf.getString("Service.Deal")).append("/order");
        Map<String, String> params = new HashMap<String, String>();
        extractBasicParams(request, params);
        extractOrderParams(request, params);
        addSign(request, params);
        try {
            JSONObject jsonResponse = httpClient.execute(new MomiaHttpPostRequest("order", true, urlBuilder.toString(), params));
            return ResponseMessage.formJson(jsonResponse);
        } catch (Exception e) {
            LOGGER.error("fail to place order: {}", params.get("order"), e);
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to place order");
        }
    }

    private void extractOrderParams(HttpServletRequest request, Map<String, String> params) {
        params.put("order", request.getParameter("order"));
    }
}
