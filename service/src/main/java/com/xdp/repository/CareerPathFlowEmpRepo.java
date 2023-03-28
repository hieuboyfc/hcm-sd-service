package com.xdp.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.xdp.entities.CareerPathFlowEmp;

import java.util.List;
import java.util.Set;

@Repository
public interface CareerPathFlowEmpRepo extends BaseRepo<CareerPathFlowEmp, Long> {

    List<CareerPathFlowEmp> findAllByCompanyIdAndCareerPathFlowIdIn(Long cid, List<Long> careerPathFlowId);

    @Query("SELECT cpfe.empId FROM CareerPath cp " +
            " JOIN CareerPathFlow cpf ON cpf.careerPathId = cp.id AND cpf.companyId = :cid " +
            " JOIN CareerPathFlowEmp cpfe ON cpfe.careerPathFlowId = cpf.id AND cpfe.companyId = :cid " +
            " WHERE cp.companyId = :cid AND cpfe.careerPathFlowId IN (:careerPathFlowIds) ")
    Set<Long> getEmpIdInCareerPathFlow(Long cid, List<Long> careerPathFlowIds);
}
