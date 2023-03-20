package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathSetupDTO;

public interface CareerPathSetupService {

    Page<CareerPathSetupDTO> getList(Long cid, String uid, String search, Pageable pageable);

    CareerPathSetupDTO update(Long cid, String uid, CareerPathSetupDTO dto);

    CareerPathSetupDTO getDetail(Long cid, String uid, Long id);

}
