package cn.momia.mapi.api.v1.dto;

import java.util.ArrayList;

public class FavoriteDto extends ArrayList<FavoriteDto.Product> implements Dto {
    public static class Product {
        private long id;
        private String cover;
        private String title;
        private String scheduler;
        private int joined;
        private float price;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getScheduler() {
            return scheduler;
        }

        public void setScheduler(String scheduler) {
            this.scheduler = scheduler;
        }

        public int getJoined() {
            return joined;
        }

        public void setJoined(int joined) {
            this.joined = joined;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }
    }
}
