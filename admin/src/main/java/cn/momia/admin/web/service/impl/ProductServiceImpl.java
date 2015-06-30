package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.ConfigUtil;
import cn.momia.admin.web.common.FinalUtil;
import cn.momia.admin.web.common.StringUtil;
import cn.momia.admin.web.entity.Category;
import cn.momia.admin.web.entity.City;
import cn.momia.admin.web.entity.DataBean;
import cn.momia.admin.web.entity.Place;
import cn.momia.admin.web.entity.Product;
import cn.momia.admin.web.entity.ProductImg;
import cn.momia.admin.web.entity.Sku;
import cn.momia.admin.web.entity.SkuPrice;
import cn.momia.admin.web.service.CategoryService;
import cn.momia.admin.web.service.CityService;
import cn.momia.admin.web.service.PlaceService;
import cn.momia.admin.web.service.ProductImgService;
import cn.momia.admin.web.service.ProductService;
import cn.momia.admin.web.service.SkuService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.StringBuffer;

/**
 * Created by hoze on 15/6/15.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductImgService productImgService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CityService cityService;

    @Autowired
    private SkuService skuService;


    @Resource
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product get(int id) {
        String sql = "select id,cityId,categoryId,placeId,title,cover,crowd,content,sales,status,addTime from t_product where id = ? and status > ? ";
        final Object [] params = new Object[]{id, FinalUtil.DEL_STATUS};
        final Product entity = new Product();
        jdbcTemplate.query(sql,params, new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException {
                entity.setId(rs.getInt("id"));
                entity.setCityId(rs.getInt("cityId"));
                entity.setCategoryId(rs.getInt("categoryId"));
                entity.setPlaceId(rs.getInt("placeId"));
                entity.setCover(rs.getString("cover"));
                entity.setCrowd(rs.getString("crowd"));
                entity.setTitle(rs.getString("title"));
                entity.setContent(rs.getString("content"));
                entity.setSales(rs.getInt("sales"));
                entity.setStatus(rs.getInt("status"));
                entity.setAddTime(rs.getString("addTime"));
            }
        });

        return entity;
    }

    @Override
    public List<Product> getEntitys() {
        List<Product> reData = new ArrayList<Product>();
        String sql = "select id,cityId,categoryId,placeId,title,cover,crowd,content,sales,status,addTime from t_product where status > ? order by id desc";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Product entity = new Product();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setCityId(Integer.parseInt(list.get(i).get("cityId").toString()));
                entity.setCategoryId(Integer.parseInt(list.get(i).get("categoryId").toString()));
                entity.setPlaceId(Integer.parseInt(list.get(i).get("placeId").toString()));
                entity.setTitle(list.get(i).get("title").toString());
                entity.setCover(list.get(i).get("cover").toString());
                entity.setCrowd(list.get(i).get("crowd").toString());
                entity.setContent(list.get(i).get("content").toString());
                entity.setSales(Integer.parseInt(list.get(i).get("sales").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public List<Product> getEntitysByKey(int categoryId) {
        List<Product> reData = new ArrayList<Product>();
        String sql = "select id,cityId,categoryId,placeId,title,cover,crowd,content,sales,status,addTime from t_product where categoryId = ? and status > ? ";
        Object [] params = new Object[]{categoryId, FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Product entity = new Product();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setCityId(Integer.parseInt(list.get(i).get("cityId").toString()));
                entity.setCategoryId(Integer.parseInt(list.get(i).get("categoryId").toString()));
                entity.setPlaceId(Integer.parseInt(list.get(i).get("placeId").toString()));
                entity.setTitle(list.get(i).get("title").toString());
                entity.setCover(list.get(i).get("cover").toString());
                entity.setCrowd(list.get(i).get("crowd").toString());
                entity.setContent(list.get(i).get("content").toString());
                entity.setSales(Integer.parseInt(list.get(i).get("sales").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }

    @Override
    public int insert(Product entity) {
        String sql = "insert into t_product(cityId,categoryId,placeId,title,cover,crowd,content,sales,status,addTime) value(?, ?, ?, ?, ?, ?, ?, ?, ?, NOW()) ";
        Object [] params = new Object[]{entity.getCityId(),entity.getCategoryId(), entity.getPlaceId(), entity.getTitle(), entity.getCover(),entity.getCrowd(), "", 0, FinalUtil.ADD_STATUS};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int update(Product entity) {
        String sql = "update t_product set cityId = ?, categoryId = ? ,placeId = ?, title = ?, cover = ?, crowd = ? where id = ? ";
        Object [] params = new Object[]{entity.getCityId(), entity.getCategoryId(),entity.getPlaceId(), entity.getTitle(),entity.getCover(),entity.getCrowd(), entity.getId()};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int update_content(int pid,String contentJson) {
        String sql = "update t_product set content = ? where id = ? ";
        Object [] params = new Object[]{contentJson,pid};
        int reData = jdbcTemplate.update(sql,params);
        return reData;
    }

    @Override
    public int delete(int id) {
        String sql = "update t_product set status = ? where id = ? ";
        Object [] params = new Object[]{FinalUtil.DEL_STATUS,id};
        int reData = jdbcTemplate.update(sql,params);
        if (reData > 0) {
            String sql_sku = "update t_sku set status = ? where productId = ? ";
            Object[] params_sku = new Object[]{FinalUtil.DEL_STATUS, id};
            reData = jdbcTemplate.update(sql_sku, params_sku);
        }
        return reData;
    }

    @Override
    public Product formEntity(HttpServletRequest request, int id) {
        Product entity = new Product();
        entity.setId(id);
        entity.setCityId(Integer.parseInt(request.getParameter("cityId")));
        entity.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        entity.setPlaceId(Integer.parseInt(request.getParameter("placeId")));
        entity.setTitle(request.getParameter("title"));
        entity.setCover(request.getParameter("cover"));
        entity.setCrowd(request.getParameter("crowd"));

        return entity;
    }

    @Override
    public String getContentJsonStr(HttpServletRequest req){
        List<DataBean> ls = new ArrayList<DataBean>();
        String[] lsStr = new String[]{"hdts","hdsm","hdlc","jhxx","wxts","drjs"};

        for (int m = 0;m < lsStr.length; m++){
           DataBean bean = getDataBean(req,lsStr[m]);
            ls.add(bean);
        }
        return JSONObject.toJSONString(ls).replace("\r\n","");
    }

    private DataBean getDataBean(HttpServletRequest req,String typeStr){
        DataBean bean = new DataBean();
        List<Map<String,String>> mapls = new ArrayList<Map<String, String>>();
        int intx = 5;
        if (typeStr.equals("jhxx") || typeStr.equals("wxts")){
            intx = 3;
        }
        if (typeStr.equals("hdts") || typeStr.equals("hdlc") || typeStr.equals("drjs") || typeStr.equals("wxts") ){
            for (int i = 0; i < intx; i++) {
                int inty = i + 1;
                String hdts = req.getParameter(typeStr+inty);
                if(hdts != null && !hdts.equals("") ){
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("text",hdts);
                    mapls.add(map);
                }
            }
            if (mapls != null && !mapls.equals("")) {
                if (typeStr.equals("hdts")) {
                    bean.setTitle("活动特色");
                    bean.setStyle("ol");
                    String hdts_lk = req.getParameter("hdts_lk");
                    if (hdts_lk != null && !hdts_lk.equals("") ) {
                        Map<String, String> mapl = new HashMap<String, String>();
                        mapl.put("label", req.getParameter("hdts_lb1"));
                        mapl.put("link", hdts_lk);
                        mapls.add(mapl);
                    }
                }
                if (typeStr.equals("hdlc")) {
                    bean.setTitle("活动流程");
                    bean.setStyle("ul");
                }
                if (typeStr.equals("drjs")) {
                    bean.setTitle("达人介绍");
                    bean.setStyle("none");
                    String drjs_url = req.getParameter("furl");
                    if (drjs_url != null && !drjs_url.equals("") ) {
                        Map<String, String> mapl = new HashMap<String, String>();
                        mapl.put("img", drjs_url);
                        mapls.add(mapl);
                    }
                }
                if (typeStr.equals("wxts")) {
                    bean.setTitle("温馨提示");
                    bean.setStyle("none");
                }
                bean.setBody(mapls);
            }
        }
        if (typeStr.equals("hdsm") || typeStr.equals("jhxx")){

            for (int j = 0; j < intx; j++) {
                int inty = j+1;
                String hdsm_lb = req.getParameter(typeStr+"_lb"+inty);
                String hdsm = req.getParameter(typeStr+inty);
                if(hdsm != null && !hdsm.equals("")){
                    if(hdsm != null && !hdsm_lb.equals("") ){
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("label",hdsm_lb);
                        map.put("text",hdsm);
                        mapls.add(map);
                    }else{
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("text",hdsm);
                        mapls.add(map);
                    }
                }
            }
            if (mapls != null && !mapls.equals("")) {
                if (typeStr.equals("hdsm")) {
                    bean.setTitle("活动说明");
                    bean.setStyle("ol");
                } else {
                    bean.setTitle("集合信息");
                    bean.setStyle("ul");
                }
                bean.setBody(mapls);
            }
        }

        return bean;
    }

    @Override
    public Map<String, String> getContentJsontoMap(String jsonStr){
        Map<String, String> reData = new HashMap<String, String>();
        String[] lsStren = new String[]{"hdts","hdsm","hdlc","jhxx","wxts","drjs"};
        String[] lsStrcn = new String[]{"活动特色","活动说明","活动流程","集合信息","温馨提示","达人介绍"};
        List<Map<String,Object>> list = StringUtil.parseJSON2List(jsonStr);
        for (int i = 0; i < list.size() ; i++) {
            List<Map<String,String>> body = (List<Map<String, String>>) list.get(i).get("body");
            String title = list.get(i).get("title").toString();
            if (title.equals("活动特色")){
                reData.put(lsStren[0],getStr(body,lsStren[0],5));
            }
            if (title.equals("活动说明")){
                reData.put(lsStren[1],getStr(body,lsStren[1],5));
            }
            if (title.equals("活动流程")){
                reData.put(lsStren[2],getStr(body,lsStren[2],5));
            }
            if (title.equals("集合信息")){
                reData.put(lsStren[3],getStr(body,lsStren[3],3));
            }
            if (title.equals("温馨提示")){
                reData.put(lsStren[4],getStr(body,lsStren[4],3));
            }
            if (title.equals("达人介绍")){
                reData.put(lsStren[5], getStr(body, lsStren[5],5));
            }
        }

        return reData;
    }

    private String getStr(List<Map<String,String>> body,String flag,int intx){
        StringBuffer reData = new StringBuffer();
        int numlink = 0;
        int numimg = 0;
        if (!body.equals("") && body != null){
            for (int i = 0; i < body.size(); i++) {
                int intnum = i+1;
                String textStr = body.get(i).get("text");
                String strlb = body.get(i).get("label");
                String strlk = body.get(i).get("link");
                String strimg = body.get(i).get("img");
                if(strimg != null && !strimg.equals("") ){
                    reData.append("<label class='control-label'>达人图片</label>");
                }
                reData.append(getDivStartAndEnd(0));

                if (strlk != null && !strlk.equals("")){
                    numlink = 1;
                    String name_id_lb = flag + "_lb1";
                    String name_id_lk = flag + "_lk";
                    reData.append("<input id='"+name_id_lb+"' name='"+name_id_lb+"' value='"+strlb+"'><br>");
                    reData.append("<input id='"+name_id_lk+"' name='"+name_id_lk+"' value='"+strlk+"'><br>");
                }else{
                    if (strlb != null && !strlb.equals("")){
                        String name_id = flag + "_lb" + intnum;
                        reData.append("<input id='"+name_id+"' name='"+name_id+"' value='"+strlb+"'><br>");
                    }
                }
                if (textStr != null && !textStr.equals("")){
                    String name_id = flag + intnum;
                    reData.append("<textarea id='"+name_id+"' name='"+name_id+"' rows='3' cols='5' >"+textStr+"</textarea>");
                }
                if (strimg != null && !strimg.equals("")){
                    numimg = 1;
                    reData.append("<img id='img_a' src='"+ ConfigUtil.loadProperties().getProperty("serverPath")+strimg+"' height='100ps' width='200ps'></img><br>");
                    reData.append("<input id='fileurl' type='file' size='20' name='fileurl'>");
                    reData.append("<input id='furl' name='furl' type='hidden' value='"+strimg+"'>");
                    reData.append("<input id='filepath' name='filepath' type='hidden' value='"+ConfigUtil.loadProperties().getProperty("serverPath")+"'>");
                }
                reData.append(getDivStartAndEnd(1));
            }

            reData.append(getSYStr(flag,intx,body.size(),numlink,numimg));
        }
        return reData.toString();
    }

    private String getSYStr(String flag,int intx,int inty,int numlink,int numimg){
        StringBuffer reData = new StringBuffer();
        if (flag.equals("hdts")) {
            if (numlink == 0) {
                String name_id_lb = flag + "_lb1";
                String name_id_lk = flag + "_lk";
                reData.append(getDivStartAndEnd(0));
                reData.append("<input id='" + name_id_lb + "' name='" + name_id_lb + "' value=''><br>");
                reData.append("<textarea id='" + name_id_lk + "' name='" + name_id_lk + "' rows='3' cols='5' ></textarea>");
                reData.append(getDivStartAndEnd(1));
            }else {
                inty = inty-1;
            }
        }
        if (flag.equals("drjs")){
            if(numimg == 0) {
                reData.append(getDivStartAndEnd(0));
                reData.append("<img id='img_a' src='' height='100ps' width='200ps' alt='未上传'></img><br>");
                reData.append("<input id='fileurl' type='file' size='20' name='fileurl'>");
                reData.append("<input id='furl' name='furl' type='hidden' value=''>");
                reData.append("<input id='filepath' name='filepath' type='hidden' value='" + ConfigUtil.loadProperties().getProperty("serverPath") + "'>");
                reData.append(getDivStartAndEnd(1));
            }else{
                inty = inty-1;
            }
        }
            if (intx > inty){
            if(flag.equals("hdts") || flag.equals("hdlc") || flag.equals("wxts") || flag.equals("drjs")){
                    for (int i = 0; i <= intx-inty; i++) {
                        inty = inty + 1;
                        String name_id = flag + inty;
                        reData.append(getDivStartAndEnd(0));
                        reData.append("<textarea id='"+name_id+"' name='"+name_id+"' rows='3' cols='5' ></textarea>");
                        reData.append(getDivStartAndEnd(1));
                    }
                }
            }
            if(flag.equals("hdsm") || flag.equals("jhxx") ){
                for (int j = 0; j <= intx-inty; j++) {
                    inty = inty + 1;
                    String name_id_lb = flag + "_lb" + inty;
                    String name_id = flag + inty;
                    reData.append(getDivStartAndEnd(0));
                    reData.append("<input id='"+name_id_lb+"' name='"+name_id_lb+"' value=''><br>");
                    reData.append("<textarea id='"+name_id+"' name='"+name_id+"' rows='3' cols='5' ></textarea>");
                    reData.append(getDivStartAndEnd(1));
                }
            }


        return reData.toString();
    }

    private String getDivStartAndEnd(int intx){
        String reData = "<div class='control-group'><div class='controls'>";
        if (intx > 0){
            reData = "</div></div>";
        }
        return reData;
    }

    @Override
    public String getPreviewInfo(int id){
        Product product = this.get(id);

        StringBuffer reData = new StringBuffer();
        reData.append("<tr><p>");
        reData.append(product.getTitle());
        reData.append("</p></tr>");
        reData.append("<tr><p>");
        List<ProductImg> imgs = productImgService.getEntitysByKey(id);
        if (imgs.size()>0) {
            for (int i = 0; i < imgs.size(); i++) {
                int intx = i + 1;
                String img_id = "img" + intx;
                reData.append("<p><img id='"+img_id+"' src='"+ConfigUtil.loadProperties().getProperty("serverPath")+ imgs.get(i).getUrl()+"'></img><br></p>");
            }
        }else{
            reData.append("<p><img id='img1' src='' alt = '没有图片'></img><br></p>");
        }
        reData.append("</p></tr>");
        Place place = placeService.get(product.getPlaceId());
        Category category = categoryService.get(product.getCategoryId());
        City city = cityService.get(product.getCityId());

        List<Sku> sku = skuService.getEntitysByKey(id);
        String skuProperties ="";
        String skuprice = "0.0";
        SkuPrice skuPriceEntity = null;
        if (sku.size() > 0){
            skuPriceEntity = skuService.getPricesMinValue(sku.get(0).getPrices());
            skuprice = skuPriceEntity.getPrice();
            skuProperties = skuService.getProperties(sku.get(0).getProperties());
        }


        reData.append("<tr><p>");
        reData.append("￥"+skuprice);
        reData.append("</p></tr>");

        reData.append("<tr><p>");
        reData.append(product.getCrowd());
        reData.append("</p></tr>");

        reData.append("<tr><p>");
        reData.append("已购买："+product.getSales());
        reData.append("</p></tr>");

        reData.append("<tr><p>");
        reData.append(skuProperties);
        reData.append("</p></tr>");

        reData.append("<tr><p>");
        reData.append(place.getAddress());
        reData.append("</p></tr>");

        List<Map<String,Object>> list = StringUtil.parseJSON2List(product.getContent());
        for (int j = 0; j < list.size(); j++) {
            reData.append("<tr>");
            reData.append("<p>"+list.get(j).get("title")+"</p>");
            List<Map<String,String>> body = (List<Map<String, String>>) list.get(j).get("body");
            for (int k = 0; k < body.size(); k++) {
                String txtStr = body.get(k).get("text");
                String lbStr = body.get(k).get("label");
                String lkStr = body.get(k).get("link");
                String imgStr = body.get(k).get("img");
                reData.append("<tr>");
                if (imgStr != null && !imgStr.equals("")){
                    reData.append("<img id='drimg' src='"+ConfigUtil.loadProperties().getProperty("serverPath")+ imgStr +"'></img><br>");
                }
                if (txtStr != null && !txtStr.equals("")){
                    if (lbStr != null && !lbStr.equals("")) {
                        reData.append("<p>");
                        reData.append(lbStr+":"+txtStr);
                        reData.append("</p>");
                    }else{
                        reData.append("<p>"+txtStr+"</p>");
                    }
                }
                if (lkStr != null && !lkStr.equals("")){
                    if (lbStr != null && !lbStr.equals("")) {
                        reData.append("<p><a href="+lkStr+" >"+lbStr+"</a></p>");
                    }else{
                        reData.append("<p><a href="+lkStr+" >点击链接</a></p>");
                    }
                }
                reData.append("</tr>");
            }
            reData.append("</tr>");
        }


        return reData.toString();
    }

    @Override
    public List<Product> getEntities(List<Product> products){
        if(products.size() > 0){
            for (int i = 0; i <products.size() ; i++) {
                Product entity = products.get(i);
                entity.setCityname(cityService.get(entity.getCityId()).getName());
                entity.setCatename(categoryService.get(entity.getCategoryId()).getName());
                entity.setPlacename(placeService.get(entity.getPlaceId()).getName());
            }
        }

        return products;
    }

    @Override
    public List<Product> getQueryPages(int start_row,int end_row) {
        List<Product> reData = new ArrayList<Product>();
        String sql = "select id,cityId,categoryId,placeId,title,cover,crowd,content,sales,status,addTime from t_product where status > ? order by id desc limit "+start_row+","+end_row;
        Object [] params = new Object[]{FinalUtil.DEL_STATUS};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
        if(list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                Product entity = new Product();
                entity.setId(Integer.parseInt(list.get(i).get("id").toString()));
                entity.setCityId(Integer.parseInt(list.get(i).get("cityId").toString()));
                entity.setCategoryId(Integer.parseInt(list.get(i).get("categoryId").toString()));
                entity.setPlaceId(Integer.parseInt(list.get(i).get("placeId").toString()));
                entity.setTitle(list.get(i).get("title").toString());
                entity.setCover(list.get(i).get("cover").toString());
                entity.setCrowd(list.get(i).get("crowd").toString());
                entity.setContent(list.get(i).get("content").toString());
                entity.setSales(Integer.parseInt(list.get(i).get("sales").toString()));
                entity.setStatus(Integer.parseInt(list.get(i).get("status").toString()));
                entity.setAddTime(list.get(i).get("addTime").toString());
                reData.add(entity);
            }
        }

        return reData;
    }
}
