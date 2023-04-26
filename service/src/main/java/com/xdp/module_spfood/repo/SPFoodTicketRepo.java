package com.xdp.module_spfood.repo;

import com.xdp.lib.repo.BaseRepo;
import com.xdp.module_spfood.entity.SPFoodTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SPFoodTicketRepo extends BaseRepo<SPFoodTicket, Long> {

    @Query(value = "SELECT sft FROM SPFoodTicket AS sft " +
            "WHERE sft.companyId = :cid " +
            "   AND CONCAT(COALESCE(LOWER(sft.code), ''), COALESCE(LOWER(sft.name), '')) LIKE CONCAT('%', COALESCE(LOWER(:search), ''), '%') " +
            "ORDER BY sft.name, sft.code ASC ")
    Page<SPFoodTicket> getListSPFoodTicket(Long cid, String search, Pageable pageable);

    Optional<SPFoodTicket> findByCompanyIdAndCode(Long cid, String code);

    Optional<SPFoodTicket> findByCompanyIdAndId(Long cid, Long id);

    @Query(value = "SELECT  u.id AS id, " +
            "               u.avatar AS avatar, " +
            "               CONCAT(u.first_name, ' ', u.middle_name, ' ', u.last_name) AS fullName, " +
            "               e.id AS empId, " +
            "               e.emp_no AS empNo, " +
            "               e.full_name as empName " +
            "       FROM user_service.user u " +
            "       JOIN hcm_service.employee e ON u.id = e.user_mapping_id " +
            "       WHERE u.company_id = :cid AND e.company_id = :cid " +
            "           AND u.status = 1 AND e.status = 1 " +
            "           AND u.id IN (:userIds) ",
            nativeQuery = true)
    List<Map<String, Object>> getListUserBy(@Param("cid") Long cid,
                                            @Param("userIds") Set<String> userIds);

    @Query(value = "SELECT  u.id AS id, " +
            "               u.avatar AS avatar, " +
            "               u.email AS email, " +
            "               u.status AS status, " +
            "               CONCAT(u.first_name, ' ', u.middle_name, ' ', u.last_name) AS fullName " +
            "       FROM user_service.user u " +
            "       WHERE u.company_id = :cid AND u.email = :email ",
            nativeQuery = true)
    Map<String, Object> getUserByEmail(@Param("cid") Long cid,
                                       @Param("email") String email);
}
