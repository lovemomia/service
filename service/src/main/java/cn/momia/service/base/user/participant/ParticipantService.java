package cn.momia.service.base.user.participant;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ParticipantService {
    long add(Participant participant);
    boolean update(Participant participant);
    boolean updateByName(long id, String name, long userId);
    boolean updateBySex(long id, String sex, long userId);
    boolean updateByBirthday(long id, Date birthday, long userId);
    boolean delete(long id, long userId);
    Participant get(long id);
    Map<Long, Participant> get(Collection<Long> ids);
    List<Participant> getByUser(long userId);
}
