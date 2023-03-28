package com.xdp.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.xdp.entities.CareerPath;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CareerPathRepo extends BaseRepo<CareerPath, Long> {

    @Query(value = "SELECT cp FROM CareerPath cp " +
            "       WHERE cp.companyId = :cid " +
            "           AND (cp.status = :status OR -1 =:status) " +
            "           AND (cp.orgId = :orgId OR -1 = :orgId) " +
            "           AND cp.createDate <= :endDate AND cp.createDate >= :startDate " +
            "           AND CONCAT(LOWER(COALESCE(cp.code, '')), '|' , LOWER(COALESCE(cp.name, ''))) LIKE LOWER(:search) ")
    Page<CareerPath> search(@Param("cid") Long cid,
                            @Param("status") Integer status,
                            @Param("orgId") Long orgId,
                            @Param("startDate") Date startDate,
                            @Param("endDate") Date endDate,
                            @Param("search") String search,
                            Pageable pageable);

    Optional<CareerPath> findByCompanyIdAndId(Long cid, Long id);

    Optional<CareerPath> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    Optional<CareerPath> findByCompanyIdAndCode(Long cid, String code);

    List<CareerPath> findAllByCompanyIdAndCode(Long cid, String code);

    List<CareerPath> findAllByCompanyIdAndIdIn(Long cid, List<Long> ids);

}
