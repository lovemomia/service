package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.encrypt.CommonUtil;
import cn.momia.common.encrypt.SHA1Util;
import cn.momia.common.error.SDKRuntimeException;
import cn.momia.service.deal.payment.gateway.MapWrappedParam;
import cn.momia.service.deal.payment.gateway.SignParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * hoze on 15/6/9.
 *
 * paySign字段是对本次发起JSAPI的行为进行鉴权,只有通过了paySign鉴权,才能继续对package鉴权并生成预支付单。这里将定义paySign的生成规则。
 * 参与paySign签名的字段包括:appid、timestamp、noncestr、package以及appkey(即paySignkey)。这里signType并不参与签名。
 * 对所有待签名参数按照字段名的ASCII码从小到大排序(字典序)后,使用URL键值对的格式(即 key1=value1&key2=value2...)拼接成字符串string1。这里需要注意的是所有参数名均为小写字符,例如appId在排序后字符串则为appid;
 * 对string1作签名算法,字段名和字段值都采用原始值(此时package的value就对应了生成的package),并进行URL转义。具体签名算法为paySign=SHA1(string)。
 */
public class WechatpaySignParam extends MapWrappedParam implements SignParam {

    private String AppId;       //公众号ID 商户注册具有支付权限的公众号成功后即可获得;
    private String timeStamp;   //时间戳 字符串类型 32个字节以下 商户生成,从1970年1月1日00:00:00至今的秒数,即当前的时间,且最终需要转换为字符串形式;
    private String nonceStr;    //随机字符串 字符串类型,32 个字节以下
    private String packages;    //订单详情扩展 字符串类型,4096个字节以下
    private String paySign;     //签名
    private String AppKey;  //公众号支付请求中用于加密的密钥 Key,可验证商户唯一身份,PaySignKey对应于支付场景中的appKey值。
    private String SignType;
    private String traceid;//交易号：自定义，可用于订单的查询和跟踪，建议根据支付用户信息生成此Id

    private String PartnerKey = "";

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    public String getAppKey() {
        return AppKey;
    }

    public void setAppKey(String appKey) {
        AppKey = appKey;
    }

    public String getSignType() {
        return SignType;
    }

    public void setSignType(String signType) {
        SignType = signType;
    }

    public String getPartnerKey() {
        return PartnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        PartnerKey = partnerKey;
    }

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public Boolean checkParameters(WechatpaySignParam singParam) {
        if (singParam.getAppId() == ""
                || singParam.getNonceStr() == ""
                || singParam.getAppKey() == ""
                || singParam.getPackages() == ""
                || singParam.getSignType() == ""
                || singParam.getTimeStamp() == "") {
            return false;
        }
        return true;
    }

    public String getSign(WechatpaySignParam singParam) throws SDKRuntimeException {
        HashMap<String, String> bizObj = getHashMapParam(singParam);
        HashMap<String, String> bizParameters = new HashMap<String, String>();
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
                bizObj.entrySet());

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> item = infoIds.get(i);
            if (item.getKey() != "") {
                bizParameters.put(item.getKey().toLowerCase(), item.getValue());
            }
        }

        if (singParam.getAppKey() == "") {
            throw new RuntimeException("APPKEY为空！");
        }
        bizParameters.put("appkey", singParam.getAppKey());
        String bizString = CommonUtil.FormatBizQueryParaMap(bizParameters, false);
        //System.out.println(bizString);

        return SHA1Util.Sha1(bizString);
    }

    private HashMap<String,String> getHashMapParam(WechatpaySignParam singParam){
        HashMap<String, String> obj = new HashMap<String, String>();

        obj.put("appid", singParam.getAppId());
        obj.put("package", singParam.getPackages());
        obj.put("timestamp", singParam.getTimeStamp());
        obj.put("traceid", singParam.getTraceid());
        obj.put("noncestr", singParam.getNonceStr());

        return obj;
    }
}
