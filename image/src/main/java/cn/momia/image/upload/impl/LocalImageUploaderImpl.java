package cn.momia.image.upload.impl;

import cn.momia.image.upload.Image;
import cn.momia.image.upload.ImageUploadResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalImageUploaderImpl extends AbstractImageUploader {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public ImageUploadResult upload(Image image) throws IOException
    {
        ImageUploadResult result = new ImageUploadResult();

        byte[] imageBytes = IOUtils.toByteArray(image.getFileStream());

        String relativePath = getImageRelativePath(imageBytes);
        String fullPath = getImageFullPath(relativePath);

        File outputFile = new File(fullPath);
        prepareOutputDir(outputFile);

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        outputStream.write(imageBytes);
        IOUtils.closeQuietly(outputStream);

        BufferedImage savedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        result.setPath(File.separator + relativePath);
        result.setWidth(savedImage.getWidth());
        result.setHeight(savedImage.getHeight());

        return result;
    }

    private String getImageRelativePath(byte[] imageBytes)
    {
        String date = DATE_FORMAT.format(new Date());
        String imageKey = DigestUtils.md5Hex(imageBytes);

        return StringUtils.join(new String[] { date, imageKey }, File.separator) + ".jpg";
    }

    private String getImageFullPath(String relativePath)
    {
        return StringUtils.join(new String[] { conf.getString("Image.Upload.Local.Dir"), relativePath }, File.separator);
    }

    private void prepareOutputDir(File outputFile)
    {
        File parent = outputFile.getParentFile();
        if (!parent.exists())
        {
            synchronized (this)
            {
                if (!parent.exists()) parent.mkdirs();
            }
        }
    }
}
