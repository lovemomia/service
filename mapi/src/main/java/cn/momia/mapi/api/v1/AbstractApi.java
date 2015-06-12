package cn.momia.mapi.api.v1;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.http.MomiaHttpClient;
import cn.momia.common.web.http.MomiaHttpRequestExecutor;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractApi {
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
}
