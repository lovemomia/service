package cn.momia.service.course.expert.impl;

import cn.momia.api.course.activity.ActivityEntry;
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
import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.deal.gateway.PayType;
import cn.momia.common.service.AbstractService;
import cn.momia.service.course.activity.Payment;
import cn.momia.service.course.expert.ExpertService;
import cn.momia.service.course.expert.WdPayment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by hoze on 16/6/15.
 */
public class ExpertServiceImpl extends AbstractService implements ExpertService {

    private static final Logger log = LoggerFactory.getLogger(ExpertServiceImpl.class);

    private final int status_one = 1;
    private final int status_two = 2;
    private final int status_three = 3;
    private final int page = 20;

    @Override
    public Expert getExpert(int expertId) {
        String sql = "select * from SG_Expert where Id = ? and Status = ? ";
        Object [] params = new Object[]{expertId, this.status_one};

        return queryObject(sql, params, Expert.class, new Expert());
    }

    @Override
    public ExpertCourse getExpertCourse(int courseId) {
        String sql = "select * from SG_ExpertCourse where Id = ? and Status = ? ";
        Object [] params = new Object[]{courseId, this.status_one};
        ExpertCourse entity = queryObject(sql, params, ExpertCourse.class, new ExpertCourse());
        int eid = (int) entity.getExpertId();
        entity.setExpert(this.getExpert(eid));

        return entity;
    }

    @Override
    public List<ExpertCourse> getExpertCourses() {
        String sql = "select Id,ExpertId,Title,Cover,Content,`Desc`,Subhead,Price,StartTime,EndTime,IsClose,Mins,Count,Status,AddTime from (select * from SG_ExpertCourse where Status = ? order by AddTime desc) as ExpertCourse limit 0,3 ";
        Object [] params = new Object[]{this.status_one};
        List<ExpertCourse> courses = queryObjectList(sql, params, ExpertCourse.class);
        for (ExpertCourse course : courses){
            int eid = (int) course.getExpertId();
            course.setExpert(this.getExpert(eid));
        }
        return courses;
    }

    @Override
    public ExpertCoursePage getExpertCoursesPage(int expertId,int start) {
        ExpertCoursePage page = new ExpertCoursePage();
        int end = start + this.page;
        String limit = start + "," + end;
        String sql = "select Id,ExpertId,Title,Cover,Content,`Desc`,Subhead,Price,StartTime,EndTime,IsClose,Mins,Count,Status,AddTime from (select * from SG_ExpertCourse where Status = ? ";
        if (expertId > 0) {
            sql = sql + " and ExpertId = " + expertId;
        }
        sql = sql + " order by AddTime desc) as ExpertCourse limit " + limit;
        Object [] params = new Object[]{this.status_one};
        List<ExpertCourse> courses = queryObjectList(sql, params, ExpertCourse.class);
        for (ExpertCourse course : courses){
            int eid = (int) course.getExpertId();
            course.setExpert(this.getExpert(eid));
        }
        page.setList(courses);
        if(courses.size() < this.page) {
            page.setNextIndex(0);
        }else{
            page.setNextIndex(end);
        }
        page.setTotalCount(courses.size());

        return page;
    }

    @Override
    public ExpertQuestion getExpertQuestion(int questionId, int status) {
        String sql = "select * from SG_ExpertQuestion where Id = ? and Status = ? ";
        Object [] params = new Object[]{questionId, status};

        ExpertQuestion question = queryObject(sql, params, ExpertQuestion.class, new ExpertQuestion());

        int eid = (int) question.getExpertId();
        question.setExpert(this.getExpert(eid));
        question.setCount(this.getHearNumber(question.getId()));
        question.setPrice(this.getExpertCourse((int)question.getCourseId()).getPrice());
        boolean isValue = StringUtils.isNotEmpty(question.getAnswer());
        question.setAnswerValue(isValue);

        log.info("========>"+ question.getAnswer());
        return question;
    }

