package com.xdp.module_spfood.service;

import com.xdp.module_spfood.dto.SPFoodTicketDTO;
import com.xdp.module_spfood.response.FileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface SPFoodTicketService {

    Page<SPFoodTicketDTO> getList(Long cid, String uid, String search, Pageable pageable);

    SPFoodTicketDTO create(Long cid, String uid, SPFoodTicketDTO dto);

    SPFoodTicketDTO update(Long cid, String uid, SPFoodTicketDTO dto);

    void delete(Long cid, String uid, Long id);

    SPFoodTicketDTO getDetail(Long cid, String uid, Long id);

    FileResponse getDataFromFile(Long cid, String uid, MultipartFile file);

}
