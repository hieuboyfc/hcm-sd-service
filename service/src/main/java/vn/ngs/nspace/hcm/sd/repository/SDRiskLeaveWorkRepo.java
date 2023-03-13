package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.SDRiskLeaveWork;

import java.util.List;
import java.util.Optional;

@Repository
public interface SDRiskLeaveWorkRepo extends BaseRepo<SDRiskLeaveWork, Long> {

    @Query(value = "SELECT srlw FROM SDRiskLeaveWork AS srlw " +
            "WHERE srlw.companyId = :cid " +
            "   AND (srlw.id <> :id OR -1 = :id)  " +
            "   AND srlw.status = :status ")
    SDRiskLeaveWork findByIdExists(Long cid, Integer status, Long id);

    Optional<SDRiskLeaveWork> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<SDRiskLeaveWork> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<SDRiskLeaveWork> findAllByCompanyIdAndIdIn(Long cid, List<Long> ids);

    @Query(value = "SELECT srlw FROM SDRiskLeaveWork AS srlw " +
            "WHERE srlw.companyId = :cid " +
            "AND CONCAT(COALESCE(LOWER(srlw.code), ''), COALESCE(LOWER(srlw.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY srlw.name, srlw.code ASC ")
    Page<SDRiskLeaveWork> getListRiskLeaveWork(Long cid, String search, Pageable pageable);

}
