package com.xdp.service;

import com.xdp.dto.AffectLeaveWorkDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AffectLeaveWorkService {

    Page<AffectLeaveWorkDTO> getList(Long cid, String uid, String search, Pageable pageable);

    AffectLeaveWorkDTO create(Long cid, String uid, AffectLeaveWorkDTO dto);

    AffectLeaveWorkDTO update(Long cid, String uid, AffectLeaveWorkDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    AffectLeaveWorkDTO getDetail(Long cid, String uid, Long id);
}
