package com.xdp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xdp.dto.HeirSetupDTO;

import java.util.Map;

public interface HeirSetupService {

    Map<String, Object> initSetup(Long cid, String uid);

    Map<String, Object> getDetail(Long cid, String uid, Long id);

    HeirSetupDTO update(Long cid, String uid, HeirSetupDTO dto) throws JsonProcessingException;

}
