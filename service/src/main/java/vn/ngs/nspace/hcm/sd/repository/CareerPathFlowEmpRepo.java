package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.CareerPathFlowEmp;

import java.util.List;

@Repository
public interface CareerPathFlowEmpRepo extends BaseRepo<CareerPathFlowEmp, Long> {

    List<CareerPathFlowEmp> findAllByCompanyIdAndCareerPathFlowIdIn(Long cid, List<Long> careerPathFlowId);

}
