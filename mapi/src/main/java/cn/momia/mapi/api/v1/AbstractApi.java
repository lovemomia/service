package cn.momia.mapi.api.v1;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.http.MomiaHttpClient;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpRequestExecutor;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApi.class);

    @Autowired
    protected Configuration conf;

    @Autowired
    protected MomiaHttpClient httpClient;

    @Autowired
    protected MomiaHttpRequestExecutor requestExecutor;

    protected String baseServiceUrl(Object[] paths) {
        return serviceUrl(conf.getString("Service.Base"), paths);
    }

    private String serviceUrl(String service, Object[] paths) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(service);
        for (Object path : paths) {
            urlBuilder.append("/").append(path);
        }

        return urlBuilder.toString();
    }

    protected String DealServiceUrl(Object[] paths) {
        return serviceUrl(conf.getString("Service.Deal"), paths);
    }

    protected ResponseMessage executeRequest(MomiaHttpRequest request) {
        try {
            JSONObject responseJson = httpClient.execute(request);
            return ResponseMessage.formJson(responseJson);
        } catch (Exception e) {
            LOGGER.error("fail to execute request: {}", request, e);
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to execute request");
        }
    }

    protected ResponseMessage executeRequests(List<MomiaHttpRequest> requests, Function<MomiaHttpResponseCollector, JSONObject> buildResponseData) {
        MomiaHttpResponseCollector collector = requestExecutor.execute(requests);
        if (!collector.isSuccessful()) {
            LOGGER.error("fail to execute requests: {}, exceptions: {}", requests, collector.getExceptions());
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to execute requests");
        }

        return new ResponseMessage(buildResponseData.apply(collector));
    }
}
