package cn.momia.image.upload;

import java.io.IOException;

public interface ImageUploader {
    ImageUploadResult upload(Image image) throws IOException;
}
