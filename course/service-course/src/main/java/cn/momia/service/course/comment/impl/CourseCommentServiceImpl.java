package cn.momia.service.course.comment.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.course.comment.CourseComment;
import cn.momia.service.course.comment.CourseCommentService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseCommentServiceImpl extends AbstractService implements CourseCommentService {
    @Override
    public boolean isCommented(long userId, long bookingId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseComment WHERE UserId=? AND BookingId=?";
        return queryInt(sql, new Object[] { userId, bookingId }) > 0;
    }

    @Override
    public boolean comment(CourseComment comment) {
        long commentId = addComment(comment);
        if (commentId <= 0) return false;

        if (comment.getImgs() != null && !comment.getImgs().isEmpty()) addCommentImgs(commentId, comment.getImgs());

        return true;
    }

    private long addComment(final CourseComment comment) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_CourseComment(UserId, BookingId, CourseId, Star, Teacher, Environment, Content, AddTime) VALUES(?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, comment.getUserId());
                ps.setLong(2, comment.getBookingId());
                ps.setLong(3, comment.getCourseId());
                ps.setInt(4, comment.getStar());
                ps.setInt(5, comment.getTeacher());
                ps.setInt(6, comment.getEnvironment());
                ps.setString(7, comment.getContent());

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    private void addCommentImgs(long commentId, List<String> imgs) {
        List<Object[]> params = new ArrayList<Object[]>();
        for (String img : imgs) {
            params.add(new Object[] { commentId, img });
        }
        String sql = "INSERT INTO SG_CourseCommentImg (CommentId, Url, AddTime) VALUES (?, ?, NOW())";
        batchUpdate(sql, params);
    }

    @Override
    public long queryCommentCountByCourse(long courseId) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        return queryCommentCountByCourses(courseIds);
    }

    private long queryCommentCountByCourses(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return 0;

        String sql = "SELECT COUNT(1) FROM SG_CourseComment WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0";
        return queryInt(sql, null);
    }

    @Override
    public List<CourseComment> queryCommentsByCourse(long courseId, int start, int count) {
        String sql = "SELECT Id FROM SG_CourseComment WHERE CourseId=? AND Status<>0 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> commentIds = queryLongList(sql, new Object[] { courseId, start, count });

        return listComments(commentIds);
    }

    private List<CourseComment> queryCommentsByCourses(Collection<Long> courseIds, int start, int count) {
        if (courseIds.isEmpty()) return new ArrayList<CourseComment>();

        String sql = "SELECT Id FROM SG_CourseComment WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status<>0 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> commentIds = queryLongList(sql, new Object[] { start, count });

        return listComments(commentIds);
    }

    private List<CourseComment> listComments(List<Long> commentIds) {
        if (commentIds.isEmpty()) return new ArrayList<CourseComment>();

        String sql = "SELECT Id, UserId, CourseId, Star, Teacher, Environment, Content, AddTime FROM SG_CourseComment WHERE Id IN (" + StringUtils.join(commentIds, ",") + ") AND Status<>0";
        List<CourseComment> comments = queryObjectList(sql, CourseComment.class);

        Map<Long, List<String>> imgsMap = queryCommentImgs(commentIds);

        Map<Long, CourseComment> commentsMap = new HashMap<Long, CourseComment>();
        for (CourseComment comment : comments) {
            comment.setImgs(imgsMap.get(comment.getId()));
            commentsMap.put(comment.getId(), comment);
        }

        List<CourseComment> result = new ArrayList<CourseComment>();
        for (long commentId : commentIds) {
            CourseComment comment = commentsMap.get(commentId);
            if (comment != null) result.add(comment);
        }

        return result;
    }

    private Map<Long, List<String>> queryCommentImgs(List<Long> commentIds) {
        if (commentIds.isEmpty()) return new HashMap<Long, List<String>>();

        final Map<Long, List<String>> imgsMap = new HashMap<Long, List<String>>();
        for (long commentId : commentIds) {
            imgsMap.put(commentId, new ArrayList<String>());
        }

        String sql = "SELECT CommentId, Url FROM SG_CourseCommentImg WHERE CommentId IN (" + StringUtils.join(commentIds, ",") + ") AND Status<>0";
        query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long commentId = rs.getLong("CommentId");
                String url = rs.getString("Url");
                imgsMap.get(commentId).add(url);
            }
        });

        return imgsMap;
    }

    @Override
    public long queryCommentCountBySubject(long subjectId) {
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId=? AND Status<>0";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId });

        return queryCommentCountByCourses(courseIds);
    }

    @Override
    public List<CourseComment> queryCommentsBySubject(long subjectId, int start, int count) {
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId=? AND Status<>0";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId });

        return queryCommentsByCourses(courseIds, start, count);
    }

    @Override
    public List<Long> queryCommentedBookingIds(long userId, Collection<Long> bookingIds) {
        if (userId <= 0 || bookingIds.isEmpty()) return new ArrayList<Long>();

        String sql = "SELECT BookingId FROM SG_CourseComment WHERE UserId=? AND BookingId IN (" + StringUtils.join(bookingIds, ",") + ")";
        return queryLongList(sql, new Object[] { userId });
    }
}
