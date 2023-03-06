package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.SDAffectLeaveWork;

import java.util.List;
import java.util.Optional;

@Repository
public interface SDAffectLeaveWorkRepo extends BaseRepo<SDAffectLeaveWork, Long> {

    Optional<SDAffectLeaveWork> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<SDAffectLeaveWork> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<SDAffectLeaveWork> findAllByCompanyIdAndStatusAndIdIn(Long cid, Integer status, List<Long> ids);

    @Query(value = "SELECT salw FROM SDAffectLeaveWork AS salw " +
            "WHERE salw.companyId = :cid " +
            "AND salw.status = :status " +
            "AND CONCAT(COALESCE(LOWER(salw.code), ''), COALESCE(LOWER(salw.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY salw.name, salw.code ASC ")
    Page<SDAffectLeaveWork> getListAffectLeaveWork(Long cid, Integer status, String search, Pageable pageable);

}
