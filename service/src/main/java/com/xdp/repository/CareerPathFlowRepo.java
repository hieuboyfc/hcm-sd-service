package com.xdp.repository;

import com.xdp.lib.repo.BaseRepo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.xdp.entities.CareerPathFlow;

import java.util.List;

@Repository
public interface CareerPathFlowRepo extends BaseRepo<CareerPathFlow, Long> {

    @Query(value = "SELECT cpf FROM CareerPathFlow cpf " +
            "WHERE cpf.companyId = :cid " +
            "AND cpf.careerPathId = :careerPathId " +
            "AND (cpf.parentId = :parentId OR cpf.parentId IS NULL) " +
            "AND (cpf.level = 0 OR cpf.level IS NULL) ")
    CareerPathFlow getByCompanyIdAndParentId(@Param("cid") Long cid,
                                             @Param("careerPathId") Long careerPathId,
                                             @Param("parentId") Long parentId);

    @Query(value = "SELECT cpf.* FROM hcm_sd_service.career_path_flow cpf " +
            "WHERE cpf.company_id = :cid " +
            "AND cpf.path <@ CAST(" +
            "           (SELECT CAST(cpf2.id AS text) " +
            "           FROM hcm_sd_service.career_path_flow cpf2 " +
            "           WHERE cpf2.company_id = :cid " +
            "           AND cpf2.career_path_id = :careerPathId " +
            "           AND (cpf2.parent_id = 0 OR cpf2.parent_id IS NULL) " +
            "           AND (cpf2.level = 0 OR cpf2.level IS NULL)) " +
            "       AS ltree)",
            nativeQuery = true)
    List<CareerPathFlow> getChildByPath(@Param("cid") Long cid,
                                        @Param("careerPathId") Long careerPathId);

    List<CareerPathFlow> findAllByCompanyIdAndCareerPathIdOrderByLevelDesc(Long cid, Long careerPathId);

    List<CareerPathFlow> findAllByCompanyIdAndCareerPathId(Long cid, Long careerPathId);

    List<CareerPathFlow> findAllByCompanyIdAndCareerPathIdIn(Long cid, List<Long> ids);

}
