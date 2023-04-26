package com.xdp.module_spfood.service.impl;

import com.xdp.module_spfood.dto.SPFoodConfigDTO;
import com.xdp.module_spfood.entity.SPFoodConfig;
import com.xdp.module_spfood.repo.SPFoodConfigRepo;
import com.xdp.module_spfood.service.SPFoodConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SPFoodConfigServiceImpl implements SPFoodConfigService {

    private final SPFoodConfigRepo repo;

    @Override
    public List<SPFoodConfigDTO> initData(Long cid, String uid) {
        List<SPFoodConfig> listData = repo.findAllByCompanyIdAndStatus(cid, 1);
        if (!listData.isEmpty()) {
            return SPFoodConfig.toDTOs(listData);
        }
        return new ArrayList<>();
    }

}