    @Override
    public ExpertHear getExpertHear(int questionId, int userId) {
        String sql = "select * from SG_ExpertHear where QuestionId = ? and UserId = ? and (Status = ? or Status = ?) ";
        Object [] params = new Object[]{questionId, userId, this.status_one, this.status_two};

        return queryObject(sql, params, ExpertHear.class, new ExpertHear());
    }

    @Override
    public List<ExpertQuestion> getExpertQuestions(int courseId) {
        String sql = "select Id,ExpertId,CourseId,UserId,Content,Mins,Status,AddTime from (select * from SG_ExpertQuestion where Status = ? and Answer is not null and Answer <> '' ";
        if (courseId > 0){
            sql = sql + " and CourseId = " + courseId;
        }
        sql = sql + " order by AddTime desc) as ExpertQuestion limit 0,3 ";
        Object [] params = new Object[]{this.status_three};
        List<ExpertQuestion> questions = queryObjectList(sql, params, ExpertQuestion.class);
        for (ExpertQuestion question : questions){
            int eid = (int) question.getExpertId();
            question.setExpert(this.getExpert(eid));
            question.setCount(this.getHearNumber(question.getId()));
            question.setPrice(this.getExpertCourse((int)question.getCourseId()).getPrice());
            boolean isValue = StringUtils.isNotEmpty(question.getAnswer());
            question.setAnswerValue(isValue);
        }
        return questions;
    }

    @Override
    public ExpertQuestionPage getExpertQuestionsPage(int courseId, int start) {
        ExpertQuestionPage page = new ExpertQuestionPage();
        int end = start + this.page;
        String limit = start + "," + end;
        String sql = "select Id,ExpertId,CourseId,UserId,Content,MIns,Status,AddTime from (select * from SG_ExpertQuestion where Status = ? and Answer is not null and Answer <> '' ";
        if (courseId > 0){
            sql = sql + " and CourseId = " + courseId;
        }
        sql = sql + " order by AddTime desc) as ExpertQuestion limit "+limit;

        Object [] params = new Object[]{this.status_three};
        List<ExpertQuestion> questions = queryObjectList(sql, params, ExpertQuestion.class);
        for (ExpertQuestion question : questions){
            int eid = (int) question.getExpertId();
            question.setExpert(this.getExpert(eid));
            question.setCount(this.getHearNumber(question.getId()));
            question.setPrice(this.getExpertCourse((int)question.getCourseId()).getPrice());
            boolean isValue = StringUtils.isNotEmpty(question.getAnswer());
            question.setAnswerValue(isValue);
        }

        page.setList(questions);
        if(questions.size() < this.page) {
            page.setNextIndex(0);
        }else{
            page.setNextIndex(end);
        }
        page.setTotalCount(questions.size());

        return page;
    }

    @Override
    public List<ExpertBanner> getExpertBanners() {
        String sql = "select Id,Cover,Action,Weight from SG_ExpertBanner where Status = ? order by AddTime desc ";
        Object [] params = new Object[]{this.status_one};
        return queryObjectList(sql, params, ExpertBanner.class);
    }

    private int getHearNumber(int qid){
        String sql = "select count(1) from SG_ExpertHear where QuestionId = ? and Status = ? ";
        Object [] params = new Object[]{qid, this.status_one};
        return queryInt(sql, params);

    }

