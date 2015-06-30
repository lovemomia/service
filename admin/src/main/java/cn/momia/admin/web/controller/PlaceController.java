package cn.momia.admin.web.controller;

import cn.momia.admin.web.common.FileUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.entity.ReturnResult;
import cn.momia.admin.web.service.AdminUserService;
import cn.momia.admin.web.service.ImagesService;
import cn.momia.admin.web.service.PlaceImgService;
import cn.momia.admin.web.service.PlaceService;
import cn.momia.admin.web.service.QueryPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Controller
@RequestMapping("/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private PlaceImgService placeImgService;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private QueryPageService queryPageService;

    @RequestMapping("/info")
    public ModelAndView info(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(3, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PLACE,context);
    }

    @RequestMapping("/oper")
    public ModelAndView operation(@RequestParam("uid") int uid,@RequestParam("id") int id, @RequestParam("mark") int mark,@RequestParam("pageNo") int pageNo,HttpServletRequest req){
        String reStr = FileUtil.PLACE_EDIT;
        Map<String, Object> context = new HashMap<String, Object>();
        if (mark == 0){
            reStr = FileUtil.PLACE_ADD;
        }else if (mark == 1){
            reStr = FileUtil.PLACE_IMG;
            context.put(FinalUtil.ENTITY,placeService.get(id));
            context.put(FinalUtil.ENTITYS,placeImgService.getEntitysByKey(id));
        }
        else{
            context.put(FinalUtil.ENTITY,placeService.get(id));
        }
        context.put("pageNo",pageNo);
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/add")
    public ModelAndView addEntity(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = placeService.insert(placeService.formEntity(req,FinalUtil.ADD_INFO));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加地址信息数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加地址信息数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(3, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PLACE,context);
    }

    @RequestMapping("/edit")
    public ModelAndView editEntity(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo,@RequestParam("id") int id, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = placeService.update(placeService.formEntity(req, id));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"修改地址信息数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"修改地址信息数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(3, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PLACE,context);
    }

    @RequestMapping("/del")
    public ModelAndView delEntity(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = placeService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除地址信息数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除地址信息数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(3, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PLACE,context);
    }

    @RequestMapping("/addimg")
    public ModelAndView addimg(@RequestParam("uid") int uid,@RequestParam("pid") int pid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();

        Images image = imagesService.uploadImgs(req);
        int reDate = placeImgService.insert(placeImgService.formEntity(image,pid));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加地址图片数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加地址图片数据失败!");
        }
        context.put("pageNo",pageNo);
        context.put(FinalUtil.ENTITYS, placeImgService.getEntitysByKey(pid));
        context.put(FinalUtil.ENTITY,placeService.get(pid));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PLACE_IMG,context);
    }

    @RequestMapping("/delimg")
    public ModelAndView delimg(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pid") int pid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = placeImgService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除地址图片数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除地址图片数据失败!");
        }
        context.put("pageNo",pageNo);
        context.put(FinalUtil.ENTITYS, placeImgService.getEntitysByKey(pid));
        context.put(FinalUtil.ENTITY,placeService.get(pid));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PLACE_IMG,context);
    }
}
