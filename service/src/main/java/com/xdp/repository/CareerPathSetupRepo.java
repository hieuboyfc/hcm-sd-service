package com.xdp.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.xdp.entities.CareerPathSetup;

import java.util.Optional;

@Repository
public interface CareerPathSetupRepo extends BaseRepo<CareerPathSetup, Long> {

    @Query(value = "SELECT cps FROM CareerPathSetup AS cps " +
            "WHERE cps.companyId = :cid " +
            "   AND CONCAT(COALESCE(LOWER(cps.code), ''), COALESCE(LOWER(cps.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY cps.name, cps.code ASC ")
    Page<CareerPathSetup> getListCareerPathSetup(Long cid, String search, Pageable pageable);

    Optional<CareerPathSetup> findByCompanyIdAndId(Long cid, Long id);

}
