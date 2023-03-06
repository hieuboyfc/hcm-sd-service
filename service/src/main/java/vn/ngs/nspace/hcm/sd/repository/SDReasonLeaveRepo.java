package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.SDReasonLeave;

import java.util.List;
import java.util.Optional;

@Repository
public interface SDReasonLeaveRepo extends BaseRepo<SDReasonLeave, Long> {

    Optional<SDReasonLeave> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<SDReasonLeave> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<SDReasonLeave> findAllByCompanyIdAndStatusAndIdIn(Long cid, Integer status, List<Long> ids);

    @Query(value = "SELECT srl FROM SDReasonLeave AS srl " +
            "WHERE srl.companyId = :cid " +
            "AND srl.status = :status " +
            "AND CONCAT(COALESCE(LOWER(srl.code), ''), COALESCE(LOWER(srl.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY srl.name, srl.code ASC ")
    Page<SDReasonLeave> getListReasonLeave(Long cid, Integer status, String search, Pageable pageable);

}
