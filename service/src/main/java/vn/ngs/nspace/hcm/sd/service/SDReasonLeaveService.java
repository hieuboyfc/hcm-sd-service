package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.category.SDReasonLeaveDTO;

import java.util.List;

public interface SDReasonLeaveService {

    Page<SDReasonLeaveDTO> getList(long cid, String uid, String search, Pageable pageable);

    SDReasonLeaveDTO create(Long cid, String uid, SDReasonLeaveDTO dto);

    SDReasonLeaveDTO update(Long cid, String uid, SDReasonLeaveDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    SDReasonLeaveDTO getDetail(Long cid, String uid, Long id);
}
