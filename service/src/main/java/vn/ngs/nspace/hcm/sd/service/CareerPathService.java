package vn.ngs.nspace.hcm.sd.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.hcm.sd.share.dto.CareerPathDTO;

import java.util.List;

public interface CareerPathService {

    Page<CareerPathDTO> search(Long cid, String uid, CareerPathDTO dto, Pageable pageable);

    CareerPathDTO create(Long cid, String uid, CareerPathDTO dto);

    CareerPathDTO update(Long cid, String uid, CareerPathDTO dto);

    void delete(Long cid, String uid, List<Long> ids);

    CareerPathDTO getDetail(Long cid, String uid, Long id);

}
