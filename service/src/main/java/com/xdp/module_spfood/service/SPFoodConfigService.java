package com.xdp.module_spfood.service;

import com.xdp.module_spfood.dto.SPFoodConfigDTO;

import java.util.List;

public interface SPFoodConfigService {

    List<SPFoodConfigDTO> initData(Long cid, String uid);

}
