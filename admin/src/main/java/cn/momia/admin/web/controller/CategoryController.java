package cn.momia.admin.web.controller;

import cn.momia.admin.web.common.FileUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Category;
import cn.momia.admin.web.entity.ReturnResult;
import cn.momia.admin.web.service.AdminUserService;
import cn.momia.admin.web.service.CategoryService;
import cn.momia.admin.web.service.QueryPageService;
import org.junit.runners.Parameterized;
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
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    public CategoryService categoryService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private QueryPageService queryPageService;

    @RequestMapping("/info")
    public ModelAndView info(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(2, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.CATEGORY,context);
    }

    @RequestMapping("/oper")
    public ModelAndView operation(@RequestParam("uid") int uid,@RequestParam("id") int id, @RequestParam("pageNo") int pageNo,HttpServletRequest req){

        String reStr = FileUtil.CATEGORY_EDIT;
        Map<String, Object> context = new HashMap<String, Object>();
        if (id == 0){
            reStr = FileUtil.CATEGORY_ADD;
        }else{
            context.put(FinalUtil.ENTITY,categoryService.get(id));
        }
        List<Category> ls = new ArrayList<Category>();
        Category category = new Category();
        category.setId(0);
        category.setName("请选择分类");
        ls.add(category);
        ls.addAll(categoryService.getEntitys());
        context.put("pageNo",pageNo);
        context.put(FinalUtil.ENTITYS,ls);
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/add")
     public ModelAndView addEntity(@RequestParam("uid") int uid, HttpServletRequest req, @RequestParam("pageNo") int pageNo){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = categoryService.insert(categoryService.formEntity(req,FinalUtil.ADD_INFO));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加分类数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加分类数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(2, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.CATEGORY,context);
    }

    @RequestMapping("/edit")
    public ModelAndView editEntity(@RequestParam("uid") int uid,@RequestParam("id") int id, @RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = categoryService.update(categoryService.formEntity(req, id));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"修改分类数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"修改分类数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(2, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.CATEGORY,context);
    }

    @RequestMapping("/del")
    public ModelAndView delEntity(@RequestParam("uid") int uid,@RequestParam("id") int id, @RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = categoryService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除分类数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除分类数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(2, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.CATEGORY,context);
    }
}
