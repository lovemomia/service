package cn.momia.service.poi.inst;

import java.util.Collection;
import java.util.List;

public interface InstitutionService {
    Institution get(int institutionId);
    List<Institution> list(Collection<Integer> institutionIds);
}
