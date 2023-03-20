package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.HeirSetup;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeirSetupRepo extends BaseRepo<HeirSetup, Long> {

    List<HeirSetup> findByCompanyIdAndStatusOrderByIdDesc(Long cid, Integer status);

    Optional<HeirSetup> findByCompanyIdAndId(Long cid, Long id);

}
