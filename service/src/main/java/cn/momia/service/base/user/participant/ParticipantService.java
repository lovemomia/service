package cn.momia.service.base.user.participant;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ParticipantService {
    long add(Participant participant);
    boolean update(Participant participant);
    boolean delete(long id, long userId);
    Participant get(long id);
    Map<Long, Participant> get(List<Long> ids);
    List<Participant> getByUser(long userId);
}
