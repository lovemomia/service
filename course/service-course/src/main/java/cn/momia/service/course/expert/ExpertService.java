package cn.momia.service.course.expert;

import cn.momia.api.course.expert.Expert;
import cn.momia.api.course.expert.ExpertBanner;
import cn.momia.api.course.expert.ExpertCourse;
import cn.momia.api.course.expert.ExpertCoursePage;
import cn.momia.api.course.expert.ExpertHear;
import cn.momia.api.course.expert.ExpertOrder;
import cn.momia.api.course.expert.ExpertQuestion;
import cn.momia.api.course.expert.ExpertQuestionPage;
import cn.momia.api.course.expert.MyCentre;
import cn.momia.api.course.expert.UserAsset;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hoze on 16/6/15.
 */
public interface ExpertService {

    public Expert getExpert(int expertId);

    public ExpertCourse getExpertCourse(int courseId);

    public List<ExpertCourse> getExpertCourses();

    public ExpertQuestion getExpertQuestion(int questionId, int status);

    public ExpertHear getExpertHear(int questionId, int userId);

    public List<ExpertQuestion> getExpertQuestions(int courseId);

    public List<ExpertBanner> getExpertBanners();

    public ExpertCoursePage getExpertCoursesPage(int expertId,int start);

    public ExpertQuestionPage getExpertQuestionsPage(int course,int start);

    public ExpertQuestionPage getMyAnswerNullPage(int userId, int start);

    public int updateQuestionAnswer(long qid, String answer, int mins);

    public long insertHear(final int questionId, final int userId);

    public long insertQuestion(int expertId,int courseId, int userId, String content);

    public long insertOrder(int questionId, int courseId, int userId, int hearId, int type, BigDecimal price, String mobile);

    public ExpertOrder getExpertOrder(int orderId, int status);

    public ExpertOrder getExpertOrderById(int userId, int cid, int qid, int hid, int status);

    public boolean pay(final WdPayment payment);

    public boolean isUserAsset(int userId,int orderId);

    public UserAsset getUserAsset(int userId);

    public void updateUserAsset(int userId,BigDecimal number,int type);

    public void insertUserAsset(int userId, BigDecimal number);

    public ExpertQuestionPage getMyQuestionsPage(int userId, int type, int start);

    public MyCentre getMyCentre(int userId);

    public int updateCourseCount(int cid);


}
