package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.SDRiskLeaveWorkDTO;

import java.util.List;

public interface SDRiskLeaveWorkService {

    Page<SDRiskLeaveWorkDTO> getList(long cid, String uid, String search, Pageable pageable);

    SDRiskLeaveWorkDTO create(Long cid, String uid, SDRiskLeaveWorkDTO dto);

    SDRiskLeaveWorkDTO update(Long cid, String uid, SDRiskLeaveWorkDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    SDRiskLeaveWorkDTO getDetail(Long cid, String uid, Long id);
}