    /**
     * 更新答案信息
     * @param qid
     * @param answer
     * @param mins
     * @return
     */
    @Override
    public int updateQuestionAnswer(long qid, String answer, int mins) {
        String sql = "UPDATE SG_ExpertQuestion SET Answer = ?, Mins = ?, Status = ? WHERE Id = ? ";
        int updateCount = singleUpdate(sql, new Object[] {answer, mins, this.status_three, qid });

        if (updateCount != 1) throw new RuntimeException("fail to update question , question id: " + qid);

        log.info(">>>>>> 答案update "+ updateCount+","+qid);
        ExpertQuestion question = this.getExpertQuestion((int)qid, this.status_three);//获取问题对象
        ExpertOrder order = this.getExpertOrderAnswer((int)question.getUserId(),(int)question.getCourseId(),question.getId(),this.status_three);//获取提问者的订单信息
        log.info(">>>>>> 获取order对象 "+ order.getPrice().doubleValue() +","+order.getId());
        BigDecimal sPrice = new BigDecimal(order.getPrice().doubleValue());
        Expert expert = this.getExpert((int)question.getExpertId());//获取专家对象
        log.info(">>>>>> update user asset "+ expert.getUserId() + "," + sPrice);
        updateUserAsset((int)expert.getUserId(), sPrice, this.status_one);//更新回答答案者的个人固定资产信息
        log.info(">>>>>> update user asset success");
        return updateCount;
    }

