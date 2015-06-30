package cn.momia.admin.web.entity;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 15/6/15.
 */
public class ReturnResult {
    private Map<String, Object> context;
    private String return_html;

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public String getReturn_html() {
        return return_html;
    }

    public void setReturn_html(String return_html) {
        this.return_html = return_html;
    }

    public ModelAndView returnView(ReturnResult res){
        ModelAndView view = new ModelAndView();
        view.addObject(res.getContext());
        view.addObject(res.getReturn_html());

        return view;
    }
}
