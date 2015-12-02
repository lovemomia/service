package cn.momia.api.event;

import cn.momia.api.event.dto.BannerDto;
import cn.momia.api.event.dto.EventDto;
import cn.momia.api.event.dto.IconDto;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class EventServiceApi extends ServiceApi {
    public List<BannerDto> listBanners(int cityId, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/event/banner"), builder.build());

        return executeReturnList(request, BannerDto.class);
    }

    public List<IconDto> listIcons(int cityId, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/event/icon"), builder.build());

        return executeReturnList(request, IconDto.class);
    }

    public List<EventDto> listEvents(int cityId, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("city", cityId)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/event/event"), builder.build());

        return executeReturnList(request, EventDto.class);
    }
}
