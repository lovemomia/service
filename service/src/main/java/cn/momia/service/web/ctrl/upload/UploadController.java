package cn.momia.service.web.ctrl.upload;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.upload.Image;
import cn.momia.service.upload.Result;
import cn.momia.service.upload.Uploader;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private Uploader uploader;

    @RequestMapping(method = { RequestMethod.POST })
    public ResponseMessage upload(HttpServletRequest request) {
        try {
            Image image = parseImage(request);
            Result result = uploader.upload(image);

            return new ResponseMessage(buildResponseData(result));
        } catch (Exception e) {
            LOGGER.error("fail to upload upload file", e);
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "fail to upload upload file");
        }
    }

    private Image parseImage(HttpServletRequest request) throws IOException, FileUploadException {
        Image image = new Image();

        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = upload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (!item.isFormField()) {
                image.setFileName(item.getName());
                image.setFileStream(item.openStream());

                break;
            }
        }

        return image;
    }

    private JSONObject buildResponseData(Result result) {
        JSONObject data = new JSONObject();

        data.put("path", result.getPath());
        data.put("width", result.getWidth());
        data.put("height", result.getHeight());

        return data;
    }
}
