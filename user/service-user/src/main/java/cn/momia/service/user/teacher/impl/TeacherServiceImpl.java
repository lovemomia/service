package cn.momia.service.user.teacher.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.user.teacher.Teacher;
import cn.momia.service.user.teacher.TeacherEducation;
import cn.momia.service.user.teacher.TeacherExperience;
import cn.momia.service.user.teacher.TeacherService;
import cn.momia.service.user.teacher.TeacherStatus;
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
        List<Teacher> teachers = list(teacherIds);

        if (!teachers.isEmpty()) return teachers.get(0);

        Teacher teacher = new Teacher();
        teacher.setExperiences(queryExperiences(Sets.newHashSet(userId)).get(userId));
        teacher.setEducations(queryEducations(Sets.newHashSet(userId)).get(userId));

        return teacher;
    }

    @Override
    public List<Teacher> list(Collection<Integer> teacherIds) {
        String sql = "SELECT Id, UserId, Pic, Name, IdNo, Gender AS Sex, Birthday, Address, Experience, Education FROM SG_Teacher WHERE Id IN (%s) AND Status<>0";
        List<Teacher> teachers = listByIds(sql, teacherIds, Integer.class, Teacher.class);

        Set<Long> userIds = new HashSet<Long>();
        for (Teacher teacher : teachers) {
            userIds.add(teacher.getUserId());
        }

        Map<Long, List<TeacherExperience>> experiencesMap = queryExperiences(userIds);
        Map<Long, List<TeacherEducation>> educationsMap = queryEducations(userIds);

        for (Teacher teacher : teachers) {
            teacher.setExperiences(experiencesMap.get(teacher.getUserId()));
            teacher.setEducations(educationsMap.get(teacher.getUserId()));
        }

        return teachers;
    }

    private Map<Long, List<TeacherExperience>> queryExperiences(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<TeacherExperience>>();

        Map<Long, List<TeacherExperience>> experiencesMap = new HashMap<Long, List<TeacherExperience>>();
        for (long userId : userIds) {
            experiencesMap.put(userId, new ArrayList<TeacherExperience>());
        }

        String sql = String.format("SELECT Id FROM SG_TeacherExperience WHERE UserId IN (%s) AND Status<>0 ORDER BY Time DESC", StringUtils.join(userIds, ","));
        List<Integer> experienceIds = queryIntList(sql);
        List<TeacherExperience> experiences = listExperiences(experienceIds);
        for (TeacherExperience experience : experiences) {
            experiencesMap.get(experience.getUserId()).add(experience);
        }

        return experiencesMap;
    }

    private List<TeacherExperience> listExperiences(List<Integer> experienceIds) {
        String sql = "SELECT Id, UserId, School, Post, Time, Content FROM SG_TeacherExperience WHERE Id IN (%s) AND Status<>0";
        return listByIds(sql, experienceIds, Integer.class, TeacherExperience.class);
    }

    private Map<Long, List<TeacherEducation>> queryEducations(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<TeacherEducation>>();

        Map<Long, List<TeacherEducation>> educationsMap = new HashMap<Long, List<TeacherEducation>>();
        for (long userId : userIds) {
            educationsMap.put(userId, new ArrayList<TeacherEducation>());
        }

        String sql = String.format("SELECT Id FROM SG_TeacherEducation WHERE UserId IN (%s) AND Status<>0 ORDER BY Time DESC", StringUtils.join(userIds, ","));
        List<Integer> educationIds = queryIntList(sql);
        List<TeacherEducation> educations = listEducations(educationIds);
        for (TeacherEducation education : educations) {
            educationsMap.get(education.getUserId()).add(education);
        }

        return educationsMap;
    }

    private List<TeacherEducation> listEducations(List<Integer> educationIds) {
        String sql = "SELECT Id, UserId, School, Major, Level, Time FROM SG_TeacherEducation WHERE Id IN (%s) AND Status<>0";
        return listByIds(sql, educationIds, Integer.class, TeacherEducation.class);
    }

    @Override
    public List<Teacher> listByUser(Collection<Long> teacherUserIds) {
        if (teacherUserIds.isEmpty()) return new ArrayList<Teacher>();

        String sql = "SELECT Id FROM SG_Teacher WHERE UserId IN (" + StringUtils.join(teacherUserIds, ",") + ") AND Status<>0";
        List<Integer> teacherIds = queryIntList(sql);

        return  list(teacherIds);
    }

    @Override
    public long add(final Teacher teacher) {
        String pic = teacher.getPic();
        if (pic.startsWith("http://")) {
            pic = pic.substring("http://".length());
            int index = pic.indexOf("/");
            if (index == -1) pic = "";
            else pic = pic.substring(index);
        }
        final String teacherPic = pic;
        final int status = teacher.isCompleted() ? TeacherStatus.Status.NOT_CHECKED : TeacherStatus.Status.NOT_FINISHED;

        int teacherId = getIdByUser(teacher.getUserId());
        if (teacherId > 0) {
            String sql = "UPDATE SG_Teacher SET Pic=?, Name=?, IdNo=?, Gender=?, Birthday=?, Address=?, Status=? WHERE Id=? AND UserId=?";
            update(sql, new Object[] { teacherPic, teacher.getName(), teacher.getIdNo(), teacher.getSex(), teacher.getBirthday(), teacher.getAddress(), status, teacherId, teacher.getUserId() });
        } else {
            KeyHolder keyHolder = insert(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO SG_Teacher(UserId, Pic, Name, IdNo, Gender, Birthday, Address, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, teacher.getUserId());
                    ps.setString(2, teacherPic);
                    ps.setString(3, teacher.getName());
                    ps.setString(4, teacher.getIdNo());
                    ps.setString(5, teacher.getSex());
                    Date birthday = teacher.getBirthday();
                    ps.setDate(6, birthday == null ? null : new java.sql.Date(birthday.getTime()));
                    ps.setString(7, teacher.getAddress());
                    ps.setInt(8, status);

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
    public int addExperience(final long userId, final TeacherExperience experience) {
        if (experience.getId() <= 0) {
            return insert(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO SG_TeacherExperience (UserId, School, Post, Time, Content, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, userId);
                    ps.setString(2, experience.getSchool());
                    ps.setString(3, experience.getPost());
                    ps.setString(4, experience.getTime());
                    ps.setString(5, experience.getContent());

                    return ps;
                }
            }).getKey().intValue();
        } else {
            String sql = "UPDATE SG_TeacherExperience SET School=?, Post=?, Time=?, Content=? WHERE Id=? AND UserId=?";
            if (update(sql, new Object[] { experience.getSchool(), experience.getPost(), experience.getTime(), experience.getContent(), experience.getId(), userId })) return experience.getId();
        }

        return 0;
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
    public List<TeacherExperience> listExperiences(long userId) {
        String sql = "SELECT Id FROM SG_TeacherExperience WHERE UserId=? AND Status<>0 ORDER BY AddTime DESC";
        List<Integer> experienceIds = queryIntList(sql, new Object[] { userId });

        return listExperiences(experienceIds);
    }

    @Override
    public int addEducation(final long userId, final TeacherEducation education) {
        if (education.getId() <= 0) {
            return insert(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "INSERT INTO SG_TeacherEducation (UserId, School, Major, Level, Time, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, userId);
                    ps.setString(2, education.getSchool());
                    ps.setString(3, education.getMajor());
                    ps.setString(4, education.getLevel());
                    ps.setString(5, education.getTime());

                    return ps;
                }
            }).getKey().intValue();
        } else {
            String sql = "UPDATE SG_TeacherEducation SET School=?, Major=?, Level=?, Time=? WHERE Id=? AND UserId=?";
            if (update(sql, new Object[] { education.getSchool(), education.getMajor(), education.getLevel(), education.getTime(), education.getId(), userId })) return education.getId();
        }

        return 0;
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

    @Override
    public List<TeacherEducation> listEducations(long userId) {
        String sql = "SELECT Id FROM SG_TeacherEducation WHERE UserId=? AND Status<>0 ORDER BY AddTime DESC";
        List<Integer> educationIds = queryIntList(sql, new Object[] { userId });

        return listEducations(educationIds);
    }
}
