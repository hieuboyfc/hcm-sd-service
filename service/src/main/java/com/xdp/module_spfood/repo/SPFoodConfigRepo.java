package com.xdp.module_spfood.repo;

import com.xdp.lib.repo.BaseRepo;
import com.xdp.module_spfood.entity.SPFoodConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPFoodConfigRepo extends BaseRepo<SPFoodConfig, Long> {

    List<SPFoodConfig> findAllByCompanyIdAndStatus(Long cid, Integer status);

}
