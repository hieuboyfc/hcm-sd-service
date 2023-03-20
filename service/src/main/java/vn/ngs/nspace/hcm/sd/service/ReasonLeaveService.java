package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.ReasonLeaveDTO;

import java.util.List;

public interface ReasonLeaveService {

    Page<ReasonLeaveDTO> getList(Long cid, String uid, String search, Pageable pageable);

    ReasonLeaveDTO create(Long cid, String uid, ReasonLeaveDTO dto);

    ReasonLeaveDTO update(Long cid, String uid, ReasonLeaveDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    ReasonLeaveDTO getDetail(Long cid, String uid, Long id);
}
