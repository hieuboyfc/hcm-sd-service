package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.FutureLeader;

import java.util.List;
import java.util.Optional;

@Repository
public interface FutureLeaderRepo extends BaseRepo<FutureLeader, Long> {

    @Query(value = "SELECT sfl FROM FutureLeader AS sfl " +
            "WHERE sfl.companyId = :cid " +
            "   AND (sfl.id <> :id OR -1 = :id)  " +
            "   AND sfl.status = :status ")
    FutureLeader findByIdExists(Long cid, Integer status, Long id);

    Optional<FutureLeader> findByCompanyIdAndStatusAndCode(Long cid, Integer status, String code);

    Optional<FutureLeader> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    Optional<FutureLeader> findByCompanyIdAndId(Long cid, Long id);

    List<FutureLeader> findAllByCompanyIdAndIdIn(Long cid, List<Long> ids);

    @Query(value = "SELECT sfl FROM FutureLeader AS sfl " +
            "WHERE sfl.companyId = :cid " +
            "AND CONCAT(COALESCE(LOWER(sfl.code), ''), COALESCE(LOWER(sfl.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY sfl.name, sfl.code ASC ")
    Page<FutureLeader> getListFutureLeader(Long cid, String search, Pageable pageable);

}
