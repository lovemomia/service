package cn.momia.service.teacher.impl;

import cn.momia.api.teacher.dto.Education;
import cn.momia.api.teacher.dto.Experience;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.service.AbstractService;
import cn.momia.service.teacher.TeacherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeacherServiceImpl extends AbstractService implements TeacherService {
    @Override
    public TeacherStatus status(long userId) {
        String sql = "SELECT Status, Msg FROM SG_Teacher WHERE UserId=? AND Status<>0";
        return queryObject(sql, new Object[] { userId }, TeacherStatus.class, TeacherStatus.NOT_EXIST_TEACHER_STATUS);
    }

    @Override
    public long add(final Teacher teacher) {
        int teacherId = getIdByUser(teacher.getUserId());
        if (teacherId > 0) {
            String sql = "UPDATE SG_Teacher SET Pic=?, Name=?, IdNo=?, Sex=?, Birthday=?, Address=?, Status=? WHERE Id=? AND UserId=?";
            update(sql, new Object[] { teacher.getPic(), teacher.getName(), teacher.getIdNo(), teacher.getSex(), teacher.getBirthday(), teacher.getAddress(), TeacherStatus.Status.NOT_CHECKED, teacherId, teacher.getUserId() });
        } else {
            KeyHolder keyHolder = insert(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO SG_Teacher(UserId, Pic, Name, IdNo, Sex, Birthday, Address, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, teacher.getUserId());
                    ps.setString(2, teacher.getPic());
                    ps.setString(3, teacher.getName());
                    ps.setString(4, teacher.getIdNo());
                    ps.setString(5, teacher.getSex());
                    ps.setDate(6, new java.sql.Date(teacher.getBirthday().getTime()));
                    ps.setString(7, teacher.getAddress());
                    ps.setInt(8, TeacherStatus.Status.NOT_CHECKED);

                    return ps;
                }
            });

            teacherId = keyHolder.getKey().intValue();
        }

        return teacherId;
    }

    private int getIdByUser(long userId) {
        String sql = "SELECT Id FROM SG_Teacher WHERE UserId=?";
        return queryInt(sql, new Object[] { userId });
    }

    @Override
    public Teacher getByUser(long userId) {
        String sql = "SELECT Id FROM SG_Teacher WHERE UserId=? AND Status<>0";
        List<Integer> teacherIds = queryIntList(sql, new Object[] { userId });
        List<Teacher> teachers = listTeachers(teacherIds);

        return teachers.isEmpty() ? Teacher.NOT_EXIST_TEACHER : teachers.get(0);
    }

    private List<Teacher> listTeachers(Collection<Integer> teacherIds) {
        if (teacherIds.isEmpty()) return new ArrayList<Teacher>();

        String sql = "SELECT Id, UserId, Pic, Name, IdNo, Sex, Birthday, Address FROM SG_Teacher WHERE Id IN (" + StringUtils.join(teacherIds, ",") + ") AND Status<>0";
        List<Teacher> teachers = queryObjectList(sql, Teacher.class);

        Map<Integer, Teacher> teachersMap = new HashMap<Integer, Teacher>();
        Set<Long> userIds = new HashSet<Long>();
        for (Teacher teacher : teachers) {
            teachersMap.put(teacher.getId(), teacher);
            userIds.add(teacher.getUserId());
        }

        Map<Long, List<Experience>> experiencesMap = queryExperiences(userIds);
        Map<Long, List<Education>> educationsMap = queryEducations(userIds);

        List<Teacher> result = new ArrayList<Teacher>();
        for (int teacherId : teacherIds) {
            Teacher teacher = teachersMap.get(teacherId);
            if (teacher == null) continue;

            teacher.setExperiences(experiencesMap.get(teacher.getUserId()));
            teacher.setEducations(educationsMap.get(teacher.getUserId()));

            result.add(teacher);
        }

        return result;
    }

    private Map<Long, List<Experience>> queryExperiences(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<Experience>>();

        Map<Long, List<Experience>> experiencesMap = new HashMap<Long, List<Experience>>();
        for (long userId : userIds) {
            experiencesMap.put(userId, new ArrayList<Experience>());
        }

        String sql = "SELECT Id, UserId, School, Post, Time, Content FROM SG_TeacherExperience WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND Status<>0";
        List<Experience> experiences = queryObjectList(sql, Experience.class);
        for (Experience experience : experiences) {
            experiencesMap.get(experience.getUserId()).add(experience);
        }

        return experiencesMap;
    }

    private Map<Long, List<Education>> queryEducations(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<Education>>();

        Map<Long, List<Education>> educationsMap = new HashMap<Long, List<Education>>();
        for (long userId : userIds) {
            educationsMap.put(userId, new ArrayList<Education>());
        }

        String sql = "SELECT Id, UserId, School, Major, Level, Time FROM SG_TeacherEducation WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND Status<>0";
        List<Education> educations = queryObjectList(sql, Education.class);
        for (Education education : educations) {
            educationsMap.get(education.getUserId()).add(education);
        }

        return educationsMap;
    }

    @Override
    public boolean updatePic(int teacherId, String pic) {
        String sql = "UPDATE SG_Teacher SET Pic=? WHERE Id=?";
        return update(sql, new Object[] { pic, teacherId });
    }

    @Override
    public boolean updateName(int teacherId, String name) {
        String sql = "UPDATE SG_Teacher SET Name=? WHERE Id=?";
        return update(sql, new Object[] { name, teacherId });
    }

    @Override
    public boolean updateIdNo(int teacherId, String idno) {
        String sql = "UPDATE SG_Teacher SET IdNo=? WHERE Id=?";
        return update(sql, new Object[] { idno, teacherId });
    }

    @Override
    public boolean updateSex(int teacherId, String sex) {
        String sql = "UPDATE SG_Teacher SET Sex=? WHERE Id=?";
        return update(sql, new Object[] { sex, teacherId });
    }

    @Override
    public boolean updateBirthday(int teacherId, Date birthday) {
        String sql = "UPDATE SG_Teacher SET Birthday=? WHERE Id=?";
        return update(sql, new Object[] { birthday, teacherId });
    }

    @Override
    public boolean updateAddress(int teacherId, String address) {
        String sql = "UPDATE SG_Teacher SET Address=? WHERE Id=?";
        return update(sql, new Object[] { address, teacherId });
    }
}
