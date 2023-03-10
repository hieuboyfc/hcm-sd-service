package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.RiskLeaveWorkDTO;

import java.util.List;

public interface RiskLeaveWorkService {

    Page<RiskLeaveWorkDTO> getList(long cid, String uid, String search, Pageable pageable);

    RiskLeaveWorkDTO create(Long cid, String uid, RiskLeaveWorkDTO dto);

    RiskLeaveWorkDTO update(Long cid, String uid, RiskLeaveWorkDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    RiskLeaveWorkDTO getDetail(Long cid, String uid, Long id);
}
