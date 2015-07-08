package cn.momia.mapi.api.v1.dto.base;

import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.composite.ListDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysm on 15-7-7.
 */
public class PlayMateDto implements Dto {
    private String date;
    private String text;
    private ListDto customers;

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public ListDto getCustomers() {
        return customers;
    }

    public PlayMateDto(JSONObject jsonObject) {
        int count = 0;
        int child = 0;
        int adult = 0;
        ListDto customerList = new ListDto();
        this.date = ProductUtil.getSkuScheduler(jsonObject.getJSONArray("skuProperties"));
        JSONArray customersArray = jsonObject.getJSONArray("customers");
        for(int i=0; i<customersArray.size(); i++) {
            CustomerDto customerDto = new CustomerDto(customersArray.getJSONObject(i));
            customerList.add(customerDto);
            JSONArray jsonArray = customersArray.getJSONObject(i).getJSONArray("participants");
            List<ParticipantDto> participantList = new ArrayList<ParticipantDto>();
            for(int j =0 ;j<jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                participantList.add(new ParticipantDto(object));
            }
            count += participantList.size();
            for (ParticipantDto participantDto : participantList)
                if(participantDto.getType() == "儿童")
                    child ++;
            adult = count - child;

        }
        this.customers = customerList;
        this.text =  count +"人已报名(" + adult + "成人" + child + "儿童)";
    }


}
