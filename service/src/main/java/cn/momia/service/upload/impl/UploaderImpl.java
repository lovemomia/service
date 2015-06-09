package cn.momia.service.upload.impl;

import cn.momia.common.config.Configuration;
import cn.momia.service.upload.Image;
import cn.momia.service.upload.Result;
import cn.momia.service.upload.Uploader;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 15-6-2.
 */
public class UploaderImpl implements Uploader {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Configuration conf;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }


    public Result upload(Image image) throws IOException {
        String ACCESS_ID = conf.getString("OSS.AccessId");
        String ACCESS_KEY = conf.getString("OSS.AccessKey");
        String ENDPOINT = conf.getString("OSS.EndPoint");

        String bucketName = ACCESS_ID.toLowerCase() + conf.getString("BuildBucketName");//唯一的bucketName
        Result result = new Result();//返回结果
        byte[] imageBytes = IOUtils.toByteArray(image.getFileStream());
        int length = imageBytes.length;//文件的长度

        //因为文件流指针在末尾，所以当上传时再次读取这个流对象 fileInputStream的时候文件没有读取到内容所以再次获取文件流
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

        String key = DigestUtils.md5Hex(imageBytes) + ".jpg";//上传到OSS的文件名

        // 使用默认的OSS服务器地址创建OSSClient对象。
        OSSClient client = new OSSClient(ENDPOINT, ACCESS_ID, ACCESS_KEY);


        ensureBucket(client, bucketName);

        setBucketPublicReadable(client, bucketName);
        uploadFile(client, bucketName, key, imageStream, length);


        BufferedImage savedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        result.setPath(key);
        result.setWidth(savedImage.getWidth());
        result.setHeight(savedImage.getHeight());

        return result;
    }

    // 创建Bucket.
    private static void ensureBucket(OSSClient client, String bucketName)
            throws OSSException, ClientException {

        try {
            // 创建bucket
            client.createBucket(bucketName);
        } catch (ServiceException e) {
            if (!OSSErrorCode.BUCKET_ALREADY_EXISTS.equals(e.getErrorCode())) {
                // 如果Bucket已经存在，则忽略
                throw e;
            }
        }
    }


    // 把Bucket设置为所有人可读
    private static void setBucketPublicReadable(OSSClient client, String bucketName)
            throws OSSException, ClientException {
        //创建bucket
        client.createBucket(bucketName);

        //设置bucket的访问权限，public-read-write权限
        client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
    }

    // 上传文件
    private static void uploadFile(OSSClient client, String bucketName, String key, InputStream imageStream, int length)
            throws OSSException, ClientException, IOException {

        ObjectMetadata objectMeta = new ObjectMetadata();
        // System.out.println(length);
        objectMeta.setContentLength(length);

        // 可以在metadata中标记文件类型
        objectMeta.setContentType("upload/jpeg");

        //   InputStream input = upload.getFileStream();
        client.putObject(bucketName, key, imageStream, objectMeta);
    }

    // 下载文件
    private static void downloadFile(OSSClient client, String bucketName, String key, String filename)
            throws OSSException, ClientException {
        client.getObject(new GetObjectRequest(bucketName, key),
                new File(filename));
    }
}