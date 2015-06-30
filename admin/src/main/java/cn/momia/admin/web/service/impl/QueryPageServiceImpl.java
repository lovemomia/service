package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.QueryPage;
import cn.momia.admin.web.service.AdminUserService;
import cn.momia.admin.web.service.CategoryService;
import cn.momia.admin.web.service.PlaceService;
import cn.momia.admin.web.service.ProductService;
import cn.momia.admin.web.service.QueryPageService;
import cn.momia.admin.web.service.SkuPropertyService;
import cn.momia.admin.web.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by hoze on 15/6/30.
 */
@Service
public class QueryPageServiceImpl implements QueryPageService {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuPropertyService skuPropertyService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueryPage formEntity(int type,int pageNo){

        QueryPage entity = new QueryPage();
        entity.setPageNo(pageNo);
        //entity.setPageSize();
        entity.setQuery_type(type);

        return entity;
    }

    @Override
    public QueryPage getEntitys(QueryPage queryPage) {
        int pageNo = queryPage.getPageNo();
        int pageSize = queryPage.getPageSize();
        int total = 0;
        List entitys = null;
        if(queryPage.getQuery_type() == 1){
            entitys = adminUserService.getQueryPages((pageNo - 1) * pageSize, pageNo *pageSize);
            total = adminUserService.getEntitys().size();
            queryPage.setList(entitys);
        }
        if(queryPage.getQuery_type() == 2){
            entitys = categoryService.getQueryPages((pageNo-1)*pageSize, pageNo*pageSize);
            total = categoryService.getEntitys().size();
            queryPage.setList(categoryService.getEntities(entitys));
        }
        if(queryPage.getQuery_type() == 3){
            entitys = placeService.getQueryPages((pageNo-1)*pageSize, pageNo*pageSize);
            total = placeService.getEntitys().size();
            queryPage.setList(entitys);
        }
        if(queryPage.getQuery_type() == 4){
            entitys = productService.getQueryPages((pageNo-1)*pageSize, pageNo*pageSize);
            total = productService.getEntitys().size();
            queryPage.setList(productService.getEntities(entitys));
        }
        if(queryPage.getQuery_type() == 5){
            entitys = skuService.getQueryPages((pageNo-1)*pageSize, pageNo*pageSize);
            total = skuService.getEntitys().size();
            queryPage.setList(skuService.getEntities(entitys));
        }
        queryPage.setTotalRecords(total);
        queryPage.setList(entitys);

        queryPage.setTotalPages(this.getTotalPages(total,pageSize));//总页数
        queryPage.setTopPageNo(this.getTopPageNo());//首页
        queryPage.setPreviousPageNo(this.getPreviousPageNo(pageNo));//上一页
        queryPage.setNextPageNo(this.getNextPageNo(pageNo,total,pageSize));//下一页
        queryPage.setBottomPageNo(this.getBottomPageNo(total,pageSize));//尾页

        return queryPage;
    }

    /**
     * 总页数
     * @return
     */
    public int getTotalPages(int totalRecords,int pageSize) {
        return (totalRecords + pageSize - 1) / pageSize;
    }


    /**
     * 取得首页
     * @return
     */
    public int getTopPageNo() {
        return 1;
    }



    /**
     * 上一页
     * @return
     */
    public int getPreviousPageNo(int pageNo) {
        if (pageNo <= 1) {
            return 1;
        }
        return pageNo - 1;
    }


    /**
     * 下一页
     * @return
     */
    public int getNextPageNo(int pageNo,int totalRecords,int pageSize ) {
        if (pageNo >= getBottomPageNo(totalRecords,pageSize)) {
            return getBottomPageNo(totalRecords,pageSize);
        }
        return pageNo + 1;
    }


    /**
     * 取得尾页
     * @return
     */
    public int getBottomPageNo(int totalRecords,int pageSize ) {
        return getTotalPages(totalRecords,pageSize);
    }


}
