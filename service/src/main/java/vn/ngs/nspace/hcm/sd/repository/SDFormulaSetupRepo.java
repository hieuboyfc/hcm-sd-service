package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.SDFormulaSetup;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SDFormulaSetupRepo extends BaseRepo<SDFormulaSetup, Long> {

    @Query(value = "SELECT sfs FROM SDFormulaSetup sfs" +
            "       WHERE sfs.companyId = :cid " +
            "           AND sfs.status = :status " +
            "           AND sfs.startDate <= :endDate " +
            "           AND sfs.endDate >= :startDate " +
            "           AND CONCAT(LOWER(COALESCE(sfs.name, '')), LOWER(COALESCE(sfs.code, ''))) LIKE LOWER(:search) ")
    Page<SDFormulaSetup> search(@Param("cid") Long cid,
                                @Param("status") Integer status,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("search") String search,
                                Pageable pageable);

    @Query(value = "SELECT sfs FROM SDFormulaSetup AS sfs " +
            "       WHERE sfs.companyId = :cid " +
            "           AND sfs.status = :status" +
            "           AND sfs.startDate <= :endDate " +
            "           AND sfs.endDate >= :startDate " +
            "           AND sfs.code = :code " +
            "           AND (sfs.id <> :id OR -1 = :id) " +
            "       ORDER BY sfs.startDate ")
    List<SDFormulaSetup> getByCodeAndEffectiveDate(Long cid, Long id, Integer status, String code, Date startDate, Date endDate);

    Optional<SDFormulaSetup> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<SDFormulaSetup> findAllByCompanyIdAndStatusAndIdIn(Long cid, Integer status, List<Long> ids);
}
