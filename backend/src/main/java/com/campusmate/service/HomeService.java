package com.campusmate.service;

import com.campusmate.domain.dto.HomePlazaQuery;
import com.campusmate.domain.vo.HomePlazaVO;

public interface HomeService {

    HomePlazaVO getHomePlaza(HomePlazaQuery query);
}
