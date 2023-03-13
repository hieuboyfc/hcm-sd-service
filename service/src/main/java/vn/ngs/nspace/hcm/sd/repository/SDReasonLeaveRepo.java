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

    @Query(value = "SELECT srl FROM SDReasonLeave AS srl " +
            "WHERE srl.companyId = :cid " +
            "   AND (srl.id <> :id OR -1 = :id)  " +
            "   AND srl.status = :status ")
    SDReasonLeave findByIdExists(Long cid, Integer status, Long id);

    Optional<SDReasonLeave> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<SDReasonLeave> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<SDReasonLeave> findAllByCompanyIdAndIdIn(Long cid, List<Long> ids);

    @Query(value = "SELECT srl FROM SDReasonLeave AS srl " +
            "WHERE srl.companyId = :cid " +
            "AND CONCAT(COALESCE(LOWER(srl.code), ''), COALESCE(LOWER(srl.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY srl.name, srl.code ASC ")
    Page<SDReasonLeave> getListReasonLeave(Long cid, String search, Pageable pageable);

}
