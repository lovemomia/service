package cn.momia.service.user.participant;

import cn.momia.service.base.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ParticipantService extends Service {
    long add(Participant participant);

    Participant get(long id);
    List<Participant> get(Collection<Long> ids);
    List<Participant> getByUser(long userId);

    boolean update(Participant participant);
    boolean updateName(long userId, long id, String name);
    boolean updateSex(long userId, long id, String sex);
    boolean updateBirthday(long userId, long id, Date birthday);
    boolean delete(long userId, long id);

    boolean check(long userId, Collection<Long> ids);
}
