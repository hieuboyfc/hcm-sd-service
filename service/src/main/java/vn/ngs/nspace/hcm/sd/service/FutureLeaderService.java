package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.FutureLeaderDTO;

import java.util.List;

public interface FutureLeaderService {

    Page<FutureLeaderDTO> getList(Long cid, String uid, String search, Pageable pageable);

    FutureLeaderDTO create(Long cid, String uid, FutureLeaderDTO dto);

    FutureLeaderDTO update(Long cid, String uid, FutureLeaderDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    FutureLeaderDTO getDetail(Long cid, String uid, Long id);
}
