package cn.momia.api.product.comment;

import java.util.Date;
import java.util.List;

public class Comment {
    private long id;
    private long productId;
    private long skuId;
    private long userId;
    private int star;
    private String content;
    private Date addTime;
    private List<String> imgs;
}
