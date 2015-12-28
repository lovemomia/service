package cn.momia.service.teacher.impl;

import cn.momia.api.teacher.dto.ChildComment;
import cn.momia.api.teacher.dto.ChildTag;
import cn.momia.api.teacher.dto.Education;
import cn.momia.api.teacher.dto.Experience;
import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.ChildRecord;
import cn.momia.api.teacher.dto.Student;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.service.AbstractService;
import cn.momia.service.teacher.TeacherService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeacherServiceImpl extends AbstractService implements TeacherService {
    private List<ChildTag> tagsCache = new ArrayList<ChildTag>();

    @Override
    protected void doReload() {
        String sql = "SELECT Id, Name FROM SG_ChildTag WHERE Status<>0";
        tagsCache = queryObjectList(sql, ChildTag.class);
    }

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

        return teachers.isEmpty() ? Teacher.NOT_EXIST_TEACHER : teachers.get(0);
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

        String sql = "SELECT Id FROM SG_TeacherExperience WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND Status<>0 ORDER BY AddTime DESC";
        List<Integer> experienceIds = queryIntList(sql);
        List<Experience> experiences = listExperiences(experienceIds);
        for (Experience experience : experiences) {
            experiencesMap.get(experience.getUserId()).add(experience);
        }

        return experiencesMap;
    }

    private List<Experience> listExperiences(List<Integer> experienceIds) {
        if (experienceIds.isEmpty()) return new ArrayList<Experience>();

        String sql = "SELECT Id, UserId, School, Post, Time, Content FROM SG_TeacherExperience WHERE Id IN (" + StringUtils.join(experienceIds, ",") + ") AND Status<>0";
        List<Experience> experiences = queryObjectList(sql, Experience.class);
        Map<Integer, Experience> experiencesMap = new HashMap<Integer, Experience>();
        for (Experience experience : experiences) {
            experiencesMap.put(experience.getId(), experience);
        }

        List<Experience> result = new ArrayList<Experience>();
        for (int experienceId : experienceIds) {
            Experience experience = experiencesMap.get(experienceId);
            if (experience != null) result.add(experience);
        }

        return result;
    }

    private Map<Long, List<Education>> queryEducations(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<Education>>();

        Map<Long, List<Education>> educationsMap = new HashMap<Long, List<Education>>();
        for (long userId : userIds) {
            educationsMap.put(userId, new ArrayList<Education>());
        }

        String sql = "SELECT Id FROM SG_TeacherEducation WHERE UserId IN (" + StringUtils.join(userIds, ",") + ") AND Status<>0 ORDER BY AddTime DESC";
        List<Integer> educationIds = queryIntList(sql);
        List<Education> educations = listEducations(educationIds);
        for (Education education : educations) {
            educationsMap.get(education.getUserId()).add(education);
        }

        return educationsMap;
    }

    private List<Education> listEducations(List<Integer> educationIds) {
        if (educationIds.isEmpty()) return new ArrayList<Education>();

        String sql = "SELECT Id, UserId, School, Major, Level, Time FROM SG_TeacherEducation WHERE Id IN (" + StringUtils.join(educationIds, ",") + ") AND Status<>0";
        List<Education> educations = queryObjectList(sql, Education.class);
        Map<Integer, Education> educationsMap = new HashMap<Integer, Education>();
        for (Education education : educations) {
            educationsMap.put(education.getId(), education);
        }

        List<Education> result = new ArrayList<Education>();
        for (int educationId : educationIds) {
            Education education = educationsMap.get(educationId);
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
    public Material getMaterial(long userId, int materialId) {
        if (!accessable(userId, materialId)) return Material.NOT_EXIST_MATERIAL;

        Set<Integer> materialIds = Sets.newHashSet(materialId);
        List<Material> materials = listMaterials(materialIds);

        return materials.isEmpty() ? Material.NOT_EXIST_MATERIAL : materials.get(0);
    }

    private boolean accessable(long userId, int materialId) {
        String sql = "SELECT COUNT(DISTINCT B.Id) FROM SG_TeacherCourse A INNER JOIN SG_TeacherMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND B.Id=? AND A.Status<>0 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId, materialId }) > 0;
    }

    @Override
    public long queryMaterialsCount(long userId) {
        String sql = "SELECT COUNT(DISTINCT B.Id) FROM SG_TeacherCourse A INNER JOIN SG_TeacherMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND A.Status<>0 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<Material> queryMaterials(long userId, int start, int count) {
        String sql = "SELECT B.Id FROM SG_TeacherCourse A INNER JOIN SG_TeacherMaterial B ON A.CourseId=B.CourseId WHERE A.UserId=? AND A.Status<>0 AND B.Status<>0 GROUP BY B.Id LIMIT ?,?";
        List<Integer> materialIds = queryIntList(sql, new Object[] { userId, start, count });

        return listMaterials(materialIds);
    }

    @Override
    public List<Long> queryUserIdsWithoutChild(long courseId, long courseSkuId) {
        String sql = "SELECT UserId FROM SG_BookedCourse WHERE CourseId=? AND CourseSkuId=? AND Status<>0 AND ChildId=0";
        return queryLongList(sql, new Object[] { courseId, courseSkuId });
    }

    @Override
    public List<Student> queryAllStudents(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id FROM SG_BookedCourse A INNER JOIN SG_Child B ON A.ChildId=B.Id WHERE A.CourseId=? AND A.CourseSkuId=? AND A.Status<>0 AND B.Status<>0";
        List<Long> childIds = queryLongList(sql, new Object[] { courseId, courseSkuId });

        return listStudents(childIds, courseId, courseSkuId);
    }

    private List<Student> listStudents(List<Long> childIds, long courseId, long courseSkuId) {
        if (childIds.isEmpty()) return new ArrayList<Student>();

        String sql = "SELECT B.Id, B.UserId, B.Avatar, B.Name, B.Birthday, B.Sex, A.PackageId, A.CheckIn FROM SG_BookedCourse A INNER JOIN SG_Child B ON A.ChildId=B.Id WHERE A.CourseId=? AND A.CourseSkuId=? AND A.Status<>0 AND B.Id IN (" + StringUtils.join(childIds, ",") + ") AND B.Status<>0";
        List<Student> students = queryObjectList(sql, new Object[] {courseId, courseSkuId  }, Student.class);
        Map<Long, Student> studentsMap = new HashMap<Long, Student>();
        for (Student student : students) {
            studentsMap.put(student.getId(), student);
        }

        List<Student> result = new ArrayList<Student>();
        for (long childId : childIds) {
            Student student = studentsMap.get(childId);
            if (student != null) result.add(student);
        }

        return result;
    }

    @Override
    public List<Student> queryCheckInStudents(long courseId, long courseSkuId) {
        String sql = "SELECT B.Id FROM SG_BookedCourse A INNER JOIN SG_Child B ON A.ChildId=B.Id WHERE A.CourseId=? AND A.CourseSkuId=? AND A.CheckIn>0 AND A.Status<>0 AND B.Status<>0";
        List<Long> childIds = queryLongList(sql, new Object[] { courseId, courseSkuId });

        return listStudents(childIds, courseId, courseSkuId);
    }

    @Override
    public List<Long> queryCommentedChildIds(long courseId, long courseSkuId) {
        String sql = "SELECT ChildId FROM SG_ChildComment WHERE CourseId=? AND CourseSkuId=? AND Status<>0";
        return queryLongList(sql, new Object[] { courseId, courseSkuId });
    }

    @Override
    public boolean checkin(long userId, long packageId, long courseId, long courseSkuId) {
        String sql = "UPDATE SG_BookedCourse SET CheckIn=1 WHERE UserId=? AND PackageId=? AND CourseId=? AND CourseSkuId=? AND Status<>0";
        return update(sql, new Object[] { userId, packageId, courseId, courseSkuId });
    }

    private List<Material> listMaterials(Collection<Integer> materialIds) {
        if (materialIds.isEmpty()) return new ArrayList<Material>();

        String sql = "SELECT A.Id, B.Cover, B.Title, C.Title AS Subject, A.Content FROM SG_TeacherMaterial A INNER JOIN SG_Course B ON A.CourseId=B.Id INNER JOIN SG_Subject C ON B.SubjectId=C.Id WHERE A.Id IN (" + StringUtils.join(materialIds, ",") + ") AND A.Status<>0 AND B.Status<>0 AND C.Status<>0";
        List<Material> materials = queryObjectList(sql, Material.class);

        Map<Integer, Material> materialsMap = new HashMap<Integer, Material>();
        for (Material material : materials) {
            materialsMap.put(material.getId(), material);
        }

        List<Material> result = new ArrayList<Material>();
        for (int materialId : materialIds) {
            Material material = materialsMap.get(materialId);
            if (material != null) result.add(material);
        }

        return result;
    }

    @Override
    public long queryChildCommentsCount(long childId) {
        String sql = "SELECT COUNT(1) FROM SG_ChildComment WHERE ChildId=? AND Status<>0";
        return queryLong(sql, new Object[] { childId });
    }

    @Override
    public List<ChildComment> queryChildComments(long childId, int start, int count) {
        String sql = "SELECT C.StartTime AS Date, B.Title, A.Content, D.NickName AS Teacher " +
                "FROM SG_ChildComment A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.CourseSkuId=C.Id " +
                "INNER JOIN SG_User D ON A.TeacherUserId=D.Id " +
                "WHERE A.ChildId=? AND A.Status<>0 AND B.Status<>0 AND C.Status<>0 AND D.Status<>0 " +
                "ORDER BY C.StartTime DESC, A.AddTime DESC " +
                "LIMIT ?,?";
        return queryObjectList(sql, new Object[] { childId, start, count }, ChildComment.class);
    }

    @Override
    public List<ChildTag> listTags() {
        if (isOutOfDate()) reload();
        return tagsCache;
    }

    @Override
    public ChildRecord getRecord(long teacherUerId, long childId, long courseId, long courseSkuId) {
        final List<ChildRecord> records = new ArrayList<ChildRecord>();
        String sql = "SELECT Tags, Content FROM SG_ChildRecord WHERE TeacherUserId=? AND ChildId=? AND CourseId=? AND CourseSkuId=? AND Status<>0 LIMIT 1";
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
    public boolean comment(ChildComment childComment) {
        long commentId = getCommentId(childComment.getChildId(), childComment.getCourseId(), childComment.getCourseSkuId());
        if (commentId > 0) {
            String sql = "UPDATE SG_ChildComment SET TeacherUserId=?, Content=?, Status=1 WHERE ChildId=? AND CourseId=? AND CourseSkuId=?";
            return update(sql, new Object[] { childComment.getTeacherUserId(), childComment.getContent(), childComment.getChildId(), childComment.getCourseId(), childComment.getCourseSkuId() });
        } else {
            String sql = "INSERT INTO SG_ChildComment (TeacherUserUserId, ChildId, CourseId, CourseSkuId, Content, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
            return update(sql, new Object[] { childComment.getTeacherUserId(), childComment.getChildId(), childComment.getCourseId(), childComment.getCourseSkuId(), childComment.getContent() });
        }
    }

    private long getCommentId(long childId, long courseId, long courseSkuId) {
        String sql = "SELECT Id FROM SG_ChildComment WHERE ChildId=? AND CourseId=? AND CourseSkuId=?";
        return queryLong(sql, new Object[] { childId, courseId, courseSkuId });
    }

    @Override
    public boolean addExperience(long userId, Experience experience) {
        String sql = "INSERT INTO SG_TeacherExperience (UserId, School, Post, Time, Content, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        return update(sql, new Object[] { userId, experience.getSchool(), experience.getPost(), experience.getTime(), experience.getContent() });
    }

    @Override
    public Experience getExperience(long userId, int experienceId) {
        String sql = "SELECT Id FROM SG_TeacherExperience WHERE UserId=? AND Id=? AND Status<>0";
        List<Integer> experienceIds = queryIntList(sql, new Object[] { userId, experienceId });
        List<Experience> experiences = listExperiences(experienceIds);

        return experiences.isEmpty() ? Experience.NOT_EXIST_EXPERIENCE : experiences.get(0);
    }

    @Override
    public boolean deleteExperience(long userId, int experienceId) {
        String sql = "UPDATE SG_TeacherExperience SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { userId, experienceId });
    }

    @Override
    public boolean addEducation(long userId, Education education) {
        String sql = "INSERT INTO SG_TeacherEducation (UserId, School, Major, Level, Time, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        return update(sql, new Object[] { userId, education.getSchool(), education.getMajor(), education.getLevel(), education.getTime() });
    }

    @Override
    public Education getEducation(long userId, int educationId) {
        String sql = "SELECT Id FROM SG_TeacherEducation WHERE UserId=? AND Id=? AND Status<>0";
        List<Integer> educationIds = queryIntList(sql, new Object[] { userId, educationId });
        List<Education> educations = listEducations(educationIds);

        return educations.isEmpty() ? Education.NOT_EXIST_EDUCATION : educations.get(0);
    }

    @Override
    public boolean deleteEducation(long userId, int educationId) {
        String sql = "UPDATE SG_TeacherEducation SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { userId, educationId });
    }
}
