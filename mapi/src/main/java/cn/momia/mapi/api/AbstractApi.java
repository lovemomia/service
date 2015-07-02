package cn.momia.mapi.api;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.http.MomiaHttpClient;
import cn.momia.common.web.http.MomiaHttpParamBuilder;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpRequestExecutor;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.Dto;
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

    protected String baseServiceUrl(Object... paths) {
        return serviceUrl(conf.getString("Service.Base"), paths);
    }

    private String serviceUrl(String service, Object... paths) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(service);
        for (Object path : paths) {
            urlBuilder.append("/").append(path);
        }

        return urlBuilder.toString();
    }

    protected String dealServiceUrl(Object... paths) {
        return serviceUrl(conf.getString("Service.Deal"), paths);
    }

    protected ResponseMessage executeRequest(MomiaHttpRequest request) {
        return executeRequest(request, null);
    }

    protected ResponseMessage executeRequest(MomiaHttpRequest request, Function<Object, Dto> buildResponseData) {
        try {
            JSONObject responseJson = httpClient.execute(request);
            if (buildResponseData == null || responseJson.getInteger("errno") != ErrorCode.SUCCESS) return ResponseMessage.formJson(responseJson);
            return new ResponseMessage(buildResponseData.apply(responseJson.get("data")));
        } catch (Exception e) {
            LOGGER.error("fail to execute request: {}", request, e);
            return new ResponseMessage(ErrorCode.FAILED, "fail to execute request");
        }
    }

    protected ResponseMessage executeRequests(List<MomiaHttpRequest> requests, Function<MomiaHttpResponseCollector, Dto> buildResponseData) {
        MomiaHttpResponseCollector collector = requestExecutor.execute(requests);
        if (collector.getErrnos().contains(ErrorCode.TOKEN_EXPIRED)) return ResponseMessage.TOKEN_EXPIRED;

        if (!collector.isSuccessful()) {
            LOGGER.error("fail to execute requests: {}, exceptions: {}", requests, collector.getExceptions());
            return new ResponseMessage(ErrorCode.FAILED, "fail to execute requests");
        }

        return new ResponseMessage(buildResponseData.apply(collector));
    }

    protected long getUserId(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        MomiaHttpRequest request = MomiaHttpRequest.GET(baseServiceUrl("user"), builder.build());

        ResponseMessage responseMessage = executeRequest(request);
        if (responseMessage.getErrno() == ErrorCode.SUCCESS) return ((JSONObject) responseMessage.getData()).getLong("id");
        if (responseMessage.getErrno() == ErrorCode.TOKEN_EXPIRED) return 0;

        throw new RuntimeException("fail to get user id");
    }
}
