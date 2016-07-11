package cn.momia.api.course.expert;

import java.math.BigDecimal;

/**
 * Created by hoze on 16/6/23.
 */
public class MyCentre {
    private int questionNumber;
    private int answerNumber;
    private BigDecimal assetNumber;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }

    public BigDecimal getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(BigDecimal assetNumber) {
        this.assetNumber = assetNumber;
    }
}
