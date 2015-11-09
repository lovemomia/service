package cn.momia.api.feed.dto;

import java.util.List;

public class FeedTagsDto {
    private List<FeedTagDto> recommendedTags;
    private List<FeedTagDto> hotTags;

    public List<FeedTagDto> getRecommendedTags() {
        return recommendedTags;
    }

    public void setRecommendedTags(List<FeedTagDto> recommendedTags) {
        this.recommendedTags = recommendedTags;
    }

    public List<FeedTagDto> getHotTags() {
        return hotTags;
    }

    public void setHotTags(List<FeedTagDto> hotTags) {
        this.hotTags = hotTags;
    }
}
