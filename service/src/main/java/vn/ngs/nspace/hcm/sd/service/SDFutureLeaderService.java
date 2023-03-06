package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.SDFutureLeaderDTO;

import java.util.List;

public interface SDFutureLeaderService {

    Page<SDFutureLeaderDTO> getList(long cid, String uid, String search, Pageable pageable);

    SDFutureLeaderDTO create(Long cid, String uid, SDFutureLeaderDTO dto);

    SDFutureLeaderDTO update(Long cid, String uid, SDFutureLeaderDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    SDFutureLeaderDTO getDetail(Long cid, String uid, Long id);
}
