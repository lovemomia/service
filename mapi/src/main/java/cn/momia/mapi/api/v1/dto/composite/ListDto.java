package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;

import java.util.ArrayList;

public class ListDto extends ArrayList<Object> implements Dto {
    public static final ListDto EMPTY = new ListDto();
}
