package cn.momia.admin.web.controller;

import cn.momia.admin.web.common.FileUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.common.QueryPage;
import cn.momia.admin.web.entity.AdminUser;
import cn.momia.admin.web.entity.ReturnResult;
import cn.momia.admin.web.service.AdminUserService;
import cn.momia.admin.web.service.QueryPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
@Controller
@RequestMapping("/user")
public class AdminUserController {

    @Autowired
    public AdminUserService adminUserService;

    @Autowired
    private QueryPageService queryPageService;

    /**
     * 跳转登录页面
     * @param req
     * @return
     */
    @RequestMapping("/login")
    public ModelAndView loginpage(HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.RETURN_MSG, FinalUtil.LOGIN_BEFORE);
        return new ModelAndView(FileUtil.USER_LOGIN,context);
    }

    @RequestMapping("/loginindex")
    public ModelAndView isVerify(@RequestParam("username") String username,@RequestParam("password") String password,HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        String reStr = FileUtil.USER_LOGIN;
        AdminUser entity = adminUserService.isVerify(username, password);
        if(!entity.exists()){
            context.put(FinalUtil.USER_ENTITY, entity);
            context.put(FinalUtil.RETURN_MSG, FinalUtil.LOGIN_SUCESS);
            reStr = FileUtil.USER_LOGIN_SUCESS;
        }else {
            context.put(FinalUtil.RETURN_MSG, FinalUtil.LOGIN_AFTER);
        }

        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/index")
    public ModelAndView index(@RequestParam("uid") int uid,HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.USER_ENTITY, adminUserService.get(uid));
        return new ModelAndView(FileUtil.USER_LOGIN_SUCESS,context);
    }

    @RequestMapping("/info")
    public ModelAndView info(@RequestParam("uid") int uid,@RequestParam("pageNo") int pageNo,HttpServletRequest req){
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(1, pageNo)));
        context.put(FinalUtil.USER_ENTITY, adminUserService.get(uid));
        return new ModelAndView(FileUtil.USER,context);
    }

    @RequestMapping("/oper")
    public ModelAndView operation(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pageNo") int pageNo, HttpServletRequest req){
        String reStr = FileUtil.USER_EDIT;
        Map<String, Object> context = new HashMap<String, Object>();
        if (id == 0){
            reStr = FileUtil.USER_ADD;
        }else{
            context.put(FinalUtil.ENTITY,adminUserService.get(id));
        }
        context.put("pageNo",pageNo);
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        context.put(FinalUtil.RETURN_MSG, "");
        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/add")
    public ModelAndView addEntity(@RequestParam("uid") int uid, HttpServletRequest req){

        String reStr = FileUtil.USER_ADD;
        Map<String, Object> context = new HashMap<String, Object>();

        int reDate = adminUserService.insert(adminUserService.formEntity(req,FinalUtil.ADD_INFO));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"添加用户数据成功!");
            reStr = FileUtil.USER;
        }else{
            context.put(FinalUtil.RETURN_MSG,"用户名已存在!");
        }
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(1, 1)));
        return new ModelAndView(reStr,context);
    }

    @RequestMapping("/edit")
    public ModelAndView editEntity(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = adminUserService.update(adminUserService.formEntity(req, id));
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"修改用户数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"用户名已存在!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(1, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.USER,context);
    }

    @RequestMapping("/del")
    public ModelAndView delEntity(@RequestParam("uid") int uid,@RequestParam("id") int id,@RequestParam("pageNo") int pageNo, HttpServletRequest req){

        Map<String, Object> context = new HashMap<String, Object>();
        int reDate = adminUserService.delete(id);
        if (reDate > 0){
            context.put(FinalUtil.RETURN_MSG,"删除分类数据成功!");
        }else{
            context.put(FinalUtil.RETURN_MSG,"删除分类数据失败!");
        }
        context.put(FinalUtil.QUERY_PAGE, queryPageService.getEntitys(queryPageService.formEntity(1, pageNo)));
        context.put(FinalUtil.USER_ENTITY,adminUserService.get(uid));
        return new ModelAndView(FileUtil.USER,context);
    }

}
