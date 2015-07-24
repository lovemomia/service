package cn.momia.service.web.ctrl.dto;

import java.util.ArrayList;

public class ListDto extends ArrayList<Object> implements Dto {
    public static final ListDto EMPTY = new ListDto();
}
