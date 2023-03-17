package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.AffectLeaveWork;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectLeaveWorkRepo extends BaseRepo<AffectLeaveWork, Long> {

    @Query(value = "SELECT salw FROM AffectLeaveWork AS salw " +
            "WHERE salw.companyId = :cid " +
            "   AND (salw.id <> :id OR -1 = :id)  " +
            "   AND salw.status = :status ")
    AffectLeaveWork findByIdExists(Long cid, Integer status, Long id);

    Optional<AffectLeaveWork> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<AffectLeaveWork> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    Optional<AffectLeaveWork> findByCompanyIdAndId(Long cid, Long id);

    List<AffectLeaveWork> findAllByCompanyIdAndIdIn(Long cid, List<Long> ids);

    @Query(value = "SELECT salw FROM AffectLeaveWork AS salw " +
            "WHERE salw.companyId = :cid " +
            "   AND CONCAT(COALESCE(LOWER(salw.code), ''), COALESCE(LOWER(salw.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY salw.name, salw.code ASC ")
    Page<AffectLeaveWork> getListAffectLeaveWork(Long cid, String search, Pageable pageable);

}
