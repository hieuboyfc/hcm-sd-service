package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.AffectLeaveWorkDTO;

import java.util.List;

public interface AffectLeaveWorkService {

    Page<AffectLeaveWorkDTO> getList(long cid, String uid, String search, Pageable pageable);

    AffectLeaveWorkDTO create(Long cid, String uid, AffectLeaveWorkDTO dto);

    AffectLeaveWorkDTO update(Long cid, String uid, AffectLeaveWorkDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    AffectLeaveWorkDTO getDetail(Long cid, String uid, Long id);
}
