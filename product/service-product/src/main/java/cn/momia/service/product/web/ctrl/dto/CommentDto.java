package cn.momia.service.product.web.ctrl.dto;

import cn.momia.common.webapp.ctrl.dto.Dto;
import cn.momia.service.comment.Comment;
import cn.momia.service.comment.CommentImage;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentDto implements Dto {
    private Comment comment;

    public long getId() {
        return comment.getId();
    }

    public long getProductId() {
        return comment.getProductId();
    }

    public long getSkuId() {
        return comment.getSkuId();
    }

    public long getUserId() {
        return comment.getUserId();
    }

    public int getStar() {
        return comment.getStar();
    }

    public String getContent() {
        return comment.getContent();
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return comment.getAddTime();
    }

    public List<String> getImgs() {
        List<String> imgs = new ArrayList<String>();
        for (CommentImage commentImage : comment.getImgs()) {
            imgs.add(commentImage.getUrl());
        }

        return imgs;
    }

    public CommentDto(Comment comment) {
        this.comment = comment;
    }
}
