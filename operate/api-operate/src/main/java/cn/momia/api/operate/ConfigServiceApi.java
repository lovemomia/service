package cn.momia.api.operate;

import cn.momia.api.operate.dto.Banner;
import cn.momia.api.operate.dto.Event;
import cn.momia.api.operate.dto.Icon;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class ConfigServiceApi extends HttpServiceApi {
    public List<Banner> listBanners(int cityId) {
        return list("/config/banner", cityId, Banner.class);
    }

    private  <T> List<T> list(String uri, int cityId, Class<T> clazz) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("city", cityId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url(uri), builder.build());

        return executeReturnList(request, clazz);
    }

    public List<Icon> listIcons(int cityId) {
        return list("/config/icon", cityId, Icon.class);
    }

    public List<Event> listEvents(int cityId) {
        return list("/config/event", cityId, Event.class);
    }
}
