package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.SkuDto;

import java.util.List;

public class PlaceOrderDto implements Dto {
    public static class Contacts implements Dto {
        private String name;
        private String mobile;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    private Contacts contacts;
    private List<SkuDto> skus;

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public List<SkuDto> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuDto> skus) {
        this.skus = skus;
    }
}
