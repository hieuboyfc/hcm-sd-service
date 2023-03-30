package com.xdp.service;

import com.xdp.dto.CareerPathDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CareerPathOldService {

    Page<CareerPathDTO> search(Long cid, String uid, CareerPathDTO dto, Pageable pageable);

    CareerPathDTO create(Long cid, String uid, CareerPathDTO dto);

    CareerPathDTO update(Long cid, String uid, CareerPathDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    CareerPathDTO refresh(Long cid, String uid, Long id);

    CareerPathDTO getDetail(Long cid, String uid, Long id);

}
