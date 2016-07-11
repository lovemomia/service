package cn.momia.api.course;

import cn.momia.api.course.activity.ActivityEntry;
import cn.momia.api.course.expert.ExpertBanner;
import cn.momia.api.course.expert.ExpertCourse;
import cn.momia.api.course.expert.ExpertCoursePage;
import cn.momia.api.course.expert.ExpertOrder;
import cn.momia.api.course.expert.ExpertQuestion;
import cn.momia.api.course.expert.ExpertQuestionPage;
import cn.momia.api.course.expert.HearResult;
import cn.momia.api.course.expert.MyCentre;
import cn.momia.api.course.expert.QExpert;
import cn.momia.api.course.expert.UserAsset;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import cn.momia.common.core.http.MomiaHttpResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by hoze on 16/6/16.
 */
public class ExpertServiceApi extends HttpServiceApi {

    public List<ExpertCourse> courses() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/expert/wd_courses")), ExpertCourse.class);
    }

    public List<ExpertQuestion> questions(int courseId) {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/expert/wd_questions/%d", courseId)), ExpertQuestion.class);
    }

    public List<ExpertBanner> banners() {
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/expert/wd_banners")), ExpertBanner.class);
    }

    public ExpertCourse get(int courseId) {
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/wd_course/%d", courseId)), ExpertCourse.class);
    }

    public ExpertCoursePage coursePage(int expertId, int start) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("expertId", expertId)
                .add("start", start);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/coursesPage"), builder.build()), ExpertCoursePage.class);
    }

    public ExpertQuestionPage questionPage(int courseId, int start) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("courseId", courseId)
                .add("start", start);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/questionsPage"), builder.build()), ExpertQuestionPage.class);
    }

    public Object questionAnswer(int questionId, String answer, int mins) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("questionId", questionId)
                .add("answer",answer)
                .add("mins", mins);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/questionAnswer"), builder.build()), Boolean.class);
    }

    public QExpert questionExpert(int questionId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("questionId", questionId);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/questionsExpert"), builder.build()), QExpert.class);
    }

    public ExpertOrder questionJoin(int courseId, String utoken, String content) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("courseId", courseId)
                .add("utoken", utoken)
                .add("content", content);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/questionJoin"), builder.build()), ExpertOrder.class);
    }

    public HearResult hearJoin(int qid, String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("qid", qid)
                .add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/hearJoin"), builder.build()), HearResult.class);
    }

    public Object prepayAsset(long orderId, String type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("oid", orderId)
                .add("type", type);
        return execute(MomiaHttpRequestBuilder.POST(url("/expert/payment/prepay/asset"), builder.build()));
    }

    public Object prepayAlipay(long orderId, String type) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("oid", orderId)
                .add("type", type);
        return execute(MomiaHttpRequestBuilder.POST(url("/expert/payment/prepay/alipay"), builder.build()));
    }

    public Object prepayWeixin(long orderId, String type, String code) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("oid", orderId)
                .add("type", type);
        if (!StringUtils.isBlank(code)) builder.add("code", code);
        return execute(MomiaHttpRequestBuilder.POST(url("/expert/payment/prepay/weixin"), builder.build()));
    }

    public boolean callbackAlipay(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/expert/payment/callback/alipay"), params), String.class));
    }

    public boolean callbackWeixin(Map<String, String> params) {
        return "OK".equalsIgnoreCase(executeReturnObject(MomiaHttpRequestBuilder.POST(url("/expert/payment/callback/weixin"), params), String.class));
    }

    public boolean checkPayment(long orderId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("oid", orderId);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/expert/payment/check"), builder.build()), Boolean.class);
    }

    public ExpertQuestionPage myQuestionPages(String utoken, int start) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("type", 0)
                .add("start", start);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/myQuestionPages"), builder.build()), ExpertQuestionPage.class);
    }

    public ExpertQuestionPage myAnswerNullPages(String utoken, int start) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/myAnswerNullPages"), builder.build()), ExpertQuestionPage.class);
    }

    public ExpertQuestionPage myAnswerPages(String utoken, int start) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("type", 1)
                .add("start", start);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/myQuestionPages"), builder.build()), ExpertQuestionPage.class);
    }

    public UserAsset myUserAsset(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/myUserAsset"), builder.build()), UserAsset.class);
    }

    public MyCentre myCentre(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/my_centre"), builder.build()), MyCentre.class);
    }

    public Object course_count(int wid) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("wid", wid);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/expert/course_count"), builder.build()), Boolean.class);
    }

}
