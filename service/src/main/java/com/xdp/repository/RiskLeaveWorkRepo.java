package com.xdp.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.xdp.entities.RiskLeaveWork;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskLeaveWorkRepo extends BaseRepo<RiskLeaveWork, Long> {

    @Query(value = "SELECT srlw FROM RiskLeaveWork AS srlw " +
            "WHERE srlw.companyId = :cid " +
            "AND CONCAT(COALESCE(LOWER(srlw.code), ''), COALESCE(LOWER(srlw.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY srlw.name, srlw.code ASC ")
    Page<RiskLeaveWork> getListRiskLeaveWork(Long cid, String search, Pageable pageable);

    @Query(value = "SELECT srlw FROM RiskLeaveWork AS srlw " +
            "WHERE srlw.companyId = :cid " +
            "   AND (srlw.id <> :id OR -1 = :id)  " +
            "   AND srlw.status = :status ")
    RiskLeaveWork findByIdExists(Long cid, Integer status, Long id);

    Optional<RiskLeaveWork> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<RiskLeaveWork> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    Optional<RiskLeaveWork> findByCompanyIdAndId(Long cid, Long id);

    List<RiskLeaveWork> findAllByCompanyIdAndIdIn(Long cid, List<Long> ids);

}
