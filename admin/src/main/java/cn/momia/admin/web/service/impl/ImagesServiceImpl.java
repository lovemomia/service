package cn.momia.admin.web.service.impl;

import cn.momia.admin.web.common.ConfigUtil;
import cn.momia.admin.web.entity.Images;
import cn.momia.admin.web.service.ImagesService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hoze on 15/6/18.
 */
@Service
public class ImagesServiceImpl implements ImagesService {

    @Override
    public Images uploadImgs(HttpServletRequest req){
        //转型为MultipartHttpRequest(重点的所在)
        MultipartHttpServletRequest multipartRequest  =  (MultipartHttpServletRequest) req;
        String path = multipartRequest.getSession().getServletContext().getRealPath("/");
        String configpath = ConfigUtil.loadProperties().get("uploadPath").toString();
        String uploadpath = path +"/"+ configpath;
        Images imges = null;
        File folder = new File(uploadpath);
        if(!folder.exists()) {folder.mkdir();}
        Collection<MultipartFile> fileList = multipartRequest.getFileMap().values();
        //multipartRequest.getFiles("fileurl");
        for (MultipartFile file : fileList) {
            imges = new Images();
            String type = file.getContentType();
            //System.out.print("====----====="+type);
            String filename = file.getOriginalFilename();
            //System.out.print("====----====="+filename);
            Map<String,String> mapType = getReturnMap(filename);
            if(filename != null && mapType.get("reData").equals("true")){
                filename = mapType.get("reKey");
            }else {
                continue;
            }

            try {
                InputStream stream = file.getInputStream();
                BufferedImage bi = ImageIO.read(stream);
                int width = bi.getWidth();
                int height = bi.getHeight();
                imges.setName(filename);
                imges.setUrl(configpath + filename);
                imges.setWidth(width);
                imges.setHeigth(height);
                FileUtils.copyInputStreamToFile(file.getInputStream(),new File(uploadpath,filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            return imges;
    }

    /**
     * 获取图片生成名称
     * @return
     */
    private String getImgsNameStr(){
        String reStr = "";
        Random rd = new Random();
        Calendar time = Calendar.getInstance();
        reStr = String.valueOf(time.get(Calendar.YEAR))
                + String.valueOf(time.get(Calendar.MONTH)+1)
                + String.valueOf(time.get(Calendar.DAY_OF_MONTH))
                + String.valueOf(time.get(Calendar.HOUR_OF_DAY))
                + String.valueOf(time.get(Calendar.MINUTE))
                + String.valueOf(time.get(Calendar.SECOND))
                + String.valueOf(rd.nextInt(100));
        return reStr;
    }

    /**
     * 验证图片类型，返回图片类型是否为true及名称
     * @param file_type
     * @return
     */
    private Map<String,String> getReturnMap(String file_type){

        Map<String,String> reMap = new HashMap<String, String>();
        List<String> ls = Arrays.asList(new String[]{".jpg", ".jepg", ".bmp",".gif",".png"});

        for (int i = 0; i < ls.size() ; i++) {
            if (file_type.endsWith(ls.get(i))){
                reMap.put("reData","true");
                reMap.put("reKey",getImgsNameStr() + ls.get(i));
                break;
            }
        }
        return reMap;

    }
}
