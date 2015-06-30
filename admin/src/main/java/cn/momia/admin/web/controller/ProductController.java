package cn.momia.admin.web.controller;

import cn.momia.admin.web.common.FileUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.entity.Sku;
import cn.momia.admin.web.service.AdminUserService;
import cn.momia.admin.web.service.CategoryService;
import cn.momia.admin.web.service.CityService;
import cn.momia.admin.web.service.ImagesService;
import cn.momia.admin.web.service.PlaceService;
import cn.momia.admin.web.service.ProductImgService;
import cn.momia.admin.web.service.ProductService;
import cn.momia.admin.web.service.QueryPageService;
import cn.momia.admin.web.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private ProductImgService productImgService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CityService cityService;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private QueryPageService queryPageService;

    @RequestMapping("/info")
    public ModelAndView getEntitys(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(4, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT,context);
    }

    @RequestMapping("/oper")
    public ModelAndView operation(@RequestParam("uid") int uid,@RequestParam("id") int id, @RequestParam("mark") int mark,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        String reStr = FileUtil.PRODUCT_EDIT;
        if (mark == 0){
            reStr = FileUtil.PRODUCT_ADD;
        }else if (mark == 1){
            reStr = FileUtil.PRODUCT_IMG;
            context.put(FinalUtil.ENTITY,productService.get(id));
            context.put(FinalUtil.ENTITYS, productImgService.getEntitysByKey(id));
        }else if (mark == 2){
            reStr = "product_other_add";
            context.put(FinalUtil.ENTITY,productService.get(id));
        }else if (mark == 3){
            reStr = "product_other_edit";
            String content = productService.get(id).getContent();
            if (!content.equals("")) {
                context.put("contents", productService.getContentJsontoMap(content));
            }
            context.put(FinalUtil.ENTITY,productService.get(id));
        }else if (mark == 4){
            reStr = "product_preview";
            context.put("jsonStr",productService.getPreviewInfo(id));
            context.put(FinalUtil.ENTITY,productService.get(id));
        }else{
            context.put(FinalUtil.ENTITY,productService.get(id));
        }
        context.put("pageNo",pageNo);
        context.put("categorys",categoryService.getEntitys());
        context.put("places",placeService.getEntitys());
        context.put("citys",cityService.getEntitys());
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/add")
    public ModelAndView addEntity(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = productService.insert(productService.formEntity(req,FinalUtil.ADD_INFO));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加商品数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加商品数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(4, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT,context);
    }

    @RequestMapping("/edit")
    public ModelAndView editEntity(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = productService.update(productService.formEntity(req, id));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"修改商品数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"修改商品数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(4, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT,context);
    }

    @RequestMapping("/del")
    public ModelAndView delEntity(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = productService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除商品数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除商品数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(4, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT,context);
    }

    @RequestMapping("/addimg")
    public ModelAndView addimg(@RequestParam("uid") int uid,@RequestParam("pid") int pid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();

        Images image = imagesService.uploadImgs(req);
        int reDate = productImgService.insert(productImgService.formEntity(image,pid));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加地址图片数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加地址图片数据失败!");
        }
        context.put("pageNo",pageNo);
        context.put(FinalUtil.ENTITYS, productImgService.getEntitysByKey(pid));
        context.put(FinalUtil.ENTITY,productService.get(pid));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT_IMG,context);
    }

    @RequestMapping("/delimg")
    public ModelAndView delimg(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pid") int pid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = productImgService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除图片数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除图片数据失败!");
        }
        context.put("pageNo",pageNo);
        context.put(FinalUtil.ENTITYS, productImgService.getEntitysByKey(pid));
        context.put(FinalUtil.ENTITY, productService.get(pid));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT_IMG,context);
    }

    @RequestMapping("/addcontent")
    public ModelAndView addcontent(@RequestParam("uid") int uid,@RequestParam("pid") int pid,@RequestParam("pageNo") int pageNo, HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        String contentJson = productService.getContentJsonStr(req);
        //System.out.print(contentJson);
        int reDate = productService.update_content(pid, contentJson);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"添加数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(4, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.PRODUCT,context);
    }

}
