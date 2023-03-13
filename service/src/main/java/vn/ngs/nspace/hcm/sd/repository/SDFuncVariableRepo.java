package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.SDFuncVariable;

import java.util.List;

@Repository
public interface SDFuncVariableRepo extends BaseRepo<SDFuncVariable, Long> {

    List<SDFuncVariable> findAllByCompanyIdAndStatus(Long cid, Integer status);

}
