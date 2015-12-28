package cn.momia.api.event;

import cn.momia.api.event.dto.Banner;
import cn.momia.api.event.dto.Event;
import cn.momia.api.event.dto.Icon;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class EventServiceApi extends ServiceApi {
    public List<Banner> listBanners(int cityId) {
        return list("/event/banner", cityId, Banner.class);
    }

    private  <T> List<T> list(String uri, int cityId, Class<T> clazz) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("city", cityId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url(uri), builder.build());

        return executeReturnList(request, clazz);
    }

    public List<Icon> listIcons(int cityId) {
        return list("/event/icon", cityId, Icon.class);
    }

    public List<Event> listEvents(int cityId) {
        return list("/event/event", cityId, Event.class);
    }
}
