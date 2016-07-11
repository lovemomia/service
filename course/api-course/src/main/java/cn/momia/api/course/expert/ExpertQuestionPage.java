package cn.momia.api.course.expert;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * Created by hoze on 16/6/15.
 */
public class ExpertQuestionPage {

    private List<ExpertQuestion> list;
    private int nextIndex;
    private int totalCount;

    public List<ExpertQuestion> getList() {
        return list;
    }

    public void setList(List<ExpertQuestion> list) {
        this.list = list;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
