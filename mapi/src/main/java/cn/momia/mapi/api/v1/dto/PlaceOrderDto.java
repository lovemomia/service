package cn.momia.mapi.api.v1.dto;

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
    private SkuDto.Skus skus;

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public SkuDto.Skus getSkus() {
        return skus;
    }

    public void setSkus(SkuDto.Skus skus) {
        this.skus = skus;
    }
}
