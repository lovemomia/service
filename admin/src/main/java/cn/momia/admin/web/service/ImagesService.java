package cn.momia.admin.web.service;

import cn.momia.admin.web.entity.Images;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by hoze on 15/6/18.
 */
public interface ImagesService {
    public Images uploadImgs(HttpServletRequest req) ;
}
