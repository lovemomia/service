package cn.momia.admin.web.controller;

import cn.momia.admin.web.common.FileUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.service.ImagesService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */

@Controller
@RequestMapping("/upload")
public class ImgController {

    @Autowired
    private ImagesService imagesService;

    @RequestMapping("/img")
    public String uploadImg(HttpServletRequest req,HttpServletResponse rsp){
        rsp.setContentType("text/html; charset=UTF-8");

        Map<String, Object> context = new HashMap<String, Object>();
        //设置响应给前台内容的PrintWriter对象
        try {
            PrintWriter out = rsp.getWriter();
            Images images = imagesService.uploadImgs(req);
            context.put("msg","0");
            context.put("path",images.getUrl());

        } catch (IOException e) {
            context.put("msg","1");
            context.put("path","000");
            e.printStackTrace();
        }
        try {
            rsp.getWriter().write(JSONObject.toJSONString(context));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
