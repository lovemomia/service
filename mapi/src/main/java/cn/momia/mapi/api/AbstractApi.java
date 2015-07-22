package cn.momia.mapi.api;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.controller.BaseController;
import cn.momia.common.web.http.MomiaHttpRequest;
import cn.momia.common.web.http.MomiaHttpRequestExecutor;
import cn.momia.common.web.http.MomiaHttpResponseCollector;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.mapi.api.v1.dto.base.Dto;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractApi extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApi.class);

    @Autowired protected Configuration conf;
    @Autowired protected MomiaHttpRequestExecutor requestExecutor;

    protected String url(Object... paths) {
        // TODO 根据paths判断使用哪个service
        return serviceUrl(conf.getString("Service.Base"), paths);
    }

    private String serviceUrl(String service, Object... paths) {
        StringBuilder urlBuilder = new StringBuilder().append(service);
        for (Object path : paths) urlBuilder.append("/").append(path);

        return urlBuilder.toString();
    }

    protected ResponseMessage executeRequest(MomiaHttpRequest request) {
        return executeRequest(request, null);
    }

    protected ResponseMessage executeRequest(MomiaHttpRequest request, Function<Object, Dto> buildResponseData) {
        ResponseMessage responseMessage = requestExecutor.execute(request);

        if (buildResponseData == null || !responseMessage.successful()) return responseMessage;
        return new ResponseMessage(buildResponseData.apply(responseMessage.getData()));
    }

    protected ResponseMessage executeRequests(List<MomiaHttpRequest> requests, Function<MomiaHttpResponseCollector, Dto> buildResponseData) {
        MomiaHttpResponseCollector collector = requestExecutor.execute(requests);

        if (collector.notLogin()) return ResponseMessage.TOKEN_EXPIRED;
        if (!collector.isSuccessful()) {
            LOGGER.error("fail to execute requests: {}", collector.getExceptions());
            return ResponseMessage.FAILED;
        }

        return new ResponseMessage(buildResponseData.apply(collector));
    }
}