    @Override
    public long insertQuestion(final int expertId, final int courseId, final int userId, final String content) {
        try {
            return (Long) execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus ts) {
                    return insert(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            String sql = "INSERT INTO SG_ExpertQuestion (ExpertId, CourseId, UserId, Content, Answer, Mins, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
                            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, expertId);
                            ps.setInt(2, courseId);
                            ps.setInt(3, userId);
                            ps.setString(4, content);
                            ps.setString(5, "");
                            ps.setInt(6, 0);
                            ps.setInt(7, 2);

                            return ps;
                        }
                    }).getKey().longValue();
                }
            });
        } catch (MomiaErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("insertQuestion exception: {}/{}", courseId, expertId,userId,content, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public long insertHear(final int questionId, final int userId) {
        try {
            return (Long) execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus ts) {
                    return insert(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            String sql = "INSERT INTO SG_ExpertHear (QuestionId, UserId, Status, AddTime) VALUES (?, ?, ?, NOW())";
                            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, questionId);
                            ps.setInt(2, userId);
                            ps.setInt(3, 2);

                            return ps;
                        }
                    }).getKey().longValue();
                }
            });
        } catch (MomiaErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("insertHear  exception: {}/{}", questionId,userId, e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public long insertOrder(final int questionId, final int courseId, final int userId, final int hearId, final int type, final BigDecimal price, final String mobile) {
        try {
            return (Long) execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus ts) {
                    return insert(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            String sql = "INSERT INTO SG_ExpertOrder (UserId, CourseId, QuestionId, HearId, Type, Price, Mobile, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, userId);
                            ps.setInt(2, courseId);
                            ps.setInt(3, questionId);
                            ps.setInt(4, hearId);
                            ps.setInt(5, type);
                            ps.setBigDecimal(6, price);
                            ps.setString(7, mobile);
                            ps.setInt(8, 2);

                            return ps;
                        }
                    }).getKey().longValue();
                }
            });
        } catch (MomiaErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("insertOrder exception: {}/{}", courseId, questionId,userId,type, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExpertOrder getExpertOrder(int orderId, int status) {
        String sql = "select * from SG_ExpertOrder where Id = ? and Status = ? ";
        Object [] params = new Object[]{orderId, status};

        return queryObject(sql, params, ExpertOrder.class, new ExpertOrder());
    }

    @Override
    public ExpertOrder getExpertOrderById(int userId, int cid, int qid, int hid, int status) {
        String sql = "select * from SG_ExpertOrder where UserId = ? and CourseId = ? and QuestionId = ? and HearId = ? and Status = ? ";
        Object [] params = new Object[]{userId, cid, qid, hid, status};

        return queryObject(sql, params, ExpertOrder.class, new ExpertOrder());
    }

    /**
     * 回答答案时获取订单信息
     * @param userId
     * @param cid
     * @param qid
     * @param status
     * @return
     */
    private ExpertOrder getExpertOrderAnswer(int userId, int cid, int qid, int status) {
        String sql = "select * from SG_ExpertOrder where UserId = ? and CourseId = ? and QuestionId = ? and Status = ? ";
        Object [] params = new Object[]{userId, cid, qid, status};

        return queryObject(sql, params, ExpertOrder.class, new ExpertOrder());
    }

    @Override
    public boolean pay(final WdPayment payment) {
        final  ExpertOrder order = this.getExpertOrder((int)payment.getOrderId(),this.status_two);
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    payOrder(payment.getOrderId());
                    logPayment(payment);
                    if (order.getType() == 1){
                        updateQuestion(order.getQuestionId());
                    }else{
                        updateHear(order.getHearId());
                        sharePay(order);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("fail to pay wd order: {}", payment.getOrderId(), e);
            return false;
        }

        return true;
    }

    private void payOrder(long orderId) {
        String sql = "UPDATE SG_ExpertOrder SET Status=? WHERE Id=? AND Status=?";
        int updateCount = singleUpdate(sql, new Object[] { ExpertOrder.Status.PAYED, orderId, ExpertOrder.Status.PRE_PAYED });

        if (updateCount != 1) throw new RuntimeException("fail to pay wd order, order id: " + orderId);
    }

    private void updateQuestion(long qid) {
        String sql = "UPDATE SG_ExpertQuestion SET Status=? WHERE Id=? AND Status=?";
        int updateCount = singleUpdate(sql, new Object[] { this.status_one, qid, this.status_two });

        if (updateCount != 1) throw new RuntimeException("fail to update question , question id: " + qid);
    }

    private void updateHear(long hid) {
        String sql = "UPDATE SG_ExpertHear SET Status=? WHERE Id=? AND Status=?";
        int updateCount = singleUpdate(sql, new Object[] { this.status_one, hid, this.status_two });

        if (updateCount != 1) throw new RuntimeException("fail to update hear , hear id: " + hid);
    }

    private void logPayment(final WdPayment payment) {
        String sql = "INSERT INTO SG_ExpertPayment(OrderId, Payer, FinishTime, PayType, TradeNo, Fee, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
        int updateCount = singleUpdate(sql, new Object[] { payment.getOrderId(), payment.getPayer(), payment.getFinishTime(), payment.getPayType(), payment.getTradeNo(), payment.getFee() });

        if (updateCount != 1) throw new RuntimeException("fail to log payment for wd order : " + payment.getOrderId());
    }

    /**
     * 验证用户个人资产是否能付费
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public boolean isUserAsset(int userId,int orderId) {
        log.info("isUserAsset order id >>>>"+ orderId);
        ExpertOrder order = this.getExpertOrder(orderId,this.status_two);
        BigDecimal price = order.getPrice();
        UserAsset asset =  this.getUserAsset(userId);
        if (!asset.exists()){
            insertUserAsset(userId,new BigDecimal(0));
            return false;
        }else{
            BigDecimal number = asset.getNumber();
            if (price.compareTo(number) <= 0){
                return true;
            }
        }

        return false;
    }

    /**
     * 获取用户个人固定资产
     * @param userId
     * @return
     */
    @Override
    public UserAsset getUserAsset(int userId){

        String sql = "select * from SG_UserAsset where UserId = ? and Status = ? ";
        Object [] params = new Object[]{userId, this.status_one};

        return queryObject(sql, params, UserAsset.class, new UserAsset());
    }

    /**
     * 偷听分钱
     * @param order
     */
    private void sharePay(ExpertOrder order){
        BigDecimal sPrice = new BigDecimal(order.getPrice().divide(new BigDecimal(2)).doubleValue());
        ExpertQuestion question = this.getExpertQuestion((int)order.getQuestionId(),this.status_three);
        Expert expert = this.getExpert((int)question.getExpertId());
        updateUserAsset((int)question.getUserId(), getBigDecimalBy2(sPrice), this.status_one);
        updateUserAsset((int)expert.getUserId(), getBigDecimalBy2(sPrice), this.status_one);
    }

    /**
     * 更新个人固定资产信息
     * @param userId
     * @param number
     * @param type
     */
    @Override
    public void updateUserAsset(int userId,BigDecimal number,int type) {

        UserAsset asset =  this.getUserAsset(userId);
        if (!asset.exists()){
            insertUserAsset(userId,new BigDecimal(0));
            asset =  this.getUserAsset(userId);
        }

        if (type == 1) {
            number = new BigDecimal(asset.getNumber().add(number).doubleValue());//相加
        }else{
            number = new BigDecimal(asset.getNumber().subtract(number).doubleValue());//相减
        }

        String sql = "UPDATE SG_UserAsset SET Number = ? WHERE UserId=? AND Status=?";
        int updateCount = singleUpdate(sql, new Object[] { getBigDecimalBy2(number) , userId , this.status_one });

        if (updateCount != 1) throw new RuntimeException("fail to log update for user id: " + userId);

        insertAssetVary(asset.getId(), number, this.status_one);
    }

    /**
     * 增加个人固定资产信息
     * @param userId
     * @param number
     */
    @Override
    public void insertUserAsset(int userId, BigDecimal number){
        String sql = "INSERT INTO SG_UserAsset(UserId, Number, AddTime) VALUES( ?, ?, NOW())";
        int updateCount = singleUpdate(sql, new Object[] { userId, getBigDecimalBy2(number) });

        if (updateCount != 1) throw new RuntimeException("fail to insert user asset for user id: " + userId);
    }


    /**
     * 个人固定资产变更详情纪录
     * @param assetId
     * @param number
     * @param type
     */
    private void insertAssetVary(int assetId,BigDecimal number,int type){
        String desc = "个人固定资产变更";
        if (type > 0){
            desc = desc + "->增加数额:"+number;
        }else{
            desc = desc + "->减少数额:"+number;
        }
//        String fee = new DecimalFormat("#.00").format(number);

        String sql = "INSERT INTO SG_UserAssetVary(AssetId, VaryNumber, Type, `Desc`, AddTime) VALUES( ?, ?, ?, ?, NOW())";
        int updateCount = singleUpdate(sql, new Object[] { assetId, number.setScale(2, BigDecimal.ROUND_HALF_UP), type, desc });

        if (updateCount != 1) throw new RuntimeException("fail to insert user asset vary for asset id: " + assetId);
    }

    /**
     * 获取用户提问或回答的列表信息
     * @param userId
     * @param type
     * @param start
     * @return
     */
    @Override
    public ExpertQuestionPage getMyQuestionsPage(int userId, int type, int start) {
        ExpertQuestionPage page = new ExpertQuestionPage();
        int end = start + this.page;
        String limit = start + "," + end;
        String sql = "select Id,ExpertId,CourseId,UserId,Content,MIns,Status,AddTime from (select * from SG_ExpertQuestion where (Status = ? or Status = ? or Status = ? ) ";
        if (type > 0){
            Expert expert = this.getExpertByUserId(userId);
            sql = sql + " and ExpertId = " + expert.getId();
        }else{
            sql = sql + " and UserId = " + userId;
        }

        sql = sql + " order by AddTime desc) as ExpertQuestion limit "+limit;

        Object [] params = new Object[]{this.status_one,this.status_three,4};
        List<ExpertQuestion> questions = queryObjectList(sql, params, ExpertQuestion.class);
        for (ExpertQuestion question : questions){
            int eid = (int) question.getExpertId();
            question.setExpert(this.getExpert(eid));
            question.setCount(this.getHearNumber(question.getId()));
            question.setPrice(this.getExpertCourse((int)question.getCourseId()).getPrice());
            boolean isValue = StringUtils.isNotEmpty(question.getAnswer());
            question.setAnswerValue(isValue);
        }

        page.setList(questions);
        if(questions.size() < this.page) {
            page.setNextIndex(0);
        }else{
            page.setNextIndex(end);
        }
        page.setTotalCount(questions.size());

        return page;
    }

    /**
     * 获取答案为空的问题列表
     * @param userId
     * @param start
     * @return
     */
    @Override
    public ExpertQuestionPage getMyAnswerNullPage(int userId, int start) {
        ExpertQuestionPage page = new ExpertQuestionPage();
        int end = start + this.page;
        String limit = start + "," + end;
        String sql = "select Id,ExpertId,CourseId,UserId,Content,MIns,Status,AddTime from (select * from SG_ExpertQuestion where Status = ? ";
        Expert expert = this.getExpertByUserId(userId);
        sql = sql + " and ExpertId = " + expert.getId();
        sql = sql + " order by AddTime desc) as ExpertQuestion limit "+limit;

        Object [] params = new Object[]{this.status_one};
        List<ExpertQuestion> questions = queryObjectList(sql, params, ExpertQuestion.class);
        for (ExpertQuestion question : questions){
            int eid = (int) question.getExpertId();
            question.setExpert(this.getExpert(eid));
            question.setCount(this.getHearNumber(question.getId()));
            question.setPrice(this.getExpertCourse((int)question.getCourseId()).getPrice());
            boolean isValue = StringUtils.isNotEmpty(question.getAnswer());
            question.setAnswerValue(isValue);
        }

        page.setList(questions);
        if(questions.size() < this.page) {
            page.setNextIndex(0);
        }else{
            page.setNextIndex(end);
        }
        page.setTotalCount(questions.size());

        return page;
    }

    private Expert getExpertByUserId(int userId) {
        String sql = "select * from SG_Expert where UserId = ? and Status = ? ";
        Object [] params = new Object[]{userId, this.status_one};

        return queryObject(sql, params, Expert.class, new Expert());
    }

    @Override
    public MyCentre getMyCentre(int userId) {
        MyCentre myCentre = new MyCentre();
        myCentre.setQuestionNumber(getMyQuestionsOrAnswerNumber(userId,0));
        myCentre.setAnswerNumber(getMyQuestionsOrAnswerNumber(userId,1));
        UserAsset userAsset = this.getUserAsset(userId);
        if (!userAsset.exists()){
            insertUserAsset(userId,new BigDecimal(0));
            userAsset =  this.getUserAsset(userId);
        }
        myCentre.setAssetNumber(userAsset.getNumber());

        return myCentre;
    }

    /**
     * 获取提问或回答的总数
     * @param userId
     * @param type
     * @return
     */
    public int getMyQuestionsOrAnswerNumber(int userId, int type) {
        String sql = "select * from SG_ExpertQuestion where (Status = ? or Status = ?) ";
        if (type > 0){
            Expert expert = this.getExpertByUserId(userId);
            sql = sql + " and ExpertId = " + expert.getId();
        }else{
            sql = sql + " and UserId = " + userId;
        }

        Object [] params = new Object[]{this.status_one, this.status_three};

        return queryObjectList(sql, params, ExpertQuestion.class).size();
    }

    /**
     * 更新收听微课的次数，每次总数＋1
     * @param wid
     * @return
     */
    @Override
    public int updateCourseCount(int wid){
        ExpertCourse course = this.getExpertCourse(wid);
        log.info("updateCourseCount 1>>>>"+course.getCount());
        int count = course.getCount() + 1;
        log.info("updateCourseCount 2>>>>"+count);
        String sql = "UPDATE SG_ExpertCourse SET Count = ? WHERE Id = ? ";
        int updateCount = singleUpdate(sql, new Object[] {count, wid });

        log.info("update result >>>>"+updateCount);

        if (updateCount != 1) throw new RuntimeException("fail to update course count , course id: " + wid);

        return updateCount;
    }

    private BigDecimal getBigDecimalBy2(BigDecimal number){
        return number.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
