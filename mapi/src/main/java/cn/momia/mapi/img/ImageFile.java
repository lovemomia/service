package cn.momia.mapi.img;

import java.util.ArrayList;
import java.util.List;

public class ImageFile {
    private static String domain;

    public static void setDomain(String domain) {
        ImageFile.domain = domain;
    }

    public static String url(String path) {
        return domain + path;
    }

    public static List<String> urls(List<String> paths) {
        List<String> urls = new ArrayList<String>();

        for (String path : paths) {
            urls.add(url(path));
        }

        return urls;
    }
}
