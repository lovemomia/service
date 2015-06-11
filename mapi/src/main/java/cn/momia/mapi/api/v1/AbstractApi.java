package cn.momia.mapi.api.v1;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.http.MomiaHttpClient;
import cn.momia.common.web.http.MomiaHttpRequestExecutor;
import cn.momia.common.web.http.MomiaHttpRequestSigner;
import cn.momia.common.web.secret.SecretKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class AbstractApi {
    @Autowired
    protected Configuration conf;

    @Autowired
    protected MomiaHttpClient httpClient;

    @Autowired
    protected MomiaHttpRequestExecutor requestExecutor;

    protected void extractBasicParams(HttpServletRequest request, Map<String, String> params) {
        String expired = request.getParameter("expired");
        if (!StringUtils.isBlank(expired)) params.put("expired", expired);
    }

    protected void addSign(HttpServletRequest request, Map<String, String> params) {
        // TODO key 根据调用的服务不同，取不同的值
        String key = SecretKey.get("service");
        params.put("sign", MomiaHttpRequestSigner.sign(params, key));
    }
}
