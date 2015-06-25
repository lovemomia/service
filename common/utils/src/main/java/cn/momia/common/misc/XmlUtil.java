package cn.momia.common.misc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {
    public static String paramsToXml(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();

        builder.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
        }
        builder.append("</xml>");

        return builder.toString();
    }

    public static Map<String, String> xmlToParams(String xml) {
        Map<String, String> params = new HashMap<String, String>();

        try {
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(new ByteArrayInputStream(xml.getBytes()));
            List<Element> elements = doc.getRootElement().elements();
            for (Element element : elements) {
                String name = element.getName();
                String value = element.getTextTrim();
                params.put(name, value);
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return params;
    }
}
