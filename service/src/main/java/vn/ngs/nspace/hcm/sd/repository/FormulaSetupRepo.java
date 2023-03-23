package vn.ngs.nspace.hcm.sd.repository;

import com.xdp.lib.repo.BaseRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.hcm.sd.entities.FormulaSetup;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FormulaSetupRepo extends BaseRepo<FormulaSetup, Long> {

    @Query(value = "SELECT sfs FROM FormulaSetup sfs" +
            "       WHERE sfs.companyId = :cid " +
            "           AND sfs.status = :status " +
            "           AND sfs.startDate <= :endDate " +
            "           AND sfs.endDate >= :startDate " +
            "           AND CONCAT(LOWER(COALESCE(sfs.code, '')), LOWER(COALESCE(sfs.name, ''))) LIKE LOWER(:search) ")
    Page<FormulaSetup> search(@Param("cid") Long cid,
                              @Param("status") Integer status,
                              @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate,
                              @Param("search") String search,
                              Pageable pageable);

    @Query(value = "SELECT sfs FROM FormulaSetup AS sfs " +
            "       WHERE sfs.companyId = :cid " +
            "           AND sfs.status = :status" +
            "           AND sfs.startDate <= :endDate " +
            "           AND sfs.endDate >= :startDate " +
            "           AND sfs.code = :code " +
            "           AND (sfs.id <> :id OR -1 = :id) " +
            "       ORDER BY sfs.startDate ")
    List<FormulaSetup> getByCodeAndEffectiveDate(Long cid, Long id, Integer status, String code, Date startDate, Date endDate);

    Optional<FormulaSetup> findByCompanyIdAndId(Long cid, Long id);

    Optional<FormulaSetup> findByCompanyIdAndStatusAndId(Long cid, Integer status, Long id);

    List<FormulaSetup> findAllByCompanyIdAndStatusAndIdIn(Long cid, Integer status, List<Long> ids);

}
