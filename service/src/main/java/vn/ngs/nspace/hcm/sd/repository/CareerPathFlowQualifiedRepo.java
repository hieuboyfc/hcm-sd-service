package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.CareerPathFlowQualified;

import java.util.List;

@Repository
public interface CareerPathFlowQualifiedRepo extends BaseRepo<CareerPathFlowQualified, Long> {

    List<CareerPathFlowQualified> findAllByCompanyIdAndCareerPathFlowIdIn(Long cid, List<Long> careerPathFlowId);

}
