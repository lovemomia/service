package cn.momia.service.base.user.participant;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ParticipantService {
    long add(Participant participant);
    boolean update(Participant participant);
    boolean delete(long id, long userId);
    Participant get(long id);
    Map<Long, Participant> get(Collection<Long> ids);
    List<Participant> getByUser(long userId);
}
