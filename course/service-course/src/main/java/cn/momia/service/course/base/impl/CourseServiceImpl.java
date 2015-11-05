package cn.momia.service.course.base.impl;

import cn.momia.api.poi.PoiServiceApi;
import cn.momia.api.poi.dto.PlaceDto;
import cn.momia.common.service.DbAccessService;
import cn.momia.service.course.base.BookedCourse;
import cn.momia.service.course.base.Course;
import cn.momia.service.course.base.CourseBook;
import cn.momia.service.course.base.CourseBookImage;
import cn.momia.service.course.base.CourseComment;
import cn.momia.service.course.base.CourseDetail;
import cn.momia.service.course.base.CourseImage;
import cn.momia.service.course.base.CourseService;
import cn.momia.service.course.base.CourseSku;
import cn.momia.service.course.base.CourseSkuPlace;
import cn.momia.service.course.base.Institution;
import cn.momia.service.course.base.Teacher;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseServiceImpl extends DbAccessService implements CourseService {
    private PoiServiceApi poiServiceApi;

    public void setPoiServiceApi(PoiServiceApi poiServiceApi) {
        this.poiServiceApi = poiServiceApi;
    }

    @Override
    public Course get(long courseId) {
        Collection<Long> courseIds = Sets.newHashSet(courseId);
        List<Course> courses = list(courseIds);

        return courses.isEmpty() ? Course.NOT_EXIST_COURSE : courses.get(0);
    }

    @Override
    public List<Course> list(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new ArrayList<Course>();

        String sql = "SELECT Id, SubjectId, Title, Cover, MinAge, MaxAge, Insurance, Joined, Price, Goal, Flow, Tips, InstitutionId FROM SG_Course WHERE Id IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<Course> courses = queryList(sql, Course.class);

        Set<Integer> institutionIds = new HashSet<Integer>();
        for (Course course : courses) {
            institutionIds.add(course.getInstitutionId());
        }
        Map<Integer, Institution> institutionsMap = queryInstitutions(institutionIds);
        Map<Long, List<CourseImage>> imgsMap = queryImgs(courseIds);
        Map<Long, CourseBook> booksMap = queryBooks(courseIds);
        Map<Long, List<CourseSku>> skusMap = querySkus(courseIds);

        for (Course course : courses) {
            Institution institution = institutionsMap.get(course.getInstitutionId());
            if (institution != null) course.setInstitution(institution.getIntro());
            course.setImgs(imgsMap.get(course.getId()));
            course.setBook(booksMap.get(course.getId()));
            course.setSkus(skusMap.get(course.getId()));
        }

        Map<Long, Course> coursesMap = new HashMap<Long, Course>();
        for (Course course : courses) {
            coursesMap.put(course.getId(), course);
        }

        List<Course> result = new ArrayList<Course>();
        for (long courseId : courseIds) {
            Course course = coursesMap.get(courseId);
            if (course != null) result.add(course);
        }

        return result;
    }

    private Map<Integer, Institution> queryInstitutions(Collection<Integer> institutionIds) {
        if (institutionIds.isEmpty()) return new HashMap<Integer, Institution>();

        String sql = "SELECT Id, Name, Cover, Intro FROM SG_Institution WHERE Id IN (" + StringUtils.join(institutionIds, ",") + ") AND Status=1";
        List<Institution> institutions = queryList(sql, Institution.class);

        Map<Integer, Institution> institutionsMap = new HashMap<Integer, Institution>();
        for (Institution institution : institutions) {
            institutionsMap.put(institution.getId(), institution);
        }

        return institutionsMap;
    }

    private Map<Long, List<CourseImage>> queryImgs(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<CourseImage>>();

        String sql = "SELECT Id, CourseId, Url, Width, Height FROM SG_CourseImg WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<CourseImage> imgs = queryList(sql, CourseImage.class);

        final Map<Long, List<CourseImage>> imgsMap = new HashMap<Long, List<CourseImage>>();
        for (long courseId : courseIds) {
            imgsMap.put(courseId, new ArrayList<CourseImage>());
        }
        for (CourseImage img : imgs) {
            imgsMap.get(img.getCourseId()).add(img);
        }

        return imgsMap;
    }

    private Map<Long, CourseBook> queryBooks(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, CourseBook>();

        String sql = "SELECT Id, CourseId, Img, `Order` FROM SG_CourseBook WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1 ORDER BY `Order` ASC";
        List<CourseBookImage> imgs = queryList(sql, CourseBookImage.class);

        final Map<Long, List<CourseBookImage>> imgsMap = new HashMap<Long, List<CourseBookImage>>();
        for (long courseId : courseIds) {
            imgsMap.put(courseId, new ArrayList<CourseBookImage>());
        }
        for (CourseBookImage img : imgs) {
            imgsMap.get(img.getCourseId()).add(img);
        }

        Map<Long, CourseBook> booksMap = new HashMap<Long, CourseBook>();
        for (long courseId : courseIds) {
            List<CourseBookImage> bookImgs = imgsMap.get(courseId);
            List<String> urls = new ArrayList<String>();
            for (CourseBookImage bookImg : bookImgs) {
                urls.add(bookImg.getImg());
            }

            CourseBook book = new CourseBook();
            book.setImgs(urls);

            booksMap.put(courseId, book);
        }

        return booksMap;
    }

    private Map<Long, List<CourseSku>> querySkus(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return new HashMap<Long, List<CourseSku>>();

        String sql = "SELECT Id FROM SG_CourseSku WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        List<Long> skuIds = queryLongList(sql);
        List<CourseSku> skus = listSkus(skuIds);

        Map<Long, List<CourseSku>> skusMap = new HashMap<Long, List<CourseSku>>();
        for (long courseId : courseIds) {
            skusMap.put(courseId, new ArrayList<CourseSku>());
        }
        for (CourseSku sku : skus) {
            skusMap.get(sku.getCourseId()).add(sku);
        }

        return skusMap;
    }

    private List<CourseSku> listSkus(Collection<Long> skuIds) {
        if (skuIds.isEmpty()) return new ArrayList<CourseSku>();

        String sql = "SELECT Id, CourseId, StartTime, EndTime, Deadline, Stock, UnlockedStock, LockedStock, PlaceId, Adult, Child FROM SG_CourseSku WHERE Id IN (" + StringUtils.join(skuIds, ",") + ") AND Status=1";
        List<CourseSku> skus = queryList(sql, CourseSku.class);

        Map<Long, CourseSku> skusMap = new HashMap<Long, CourseSku>();
        for (CourseSku sku : skus) {
            skusMap.put(sku.getId(), sku);
        }

        List<CourseSku> result = new ArrayList<CourseSku>();
        for (long skuId : skuIds) {
            CourseSku sku = skusMap.get(skuId);
            if (sku != null) result.add(sku);
        }

        return completeSkus(result);
    }

    private List<CourseSku> completeSkus(List<CourseSku> skus) {
        Set<Integer> placeIds = new HashSet<Integer>();
        for (CourseSku sku : skus) {
            placeIds.add(sku.getPlaceId());
        }

        List<PlaceDto> places = poiServiceApi.list(placeIds);
        Map<Integer, PlaceDto> placesMap = new HashMap<Integer, PlaceDto>();
        for (PlaceDto place : places) {
            placesMap.put(place.getId(), place);
        }

        List<CourseSku> completedSkus = new ArrayList<CourseSku>();
        for (CourseSku sku : skus) {
            PlaceDto place = placesMap.get(sku.getPlaceId());
            if (place == null) continue;

            sku.setPlace(buildCourseSkuPlace(place));
            completedSkus.add(sku);
        }

        return completedSkus;
    }

    private CourseSkuPlace buildCourseSkuPlace(PlaceDto place) {
        CourseSkuPlace courseSkuPlace = new CourseSkuPlace();
        courseSkuPlace.setId(place.getId());
        courseSkuPlace.setCityId(place.getCityId());
        courseSkuPlace.setRegionId(place.getRegionId());
        courseSkuPlace.setName(place.getName());
        courseSkuPlace.setAddress(place.getAddress());
        courseSkuPlace.setLng(place.getLng());
        courseSkuPlace.setLat(place.getLat());

        return courseSkuPlace;
    }

    @Override
    public long queryBookImgCount(long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseBook WHERE CourseId=? AND Status=1";
        return queryLong(sql, new Object[] { courseId });
    }

    @Override
    public List<String> queryBookImgs(long courseId, int start, int count) {
        String sql = "SELECT Img FROM SG_CourseBook WHERE CourseId=? AND Status=1 ORDER BY `Order` ASC LIMIT ?,?";
        return queryStringList(sql, new Object[] { courseId, start, count });
    }

    @Override
    public long queryTeacherCount(long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_CourseTeacher WHERE CourseId=? AND Status=1";
        return queryLong(sql, new Object[] { courseId });
    }

    @Override
    public List<Teacher> queryTeachers(long courseId, int start, int count) {
        String sql = "SELECT TeacherId FROM SG_CourseTeacher WHERE CourseId=? AND Status=1 LIMIT ?,?";
        List<Integer> teacherIds = queryIntList(sql, new Object[] { courseId, start, count });

        return listTeachers(teacherIds);
    }

    private List<Teacher> listTeachers(List<Integer> teacherIds) {
        if (teacherIds.isEmpty()) return new ArrayList<Teacher>();

        String sql = "SELECT Id, Name, Avatar, Education, Experience FROM SG_Teacher WHERE Id IN (" + StringUtils.join(teacherIds, ",") + ") AND Status=1";
        return queryList(sql, Teacher.class);
    }

    @Override
    public long queryCountBySubject(int subjectId) {
        String sql = "SELECT COUNT(DISTINCT A.Id) FROM SG_Course A INNER JOIN SG_CourseSku B ON A.Id=B.CourseId WHERE A.SubjectId=? AND A.Status=1 AND B.Deadline>NOW() AND B.Status=1";
        return queryLong(sql, new Object[] { subjectId });
    }

    @Override
    public List<Course> queryBySubject(int subjectId, int start, int count) {
        String sql = "SELECT A.Id FROM SG_Course A INNER JOIN SG_CourseSku B ON A.Id=B.CourseId WHERE A.SubjectId=? AND A.Status=1 AND B.Deadline>NOW() AND B.Status=1 GROUP BY A.Id ORDER BY MIN(B.StartTime) ASC LIMIT ?,?";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId, start, count });

        return list(courseIds);
    }

    @Override
    public List<Course> queryAllBySubject(long subjectId) {
        Set<Long> subjectIds = Sets.newHashSet(subjectId);
        Map<Long, List<Course>> coursesMap = queryAllBySubjects(subjectIds);

        return coursesMap.get(subjectId);
    }

    @Override
    public Map<Long, List<Course>> queryAllBySubjects(Collection<Long> subjectIds) {
        if (subjectIds.isEmpty()) return new HashMap<Long, List<Course>>();

        String sql = "SELECT Id FROM SG_Course WHERE SubjectId IN (" + StringUtils.join(subjectIds, ",") + ") AND Status=1 ORDER BY AddTime DESC";
        List<Long> courseIds = queryLongList(sql);
        List<Course> courses = list(courseIds);

        Map<Long, List<Course>> coursesMap = new HashMap<Long, List<Course>>();
        for (long subjectId : subjectIds) {
            coursesMap.put(subjectId, new ArrayList<Course>());
        }
        for (Course course : courses) {
            coursesMap.get(course.getSubjectId()).add(course);
        }

        return coursesMap;
    }

    @Override
    public List<CourseSku> querySkus(long courseId, String start, String end) {
        String sql = "SELECT Id FROM SG_CourseSku WHERE CourseId=? AND StartTime>=? AND EndTime<? AND Status=1 ORDER BY StartTime ASC";
        List<Long> skuIds = queryLongList(sql, new Object[] { courseId, start, end });

        return listSkus(skuIds);
    }

    @Override
    public CourseSku getSku(long skuId) {
        Set<Long> skuIds = Sets.newHashSet(skuId);
        List<CourseSku> skus = listSkus(skuIds);

        return skus.isEmpty() ? CourseSku.NOT_EXIST_COURSE_SKU : skus.get(0);
    }

    @Override
    public boolean lockSku(long skuId) {
        String sql = "UPDATE SG_CourseSku SET UnlockedStock=UnlockedStock-1, LockedStock=LockedStock+1 WHERE Id=? AND Status=1 AND UnlockedStock>=1";
        return update(sql, new Object[] { skuId });
    }

    @Override
    public boolean unlockSku(long skuId) {
        String sql = "UPDATE SG_CourseSku SET UnlockedStock=UnlockedStock+1, LockedStock=LockedStock-1 WHERE Id=? AND Status=1 AND LockedStock>=1";
        return update(sql, new Object[] { skuId });
    }

    @Override
    public Map<Long, Date> queryStartTimesByPackages(Set<Long> packageIds) {
        if (packageIds.isEmpty()) return new HashMap<Long, Date>();

        final Map<Long, Date> startTimesMap = new HashMap<Long, Date>();
        String sql = "SELECT PackageId, MIN(StartTime) AS StartTime FROM SG_BookedCourse WHERE PackageId IN (" + StringUtils.join(packageIds, ",") + ") AND Status=1 GROUP BY PackageId";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                startTimesMap.put(rs.getLong("PackageId"), rs.getTimestamp("StartTime"));
            }
        });

        return startTimesMap;
    }

    @Override
    public BookedCourse getBookedCourse(long bookingId) {
        Set<Long> bookingIds = Sets.newHashSet(bookingId);
        List<BookedCourse> bookedCourses = listBookedCourses(bookingIds);

        return bookedCourses.isEmpty() ? BookedCourse.NOT_EXIST_BOOKED_COURSE : bookedCourses.get(0);
    }

    private List<BookedCourse> listBookedCourses(Collection<Long> bookingIds) {
        if (bookingIds.isEmpty()) return new ArrayList<BookedCourse>();

        String sql = "SELECT Id, UserId, OrderId, PackageId, CourseId, CourseSkuId, StartTime, EndTime FROM SG_BookedCourse WHERE Id IN (" + StringUtils.join(bookingIds, ",") + ") AND Status=1";
        List<BookedCourse> bookedCourses = queryList(sql, BookedCourse.class);

        Map<Long, BookedCourse> bookedCoursesMap = new HashMap<Long, BookedCourse>();
        for (BookedCourse bookedCourse : bookedCourses) {
            bookedCoursesMap.put(bookedCourse.getId(), bookedCourse);
        }

        List<BookedCourse> result = new ArrayList<BookedCourse>();
        for (long bookingId : bookingIds) {
            BookedCourse bookedCourse = bookedCoursesMap.get(bookingId);
            if (bookedCourse != null) result.add(bookedCourse);
        }

        return result;
    }

    @Override
    public long queryNotFinishedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime>NOW()";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<BookedCourse> queryNotFinishedByUser(long userId, int start, int count) {
        String sql = "SELECT Id FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime>NOW() ORDER BY StartTime ASC LIMIT ?,?";
        List<Long> bookingIds = queryLongList(sql, new Object[] { userId, start, count });

        return listBookedCourses(bookingIds);
    }

    @Override
    public long queryFinishedCountByUser(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime<=NOW()";
        return queryLong(sql, new Object[] { userId });
    }

    @Override
    public List<BookedCourse> queryFinishedByUser(long userId, int start, int count) {
        String sql = "SELECT Id FROM SG_BookedCourse WHERE UserId=? AND Status=1 AND StartTime<=NOW() ORDER BY StartTime ASC LIMIT ?,?";
        List<Long> bookingIds = queryLongList(sql, new Object[] { userId, start, count });

        return listBookedCourses(bookingIds);
    }

    @Override
    public Map<Long, Integer> queryBookedCourseCounts(Set<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, Integer>();

        final Map<Long, Integer> map = new HashMap<Long, Integer>();
        for (long orderId : orderIds) {
            map.put(orderId, 0);
        }
        String sql = "SELECT OrderId, COUNT(1) AS Count FROM SG_BookedCourse WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND Status=1 GROUP BY OrderId";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long orderId = rs.getLong("OrderId");
                int count = rs.getInt("Count");
                map.put(orderId, count);
            }
        });

        return map;
    }

    @Override
    public Map<Long, Integer> queryFinishedCourseCounts(Set<Long> orderIds) {
        if (orderIds.isEmpty()) return new HashMap<Long, Integer>();

        final Map<Long, Integer> map = new HashMap<Long, Integer>();
        for (long orderId : orderIds) {
            map.put(orderId, 0);
        }
        String sql = "SELECT OrderId, COUNT(1) AS Count FROM SG_BookedCourse WHERE OrderId IN (" + StringUtils.join(orderIds, ",") + ") AND EndTime<=NOW() AND Status=1 GROUP BY OrderId";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long orderId = rs.getLong("OrderId");
                int count = rs.getInt("Count");
                map.put(orderId, count);
            }
        });

        return map;
    }

    @Override
    public boolean booked(long packageId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE PackageId=? AND CourseId=? AND Status=1";
        return queryInt(sql, new Object[] { packageId, courseId }) > 0;
    }

    @Override
    public long booking(final long userId, final long orderId, final long packageId, final CourseSku sku) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_BookedCourse(UserId, OrderId, PackageId, CourseId, CourseSkuId, StartTime, EndTime, AddTime) VALUES(?, ?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setLong(2, orderId);
                ps.setLong(3, packageId);
                ps.setLong(4, sku.getCourseId());
                ps.setLong(5, sku.getId());
                ps.setDate(6, new java.sql.Date(sku.getStartTime().getTime()));
                ps.setDate(7, new java.sql.Date(sku.getEndTime().getTime()));

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void increaseJoined(long courseId, int joinCount) {
        String sql = "UPDATE SG_Course SET Joined=Joined+? WHERE Id=? AND Status=1";
        update(sql, new Object[] { joinCount, courseId });
    }

    @Override
    public boolean cancel(long userId, long bookingId) {
        String sql = "UPDATE SG_BookedCourse SET Status=0 WHERE Id=? AND UserId=? AND Status=1";
        return update(sql, new Object[] { bookingId, userId });
    }

    @Override
    public void decreaseJoined(long courseId, int joinCount) {
        String sql = "UPDATE SG_Course SET Joined=Joined-? WHERE Id=? AND Status=1 AND Joined>=?";
        update(sql, new Object[] { joinCount, courseId, joinCount });
    }

    @Override
    public CourseDetail getDetail(long courseId) {
        String sql = "SELECT Id, CourseId, Abstracts, Detail FROM SG_CourseDetail WHERE CourseId=? AND Status=1";
        return queryObject(sql, new Object[] { courseId }, CourseDetail.class, CourseDetail.NOT_EXIST_COURSE_DETAIL);
    }

    @Override
    public Institution getInstitution(long courseId) {
        String sql = "SELECT B.Id, B.Name, B.Cover, B.Intro FROM SG_Course A INNER JOIN SG_Institution B ON A.InstitutionId=B.Id WHERE A.Id=? AND A.Status=1 AND B.Status=1";
        return queryObject(sql, new Object[] { courseId }, Institution.class, Institution.NOT_EXIST_INSTITUTION);
    }

    @Override
    public boolean matched(long subjectId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_Course WHERE Id=? AND SubjectId=? AND Status=1";
        return queryInt(sql, new Object[] { courseId, subjectId }) > 0;
    }

    @Override
    public boolean finished(long userId, long courseId) {
        String sql = "SELECT COUNT(1) FROM SG_BookedCourse WHERE UserId=? AND CourseId=? AND Status=1 AND StartTime<=NOW()";
        return queryInt(sql, new Object[] { userId, courseId }) > 0;
    }

    @Override
    public boolean comment(CourseComment comment) {
        long commentId = addComment(comment);
        if (commentId <= 0) return false;

        addCommentImgs(commentId, comment.getImgs());

        return true;
    }

    private long addComment(final CourseComment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_CourseComment(UserId, CourseId, Star, Teacher, Environment, Content, AddTime) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, comment.getUserId());
                ps.setLong(2, comment.getCourseId());
                ps.setInt(3, comment.getStar());
                ps.setInt(4, comment.getTeacher());
                ps.setInt(5, comment.getEnvironment());
                ps.setString(6, comment.getContent());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private void addCommentImgs(long commentId, List<String> imgs) {
        List<Object[]> params = new ArrayList<Object[]>();
        for (String img : imgs) {
            params.add(new Object[] { commentId, img });
        }
        String sql = "INSERT INTO SG_CourseCommentImg (CommentId, Url, AddTime) VALUES (?, ?, NOW())";
        jdbcTemplate.batchUpdate(sql, params);
    }

    @Override
    public long queryCommentCountByCourse(long courseId) {
        Set<Long> courseIds = Sets.newHashSet(courseId);
        return queryCommentCountByCourses(courseIds);
    }

    private long queryCommentCountByCourses(Collection<Long> courseIds) {
        if (courseIds.isEmpty()) return 0;

        String sql = "SELECT COUNT(1) FROM SG_CourseComment WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1";
        return queryInt(sql, null);
    }

    @Override
    public List<CourseComment> queryCommentsByCourse(long courseId, int start, int count) {
        String sql = "SELECT Id FROM SG_CourseComment WHERE CourseId=? AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> commentIds = queryLongList(sql, new Object[] { courseId, start, count });

        return listComments(commentIds);
    }

    private List<CourseComment> queryCommentsByCourses(Collection<Long> courseIds, int start, int count) {
        if (courseIds.isEmpty()) return new ArrayList<CourseComment>();

        String sql = "SELECT Id FROM SG_CourseComment WHERE CourseId IN (" + StringUtils.join(courseIds, ",") + ") AND Status=1 ORDER BY AddTime DESC LIMIT ?,?";
        List<Long> commentIds = queryLongList(sql, new Object[] { start, count });

        return listComments(commentIds);
    }

    private List<CourseComment> listComments(List<Long> commentIds) {
        if (commentIds.isEmpty()) return new ArrayList<CourseComment>();

        String sql = "SELECT Id, UserId, CourseId, Star, Teacher, Environment, Content, AddTime FROM SG_CourseComment WHERE Id IN (" + StringUtils.join(commentIds, ",") + ") AND Status=1";
        List<CourseComment> comments = queryList(sql, CourseComment.class);

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

        String sql = "SELECT CommentId, Url FROM SG_CourseCommentImg WHERE CommentId IN (" + StringUtils.join(commentIds, ",") + ") AND Status=1";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
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
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId=? AND Status=1";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId });

        return queryCommentCountByCourses(courseIds);
    }

    @Override
    public List<CourseComment> queryCommentsBySubject(long subjectId, int start, int count) {
        String sql = "SELECT Id FROM SG_Course WHERE SubjectId=? AND Status=1";
        List<Long> courseIds = queryLongList(sql, new Object[] { subjectId });

        return queryCommentsByCourses(courseIds, start, count);
    }
}
