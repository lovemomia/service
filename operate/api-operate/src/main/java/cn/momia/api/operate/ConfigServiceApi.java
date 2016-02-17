package cn.momia.api.operate;

import cn.momia.api.operate.dto.Config;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

import java.util.List;

public class ConfigServiceApi extends HttpServiceApi {
    public List<Config.Banner> listBanners(int cityId) {
        return list("/config/banner", cityId, Config.Banner.class);
    }

    private  <T> List<T> list(String uri, int cityId, Class<T> clazz) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("city", cityId);
        return executeReturnList(MomiaHttpRequestBuilder.GET(url(uri), builder.build()), clazz);
    }

    public List<Config.Icon> listIcons(int cityId) {
        return list("/config/icon", cityId, Config.Icon.class);
    }

    public List<Config.Event> listEvents(int cityId) {
        return list("/config/event", cityId, Config.Event.class);
    }
}
