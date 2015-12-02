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
    public List<Banner> listBanners(int cityId, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/event/banner"), builder.build());

        return executeReturnList(request, Banner.class);
    }

    public List<Icon> listIcons(int cityId, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/event/icon"), builder.build());

        return executeReturnList(request, Icon.class);
    }

    public List<Event> listEvents(int cityId, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/event/event"), builder.build());

        return executeReturnList(request, Event.class);
    }
}
