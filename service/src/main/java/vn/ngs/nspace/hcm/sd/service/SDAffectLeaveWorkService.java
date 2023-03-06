package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.SDAffectLeaveWorkDTO;

import java.util.List;

public interface SDAffectLeaveWorkService {

    Page<SDAffectLeaveWorkDTO> getList(long cid, String uid, String search, Pageable pageable);

    SDAffectLeaveWorkDTO create(Long cid, String uid, SDAffectLeaveWorkDTO dto);

    SDAffectLeaveWorkDTO update(Long cid, String uid, SDAffectLeaveWorkDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    SDAffectLeaveWorkDTO getDetail(Long cid, String uid, Long id);
}
