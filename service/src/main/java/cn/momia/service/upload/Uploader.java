package cn.momia.service.upload;

import java.io.IOException;

/**
 * Created by Administrator on 15-6-2.
 */
public interface Uploader {
    public Result upload(Image image) throws IOException;
}
