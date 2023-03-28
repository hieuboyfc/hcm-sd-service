package com.xdp.repository;

import com.xdp.entities.FuncVariable;
import com.xdp.lib.repo.BaseRepo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncVariableRepo extends BaseRepo<FuncVariable, Long> {

    List<FuncVariable> findAllByCompanyIdAndStatus(Long cid, Integer status);

}
