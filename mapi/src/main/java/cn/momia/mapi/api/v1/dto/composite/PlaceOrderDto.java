package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.ContactsDto;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.SkuDto;

import java.util.List;

public class PlaceOrderDto implements Dto {
    private ContactsDto contacts;
    private List<SkuDto> skus;

    public ContactsDto getContacts() {
        return contacts;
    }

    public void setContacts(ContactsDto contacts) {
        this.contacts = contacts;
    }

    public List<SkuDto> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuDto> skus) {
        this.skus = skus;
    }
}
