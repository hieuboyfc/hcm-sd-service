package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.CareerPath;

import java.util.Optional;

@Repository
public interface CareerPathRepo extends BaseRepo<CareerPath, Long> {

    Optional<CareerPath> findByCompanyIdAndId(Long cid, Long id);

    Optional<CareerPath> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    Optional<CareerPath> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

}
