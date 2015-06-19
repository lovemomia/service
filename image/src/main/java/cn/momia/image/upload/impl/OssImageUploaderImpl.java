package cn.momia.image.upload.impl;

import cn.momia.image.upload.Image;
import cn.momia.image.upload.ImageUploadResult;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OssImageUploaderImpl extends AbstractImageUploader {
    private OSSClient ossClient;
    private String bucketName;

    public void init() {
        String AccessId = conf.getString("Oss.AccessId");
        String AccessKey = conf.getString("Oss.AccessKey");
        String EndPoint = conf.getString("Oss.EndPoint");
        ossClient = new OSSClient(EndPoint, AccessId, AccessKey);

        bucketName = conf.getString("Oss.BucketName");
        if (!ossClient.doesBucketExist(bucketName)) {
            throw new RuntimeException("bucket: " + bucketName + " does not exist");
        }
    }

    public ImageUploadResult upload(Image image) throws IOException {
        byte[] imageBytes = IOUtils.toByteArray(image.getFileStream());

        String fileName = DigestUtils.md5Hex(imageBytes) + ".jpg"; //上传到OSS的文件名
        uploadFile(fileName, new ByteArrayInputStream(imageBytes), imageBytes.length);

        ImageUploadResult result = new ImageUploadResult(); //返回结果
        result.setPath(fileName);

        BufferedImage savedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        result.setWidth(savedImage.getWidth());
        result.setHeight(savedImage.getHeight());

        return result;
    }

    // 上传文件
    private void uploadFile(String fileName, InputStream stream, int length) {
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(length);
        // 可以在metadata中标记文件类型
        objectMeta.setContentType("image/jpeg");

        ossClient.putObject(bucketName, fileName, stream, objectMeta);
    }
}