package cn.momia.service.user.child.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.user.child.Child;
import cn.momia.service.user.child.ChildComment;
import cn.momia.service.user.child.ChildRecord;
import cn.momia.service.user.child.ChildService;
import cn.momia.service.user.child.ChildTag;
import com.google.common.base.Splitter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildServiceImpl extends AbstractService implements ChildService {
    private List<ChildTag> tagsCache = new ArrayList<ChildTag>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, Name FROM SG_ChildTag WHERE Status=1";
        tagsCache = queryObjectList(sql, ChildTag.class);
    }

    @Override
    public long add(final Child child) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_Child (UserId, Avatar, Name, Sex, Birthday, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, child.getUserId());
                ps.setString(2, child.getAvatar());
                ps.setString(3, child.getName());
                ps.setString(4, child.getSex());
                ps.setDate(5, new java.sql.Date(child.getBirthday().getTime()));

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    @Override
    public Child get(long childId) {
        List<Child> children = list(Sets.newHashSet(childId));
        return children.isEmpty() ? Child.NOT_EXIST_USER_CHILD : children.get(0);
    }

    @Override
    public List<Child> list(Collection<Long> childrenIds) {
        String sql = "SELECT Id, UserId, Avatar, Name, Sex, Birthday FROM SG_Child WHERE Id IN (" + StringUtils.join(childrenIds, ",") + ") AND Status=1";
        return listByIds(sql, childrenIds, Long.class, Child.class);
    }

    @Override
    public Map<Long, List<Child>> queryByUsers(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<Child>>();

        Map<Long, List<Child>> childrenMap = new HashMap<Long, List<Child>>();
        for (long userId : userIds) {
            childrenMap.put(userId, new ArrayList<Child>());
        }

        String sql = String.format("SELECT Id FROM SG_Child WHERE UserId IN (%s) AND Status=1", StringUtils.join(userIds, ","));
        List<Long> childIds = queryLongList(sql);
        List<Child> children = list(childIds);

        for (Child child : children) {
            childrenMap.get(child.getUserId()).add(child);
        }

        return childrenMap;
    }

    @Override
    public boolean updateAvatar(long userId, long childId, String avatar) {
        String sql = "UPDATE SG_Child SET Avatar=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { avatar, userId, childId });
    }

    @Override
    public boolean updateName(long userId, long childId, String name) {
        String sql = "UPDATE SG_Child SET Name=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { name, userId, childId });
    }

    @Override
    public boolean updateSex(long userId, long childId, String sex) {
        String sql = "UPDATE SG_Child SET Sex=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { sex, userId, childId });
    }

    @Override
    public boolean updateBirthday(long userId, long childId, Date birthday) {
        String sql = "UPDATE SG_Child SET Birthday=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { birthday, userId, childId });
    }

    @Override
    public boolean delete(long userId, long childId) {
        String sql = "UPDATE SG_Child SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { userId, childId });
    }

    @Override
    public List<ChildTag> listAllTags() {
        if (isOutOfDate()) reload();
        return tagsCache;
    }

    @Override
    public ChildRecord getRecord(long teacherUerId, long childId, long courseId, long courseSkuId) {
        final List<ChildRecord> records = new ArrayList<ChildRecord>();
        String sql = "SELECT Tags, Content FROM SG_ChildRecord WHERE TeacherUserId=? AND ChildId=? AND CourseId=? AND CourseSkuId=? AND Status=1 LIMIT 1";
        query(sql, new Object[] { teacherUerId, childId, courseId, courseSkuId }, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ChildRecord record = new ChildRecord();
                List<Integer> tags = new ArrayList<Integer>();
                for (String tagId : Splitter.on(",").trimResults().omitEmptyStrings().split(rs.getString("Tags"))) {
                    tags.add(Integer.valueOf(tagId));
                }
                record.setTags(tags);
                record.setContent(rs.getString("Content"));

                records.add(record);
            }
        });

        return records.isEmpty() ? ChildRecord.EMPTY_RECORD : records.get(0);
    }

    @Override
    public boolean record(ChildRecord childRecord) {
        long recordId = getRecordId(childRecord.getChildId(), childRecord.getCourseId(), childRecord.getCourseSkuId());
        if (recordId > 0) {
            String sql = "UPDATE SG_ChildRecord SET TeacherUserId=?, Tags=?, Content=?, Status=1 WHERE ChildId=? AND CourseId=? AND CourseSkuId=?";
            return update(sql, new Object[] { childRecord.getTeacherUserId(), StringUtils.join(childRecord.getTags(), ","), childRecord.getContent(), childRecord.getChildId(), childRecord.getCourseId(), childRecord.getCourseSkuId() });
        } else {
            String sql = "INSERT INTO SG_ChildRecord (TeacherUserId, ChildId, CourseId, CourseSkuId, Tags, Content, AddTime) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            return update(sql, new Object[] { childRecord.getTeacherUserId(), childRecord.getChildId(), childRecord.getCourseId(), childRecord.getCourseSkuId(), StringUtils.join(childRecord.getTags(), ","), childRecord.getContent() });
        }
    }

    private long getRecordId(long childId, long courseId, long courseSkuId) {
        String sql = "SELECT Id FROM SG_ChildRecord WHERE ChildId=? AND CourseId=? AND CourseSkuId=?";
        return queryLong(sql, new Object[] { childId, courseId, courseSkuId });
    }

    @Override
    public long queryCommentsCount(long childId) {
        String sql = "SELECT COUNT(1) FROM SG_ChildComment WHERE ChildId=? AND Status=1";
        return queryLong(sql, new Object[] { childId });
    }

    @Override
    public List<ChildComment> queryComments(long childId, int start, int count) {
        String sql = "SELECT Id, TeacherUserId, ChildId, CourseId, CourseSkuId, Content FROM SG_ChildComment WHERE ChildId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        return queryObjectList(sql, new Object[] { childId, start, count }, ChildComment.class);
    }

    @Override
    public boolean comment(ChildComment childComment) {
        long commentId = getCommentId(childComment.getChildId(), childComment.getCourseId(), childComment.getCourseSkuId());
        if (commentId > 0) {
            String sql = "UPDATE SG_ChildComment SET TeacherUserId=?, Content=?, Status=1 WHERE ChildId=? AND CourseId=? AND CourseSkuId=?";
            return update(sql, new Object[] { childComment.getTeacherUserId(), childComment.getContent(), childComment.getChildId(), childComment.getCourseId(), childComment.getCourseSkuId() });
        } else {
            String sql = "INSERT INTO SG_ChildComment (TeacherUserId, ChildId, CourseId, CourseSkuId, Content, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
            return update(sql, new Object[] { childComment.getTeacherUserId(), childComment.getChildId(), childComment.getCourseId(), childComment.getCourseSkuId(), childComment.getContent() });
        }
    }

    private long getCommentId(long childId, long courseId, long courseSkuId) {
        String sql = "SELECT Id FROM SG_ChildComment WHERE ChildId=? AND CourseId=? AND CourseSkuId=?";
        return queryLong(sql, new Object[] { childId, courseId, courseSkuId });
    }

    @Override
    public List<Long> queryCommentedChildIds(long courseId, long courseSkuId) {
        String sql = "SELECT ChildId FROM SG_ChildComment WHERE CourseId=? AND CourseSkuId=? AND Status=1";
        return queryLongList(sql, new Object[] { courseId, courseSkuId });
    }
}
