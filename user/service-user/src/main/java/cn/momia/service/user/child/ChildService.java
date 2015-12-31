package cn.momia.service.user.child;

import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.ChildComment;
import cn.momia.api.user.dto.ChildRecord;
import cn.momia.api.user.dto.ChildTag;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ChildService {
    long add(Child child);

    Child get(long childId);
    List<Child> list(Collection<Long> childrenIds);

    Map<Long, List<Child>> queryByUsers(Collection<Long> userIds);

    boolean updateAvatar(long userId, long childId, String avatar);
    boolean updateName(long userId, long childId, String name);
    boolean updateSex(long userId, long childId, String sex);
    boolean updateBirthday(long userId, long childId, Date birthday);

    boolean delete(long userId, long childId);

    List<ChildTag> listAllTags();
    ChildRecord getRecord(long userId, long childId, long courseId, long courseSkuId);
    boolean record(ChildRecord childRecord);

    long queryCommentsCount(long childId);
    List<ChildComment> queryComments(long childId, int start, int count);
    boolean comment(ChildComment childComment);

    List<Long> queryCommentedChildIds(long courseId, long courseSkuId);
}
