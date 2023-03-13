package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.FuncVariable;

import java.util.List;

@Repository
public interface FuncVariableRepo extends BaseRepo<FuncVariable, Long> {

    List<FuncVariable> findAllByCompanyIdAndStatus(Long cid, Integer status);

}
