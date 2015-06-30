package cn.momia.admin.web.controller;

import cn.momia.admin.web.common.FileUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.service.AdminUserService;
import cn.momia.admin.web.service.CategoryService;
import cn.momia.admin.web.service.ProductService;
import cn.momia.admin.web.service.SkuPropertyService;
import cn.momia.admin.web.service.SkuPropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoze on 15/6/19.
 */
@Controller
@RequestMapping("/property")
public class PropertyController {

    @Autowired
    private SkuPropertyService skuPropertyService;

    @Autowired
    private SkuPropertyValueService skuPropertyValueService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping("/info")
    public ModelAndView info(@RequestParam("uid") int uid, HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.ENTITYS, skuPropertyService.getEntitys());
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PROPERTY,context);
    }

    @RequestMapping("/oper")
    public ModelAndView operation(@RequestParam("uid") int uid,@RequestParam("id") int id, @RequestParam("mark") int mark, HttpServletRequest req){

        String reStr = FileUtil.PROPERTY_EDIT;
        Map<String, Object> context = new HashMap<String, Object>();
        if (mark == 0){
            reStr = FileUtil.PROPERTY_ADD;
            context.put(FinalUtil.ENTITYS,categoryService.getEntitys());
        }else if (mark == 1){
            reStr = FileUtil.PROPERTY_VALUE;
            context.put(FinalUtil.ENTITY,skuPropertyService.get(id));
            context.put(FinalUtil.ENTITYS,skuPropertyValueService.getEntitysByKey(id));
        }else{
            context.put(FinalUtil.ENTITY,skuPropertyService.get(id));
            context.put(FinalUtil.ENTITYS,categoryService.getEntitys());
        }
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/add")
    public ModelAndView addEntity(@RequestParam("uid") int uid, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = skuPropertyService.insert(skuPropertyService.formEntity(req,FinalUtil.ADD_INFO));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加属性数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加属性数据失败!");
        }
        context.put(FinalUtil.ENTITYS,skuPropertyService.getEntitys());
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PROPERTY,context);
    }

    @RequestMapping("/edit")
    public ModelAndView editEntity(@RequestParam("uid") int uid,@RequestParam("id") int id, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = skuPropertyService.update(skuPropertyService.formEntity(req, id));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"修改属性数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"修改属性数据失败!");
        }
        context.put(FinalUtil.ENTITYS,skuPropertyService.getEntitys());
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PROPERTY,context);
    }

    @RequestMapping("/del")
    public ModelAndView delEntity(@RequestParam("uid") int uid,@RequestParam("id") int id, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = skuPropertyService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除属性数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除属性数据失败!");
        }
        context.put(FinalUtil.ENTITYS,skuPropertyService.getEntitys());
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PROPERTY,context);
    }

    @RequestMapping("/addvalue")
    public ModelAndView addvalue(@RequestParam("uid") int uid,@RequestParam("pid") int pid, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = skuPropertyValueService.insert(skuPropertyValueService.formEntity(req,pid));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加属性值数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加属性值数据失败!");
        }
        context.put(FinalUtil.ENTITYS, skuPropertyValueService.getEntitysByKey(pid));
        context.put(FinalUtil.ENTITY,skuPropertyService.get(pid));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PROPERTY_VALUE,context);
    }

    @RequestMapping("/delvalue")
    public ModelAndView delvalue(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pid") int pid, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = skuPropertyValueService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除属性值数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除属性值数据失败!");
        }
        context.put(FinalUtil.ENTITYS, skuPropertyValueService.getEntitysByKey(pid));
        context.put(FinalUtil.ENTITY,skuPropertyService.get(pid));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PROPERTY_VALUE,context);
    }

}
