package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.category.SDRiskLeaveWork;

import java.util.List;
import java.util.Optional;

@Repository
public interface SDRiskLeaveWorkRepo extends BaseRepo<SDRiskLeaveWork, Long> {

    Optional<SDRiskLeaveWork> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<SDRiskLeaveWork> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<SDRiskLeaveWork> findAllByCompanyIdAndStatusAndIdIn(Long cid, Integer status, List<Long> ids);

    @Query(value = "SELECT srlw FROM SDRiskLeaveWork AS srlw " +
            "WHERE srlw.companyId = :cid " +
            "AND srlw.status = :status " +
            "AND CONCAT(COALESCE(LOWER(srlw.code), ''), COALESCE(LOWER(srlw.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY srlw.name, srlw.code ASC ")
    Page<SDRiskLeaveWork> getListRiskLeaveWork(Long cid, Integer status, String search, Pageable pageable);

}
