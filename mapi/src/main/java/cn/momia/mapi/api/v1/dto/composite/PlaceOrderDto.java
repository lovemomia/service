package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.ContactsDto;
import cn.momia.mapi.api.v1.dto.base.Dto;

public class PlaceOrderDto implements Dto {
    private ContactsDto contacts;
    private ListDto skus;

    public ContactsDto getContacts() {
        return contacts;
    }

    public void setContacts(ContactsDto contacts) {
        this.contacts = contacts;
    }

    public ListDto getSkus() {
        return skus;
    }

    public void setSkus(ListDto skus) {
        this.skus = skus;
    }
}
