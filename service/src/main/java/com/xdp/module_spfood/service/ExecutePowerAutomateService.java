package com.xdp.module_spfood.service;

import com.xdp.module_spfood.dto.SPFoodConfigDTO;
import com.xdp.module_spfood.dto.SPFoodTicketDTO;
import com.xdp.module_spfood.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ExecutePowerAutomateService {

    Boolean setupSoftware(SPFoodConfigDTO configDTO, SPFoodTicketDTO ticketDTO);

    Boolean createOrUpdate(SPFoodConfigDTO configDTO, SPFoodTicketDTO ticketDTO);

    Boolean delete(SPFoodConfigDTO configDTO, Long id);

    FileResponse getDataFromFile(SPFoodConfigDTO configDTO, MultipartFile file);

}
