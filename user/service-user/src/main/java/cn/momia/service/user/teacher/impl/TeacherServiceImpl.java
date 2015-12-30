package cn.momia.service.user.teacher.impl;

import cn.momia.api.user.dto.Teacher;
import cn.momia.api.user.dto.TeacherEducation;
import cn.momia.api.user.dto.TeacherExperience;
import cn.momia.api.user.dto.TeacherStatus;
import cn.momia.common.service.AbstractService;
import cn.momia.service.user.teacher.TeacherService;
import com.google.common.collect.Sets;
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
    public Teacher getByUser(long userId) {
        String sql = "SELECT Id FROM SG_Teacher WHERE UserId=? AND Status<>0";
        List<Integer> teacherIds = queryIntList(sql, new Object[] { userId });
        List<Teacher> teachers = listTeachers(teacherIds);

        if (!teachers.isEmpty()) return teachers.get(0);

        Teacher teacher = new Teacher();
        teacher.setExperiences(queryExperiences(Sets.newHashSet(userId)).get(userId));
        teacher.setEducations(queryEducations(Sets.newHashSet(userId)).get(userId));

        return teacher;
    }

    private List<Teacher> listTeachers(Collection<Integer> teacherIds) {
        if (teacherIds.isEmpty()) return new ArrayList<Teacher>();

        String sql = "SELECT Id, UserId, Pic, Name, IdNo, Gender AS Sex, Birthday, Address FROM SG_Teacher WHERE Id IN (" + StringUtils.join(teacherIds, ",") + ") AND Status<>0";
        List<Teacher> teachers = queryObjectList(sql, Teacher.class);

        Map<Integer, Teacher> teachersMap = new HashMap<Integer, Teacher>();
        Set<Long> userIds = new HashSet<Long>();
        for (Teacher teacher : teachers) {
            teachersMap.put(teacher.getId(), teacher);
            userIds.add(teacher.getUserId());
        }

        Map<Long, List<TeacherExperience>> experiencesMap = queryExperiences(userIds);
        Map<Long, List<TeacherEducation>> educationsMap = queryEducations(userIds);

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

    private Map<Long, List<TeacherExperience>> queryExperiences(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<TeacherExperience>>();

        Map<Long, List<TeacherExperience>> experiencesMap = new HashMap<Long, List<TeacherExperience>>();
        for (long userId : userIds) {
            experiencesMap.put(userId, new ArrayList<TeacherExperience>());
        }

        String sql = "SELECT Id FROM SG_TeacherExperience WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND Status<>0 ORDER BY AddTime DESC";
        List<Integer> experienceIds = queryIntList(sql);
        List<TeacherExperience> experiences = listExperiences(experienceIds);
        for (TeacherExperience experience : experiences) {
            experiencesMap.get(experience.getUserId()).add(experience);
        }

        return experiencesMap;
    }

    private List<TeacherExperience> listExperiences(List<Integer> experienceIds) {
        if (experienceIds.isEmpty()) return new ArrayList<TeacherExperience>();

        String sql = "SELECT Id, UserId, School, Post, Time, Content FROM SG_TeacherExperience WHERE Id IN (" + StringUtils.join(experienceIds, ",") + ") AND Status<>0";
        List<TeacherExperience> experiences = queryObjectList(sql, TeacherExperience.class);
        Map<Integer, TeacherExperience> experiencesMap = new HashMap<Integer, TeacherExperience>();
        for (TeacherExperience experience : experiences) {
            experiencesMap.put(experience.getId(), experience);
        }

        List<TeacherExperience> result = new ArrayList<TeacherExperience>();
        for (int experienceId : experienceIds) {
            TeacherExperience experience = experiencesMap.get(experienceId);
            if (experience != null) result.add(experience);
        }

        return result;
    }

    private Map<Long, List<TeacherEducation>> queryEducations(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<TeacherEducation>>();

        Map<Long, List<TeacherEducation>> educationsMap = new HashMap<Long, List<TeacherEducation>>();
        for (long userId : userIds) {
            educationsMap.put(userId, new ArrayList<TeacherEducation>());
        }

        String sql = "SELECT Id FROM SG_TeacherEducation WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND Status<>0 ORDER BY AddTime DESC";
        List<Integer> educationIds = queryIntList(sql);
        List<TeacherEducation> educations = listEducations(educationIds);
        for (TeacherEducation education : educations) {
            educationsMap.get(education.getUserId()).add(education);
        }

        return educationsMap;
    }

    private List<TeacherEducation> listEducations(List<Integer> educationIds) {
        if (educationIds.isEmpty()) return new ArrayList<TeacherEducation>();

        String sql = "SELECT Id, UserId, School, Major, Level, Time FROM SG_TeacherEducation WHERE Id IN (" + StringUtils.join(educationIds, ",") + ") AND Status<>0";
        List<TeacherEducation> educations = queryObjectList(sql, TeacherEducation.class);
        Map<Integer, TeacherEducation> educationsMap = new HashMap<Integer, TeacherEducation>();
        for (TeacherEducation education : educations) {
            educationsMap.put(education.getId(), education);
        }

        List<TeacherEducation> result = new ArrayList<TeacherEducation>();
        for (int educationId : educationIds) {
            TeacherEducation education = educationsMap.get(educationId);
            if (education != null) result.add(education);
        }

        return result;
    }

    @Override
    public long add(final Teacher teacher) {
        int teacherId = getIdByUser(teacher.getUserId());
        if (teacherId > 0) {
            String sql = "UPDATE SG_Teacher SET Pic=?, Name=?, IdNo=?, Gender=?, Birthday=?, Address=?, Status=? WHERE Id=? AND UserId=?";
            update(sql, new Object[] { teacher.getPic(), teacher.getName(), teacher.getIdNo(), teacher.getSex(), teacher.getBirthday(), teacher.getAddress(), TeacherStatus.Status.NOT_CHECKED, teacherId, teacher.getUserId() });
        } else {
            KeyHolder keyHolder = insert(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO SG_Teacher(UserId, Pic, Name, IdNo, Gender, Birthday, Address, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, teacher.getUserId());
                    String pic = teacher.getPic();
                    if (pic.startsWith("http://")) {
                        pic = pic.substring("http://".length());
                        int index = pic.indexOf("/");
                        if (index == -1) pic = "";
                        else pic = pic.substring(index);
                    }
                    ps.setString(2, pic);
                    ps.setString(3, teacher.getName());
                    ps.setString(4, teacher.getIdNo());
                    ps.setString(5, teacher.getSex());
                    Date birthday = teacher.getBirthday();
                    ps.setDate(6, birthday == null ? null : new java.sql.Date(birthday.getTime()));
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
    public boolean addExperience(long userId, TeacherExperience experience) {
        String sql = "INSERT INTO SG_TeacherExperience (UserId, School, Post, Time, Content, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        return update(sql, new Object[] { userId, experience.getSchool(), experience.getPost(), experience.getTime(), experience.getContent() });
    }

    @Override
    public TeacherExperience getExperience(long userId, int experienceId) {
        String sql = "SELECT Id FROM SG_TeacherExperience WHERE UserId=? AND Id=? AND Status<>0";
        List<Integer> experienceIds = queryIntList(sql, new Object[] { userId, experienceId });
        List<TeacherExperience> experiences = listExperiences(experienceIds);

        return experiences.isEmpty() ? TeacherExperience.NOT_EXIST_TEACHER_EXPERIENCE : experiences.get(0);
    }

    @Override
    public boolean deleteExperience(long userId, int experienceId) {
        String sql = "UPDATE SG_TeacherExperience SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { userId, experienceId });
    }

    @Override
    public boolean addEducation(long userId, TeacherEducation education) {
        String sql = "INSERT INTO SG_TeacherEducation (UserId, School, Major, Level, Time, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        return update(sql, new Object[] { userId, education.getSchool(), education.getMajor(), education.getLevel(), education.getTime() });
    }

    @Override
    public TeacherEducation getEducation(long userId, int educationId) {
        String sql = "SELECT Id FROM SG_TeacherEducation WHERE UserId=? AND Id=? AND Status<>0";
        List<Integer> educationIds = queryIntList(sql, new Object[] { userId, educationId });
        List<TeacherEducation> educations = listEducations(educationIds);

        return educations.isEmpty() ? TeacherEducation.NOT_EXIST_TEACHER_EDUCATION : educations.get(0);
    }

    @Override
    public boolean deleteEducation(long userId, int educationId) {
        String sql = "UPDATE SG_TeacherEducation SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { userId, educationId });
    }
}
