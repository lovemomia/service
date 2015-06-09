package cn.momia.common.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtil {
    public static InputStream openFileInputStream(String fileName) {
        InputStream inputStream = null;

        // 先尝试直接打开文件
        try {
            inputStream = new FileInputStream(fileName);
        } catch (Exception e) {
            // 直接打开文件失败时忽略异常，尝试从classpath下加载文件
        }

        if (inputStream == null) {
            try {
                File file = getFileInClassPath(fileName);
                inputStream = new FileInputStream(file);
            } catch (Exception e) {
                throw new RuntimeException("fail to open file input stream of file: " + fileName);
            }
        }

        return inputStream;
    }

    private static File getFileInClassPath(String fileName) throws FileNotFoundException {
        String[] classPaths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

        for (String path : classPaths) {
            File parentFile = new File(path);
            if (parentFile.exists() && parentFile.isDirectory()) {
                File file = new File(parentFile, fileName);
                if (file.exists()) return file;
            }
        }

        throw new FileNotFoundException(fileName);
    }
}
