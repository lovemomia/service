package cn.momia.service.user.child;

import cn.momia.api.user.dto.Child;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ChildService {
    long add(Child child);

    Child get(long childId);
    List<Child> list(Collection<Long> childIds);

    Map<Long, List<Child>> queryByUsers(Collection<Long> userIds);

    boolean updateAvatar(long userId, long childId, String avatar);
    boolean updateName(long userId, long childId, String name);
    boolean updateSex(long userId, long childId, String sex);
    boolean updateBirthday(long userId, long childId, Date birthday);

    boolean delete(long userId, long childId);
}
