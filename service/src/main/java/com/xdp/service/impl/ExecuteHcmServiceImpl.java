package com.xdp.service.impl;

import com.xdp.lib.dto.BaseResponse;
import com.xdp.service.ExecuteHcmService;
import com.xdp.dto.PositionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExecuteHcmServiceImpl implements ExecuteHcmService {

    @Value("${nspace.service.hcm.URL:nothing}")
    public String HcmServiceURL;

    @Value("${nspace.service.hcm.key:nothing}")
    public String HcmServiceKey;

    private HttpHeaders createHeader(String uid, Long cid) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", HcmServiceKey);
        headers.set("uid", uid);
        headers.set("cid", String.valueOf(cid));
        return headers;
    }

    @Override
    public List<PositionDTO> getDataPositionAndEmployee(Long cid, String uid, Map<String, Object> filter) {
        try {
            if (!filter.isEmpty()) {
                URI uri = new URI(HcmServiceURL + "/position-category/by-career-path");
                HttpMethod method = HttpMethod.POST;
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = createHeader(uid, cid);
                var request = new HttpEntity<>(filter, headers);

                ParameterizedTypeReference<BaseResponse<List<PositionDTO>>> responseType = new ParameterizedTypeReference<>() {
                };
                var response = restTemplate.exchange(uri, method, request, responseType);
                var resp = response.getBody();
                assert resp != null;
                return resp.getData();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
