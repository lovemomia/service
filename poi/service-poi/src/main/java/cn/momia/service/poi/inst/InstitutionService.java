package cn.momia.service.poi.inst;

import cn.momia.api.poi.dto.Institution;

import java.util.Collection;
import java.util.List;

public interface InstitutionService {
    Institution get(int institutionId);
    List<Institution> list(Collection<Integer> institutionIds);
}
