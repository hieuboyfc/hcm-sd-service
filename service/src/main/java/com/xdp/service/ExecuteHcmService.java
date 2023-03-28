package com.xdp.service;

import com.xdp.dto.PositionDTO;

import java.util.List;
import java.util.Map;

public interface ExecuteHcmService {

    List<PositionDTO> getDataPositionAndEmployee(Long cid, String uid, Map<String, Object> filter);

}
