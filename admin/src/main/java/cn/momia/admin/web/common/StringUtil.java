package cn.momia.admin.web.common;

import cn.momia.admin.web.entity.DataBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
public class StringUtil {

    public static List<Map<String, Object>> parseJSON2List(String jsonStr){
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject json2 = it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }


    public static Map<String, Object> parseJSON2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }


    public static List<Map<String, Object>> getListByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2List(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Map<String, Object> getMapByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2Map(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //test
    public static void main(String[] args) {
        String str = "[       {         \"title\": \"活动特色\",         \"style\": \"ol\",         \"body\": [           {             \"text\": \"培６月，麦淘的福利一波一波停不下来，简直成了宝爸宝妈们丰收的季节。此前免费的亲子摄影，１元看喜洋洋星光展几乎好评如潮。如果你还没有抢到，第三波福利千万不要错过！\"           },           {             \"text\": \"端午节将近，小麦为大家准备了端午大红包。只要花费１毛钱，跟我们完成一个小小的互动，就有机会把它带回家，速度行动吧！\"           },           {             \"text\": \"查看图文详情\",             \"link\": \"http://www.maitao.com/detail/A0701506151141221346\"           }         ]       },      {         \"title\": \"活动说明\",         \"style\": \"none\",         \"body\": [           {             \"label\": \"费用\",             \"text\": \"50元/儿童，家长不收费，限两名家长陪同，含活动组织费、材料费和保险费\"           },           {             \"label\": \"适合年龄\",             \"text\": \"2-3岁，需家长陪同\"           },           {             \"label\": \"成团条件\",             \"text\": \"5名儿童成团，8名儿童以内\"           },           {             \"text\": \"本活动为哆啦亲子发起的活动，自行前往目的地集合\"           }         ]       },       {         \"title\": \"活动流程\",         \"style\": \"ul\",         \"body\": [           {             \"text\": \"9:45 集合\"           },           {             \"text\": \"10:00 入馆\",           },           {             \"text\": \"10:35 开场白\"           }         ]       },       {         \"title\": \"集合信息\",         \"style\": \"ul\",         \"body\": [           {             \"text\": \"时间：2015-06-20 9:30\"           },           {             \"text\": \"地点：中山公园正门\"           },         ]       },       {         \"title\": \"达人介绍\",         \"style\": \"none\",         \"body\": [           {             \"img\": \"http://s.momia.cn/2015-05-19/dc0b3ddba449c28a009140217b4ced5d.jpg\"           },           {             \"text\": \"史蒂夫·乔布斯，1955年2月24日生于美国加利福尼亚州旧金山，美国发明家、企业家、美国苹果公司联合创办人。\"           },         ]       }     ]";
        String jstr = "[\n" +
                "    {\n" +
                "        \"body\": [\n" +
                "            {\n" +
                "                \"text\": \"sadasd\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"link\": \"asdasd\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"style\": \"ol\",\n" +
                "        \"title\": \"活动特色\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"body\": [\n" +
                "            {\n" +
                "                \"label\": \"asd\",\n" +
                "                \"text\": \"asdasd\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"style\": \"ol\",\n" +
                "        \"title\": \"活动说明\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"body\": [\n" +
                "            {\n" +
                "                \"text\": \"asdasd\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"text\": \"asdasdasd\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"style\": \"ul\",\n" +
                "        \"title\": \"活动流程\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"body\": [\n" +
                "            {\n" +
                "                \"label\": \"asdasd\",\n" +
                "                \"text\": \"asdasd\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"style\": \"ul\",\n" +
                "        \"title\": \"集合信息\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"body\": [\n" +
                "            {\n" +
                "                \"text\": \"asdasd\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"style\": \"none\",\n" +
                "        \"title\": \"温馨提示\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"body\": [\n" +
                "            {\n" +
                "                \"text\": \"asdasdasd\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"text\": \"asdasdasd\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"style\": \"none\",\n" +
                "        \"title\": \"达人介绍\"\n" +
                "    }\n" +
                "]";
        List<Map<String,Object>> list = parseJSON2List(jstr) ;
        DataBean bean = new DataBean();
        for (int i = 0; i < list.size() ; i++) {
            if (list.get(i).get("title").equals("活动特色")){
                List<Map<String,String>> lists = (List<Map<String, String>>) list.get(i).get("body");
                bean.setTitle("hdts");
                bean.setStyle(list.get(i).get("style").toString());
                bean.setBody(lists);
                System.out.println(bean.getBody());

            }
        }
    }
}
