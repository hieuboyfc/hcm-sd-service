package com.xdp.module_spfood.service.impl;

import com.xdp.lib.dto.BaseResponse;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.module_spfood.dto.SPFoodConfigDTO;
import com.xdp.module_spfood.dto.SPFoodTicketDTO;
import com.xdp.module_spfood.response.FileResponse;
import com.xdp.module_spfood.service.ExecutePowerAutomateService;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExecutePowerAutomateServiceImpl implements ExecutePowerAutomateService {

    @Override
    public Boolean setupSoftware(SPFoodConfigDTO configDTO, SPFoodTicketDTO ticketDTO) {
        try {
            if (configDTO != null && configDTO.getStatus() != 1) {
                return true;
            }
            if (configDTO != null && ticketDTO != null) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("groupSetupId", ticketDTO.getGroupSetup() != null ? ticketDTO.getGroupSetup() : StringUtils.EMPTY);
                requestBody.put("email", ticketDTO.getEmail() != null ? ticketDTO.getEmail() : StringUtils.EMPTY);
                requestBody.put("typeSetup", ticketDTO.getHandleType() != null ? ticketDTO.getHandleType() : StringUtils.EMPTY);
                URI uri = new URI(configDTO.getLinkApi());
                HttpMethod method = HttpMethod.valueOf(configDTO.getMethod());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                var request = new HttpEntity<>(requestBody, headers);

                ParameterizedTypeReference<BaseResponse<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
                };
                var response = restTemplate.exchange(uri, method, request, responseType);
                var resp = response.getStatusCodeValue();
                if (resp == 200) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error Call API Method setupSoftware() of ExecutePowerAutomateServiceImpl.class: " + e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Boolean createOrUpdate(SPFoodConfigDTO configDTO, SPFoodTicketDTO ticketDTO) {
        try {
            if (configDTO != null && configDTO.getStatus() != 1) {
                return true;
            }
            if (configDTO != null && ticketDTO != null) {
                String pattern = "yyyy-MM-dd HH:MM:ss";
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("id", String.valueOf(ticketDTO.getId()));
                requestBody.put("code", ticketDTO.getCode());
                requestBody.put("name", ticketDTO.getName());
                requestBody.put("type", ticketDTO.getType());
                if (ticketDTO.getDescription() != null) {
                    requestBody.put("description", ticketDTO.getDescription());
                }
                if (ticketDTO.getSolution() != null) {
                    requestBody.put("solution", ticketDTO.getSolution());
                }
                if (ticketDTO.getEmail() != null) {
                    requestBody.put("email", ticketDTO.getEmail());
                }
                if (ticketDTO.getPhone() != null) {
                    requestBody.put("phone", ticketDTO.getPhone());
                }
                if (ticketDTO.getStartDate() != null) {
                    requestBody.put("startDate", convertDateToString(ticketDTO.getStartDate(), pattern));
                }
                if (ticketDTO.getResponseDeadline() != null) {
                    requestBody.put("responseDeadline", convertDateToString(ticketDTO.getResponseDeadline(), pattern));
                }
                if (ticketDTO.getDeadline() != null) {
                    requestBody.put("deadline", convertDateToString(ticketDTO.getDeadline(), pattern));
                }
                requestBody.put("project", ticketDTO.getProject());
                requestBody.put("phase", ticketDTO.getPhase());
                requestBody.put("requested", ticketDTO.getRequested());
                requestBody.put("responsible", ticketDTO.getResponsible());
                requestBody.put("statusName", ticketDTO.getStatusName());
                requestBody.put("createDate", convertDateToString(ticketDTO.getCreateDate(), pattern));
                requestBody.put("modifiedDate", convertDateToString(ticketDTO.getModifiedDate(), pattern));
                URI uri = new URI(configDTO.getLinkApi());
                HttpMethod method = HttpMethod.valueOf(configDTO.getMethod());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                var request = new HttpEntity<>(requestBody, headers);

                ParameterizedTypeReference<BaseResponse<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
                };
                var response = restTemplate.exchange(uri, method, request, responseType);
                var resp = response.getStatusCodeValue();
                if (resp == 200) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error Call API Method createOrUpdate() of ExecutePowerAutomateServiceImpl.class: " + e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Boolean delete(SPFoodConfigDTO configDTO, Long id) {
        try {
            if (configDTO != null && configDTO.getStatus() != 1) {
                return true;
            }
            if (configDTO != null && id != null) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("id", String.valueOf(id));
                URI uri = new URI(configDTO.getLinkApi());
                HttpMethod method = HttpMethod.valueOf(configDTO.getMethod());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                var request = new HttpEntity<>(requestBody, headers);

                ParameterizedTypeReference<BaseResponse<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
                };
                var response = restTemplate.exchange(uri, method, request, responseType);
                var resp = response.getStatusCodeValue();
                if (resp == 200) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error Call API Method delete() of ExecutePowerAutomateServiceImpl.class: " + e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public FileResponse getDataFromFile(SPFoodConfigDTO configDTO, MultipartFile file) {
        FileResponse fileResponse = new FileResponse();
        try {
            if (configDTO != null && !file.isEmpty()) {
                Resource fileResource = file.getResource();
                LinkedMultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
                requestBody.add("file", fileResource);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                URI uri = new URI(configDTO.getLinkApi());
                HttpMethod method = HttpMethod.valueOf(configDTO.getMethod());
                RestTemplate restTemplate = new RestTemplate();
                HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, headers);
                ParameterizedTypeReference<FileResponse> responseType = new ParameterizedTypeReference<>() {
                };
                var response = restTemplate.exchange(uri, method, request, responseType);
                if (response.getStatusCode().value() == 200) {
                    fileResponse = response.getBody();
                }
            }
        } catch (Exception e) {
            System.out.println("Error Call API Method getDataFromFile() of ExecutePowerAutomateServiceImpl.class: " + e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        return fileResponse;
    }

    private String convertDateToString(Date date, String pattern) {
        String result = StringUtils.EMPTY;
        try {
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            if (date == null || StringUtils.isEmpty(pattern)) {
                return result;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            result = dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
