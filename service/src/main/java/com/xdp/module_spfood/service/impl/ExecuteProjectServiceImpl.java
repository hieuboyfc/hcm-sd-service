package com.xdp.module_spfood.service.impl;

import com.xdp.lib.dto.BaseResponse;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.module_spfood.service.ExecuteProjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class ExecuteProjectServiceImpl implements ExecuteProjectService {

    @Value("${nspace.service.project.URL:nothing}")
    public String projectServiceURL;

    @Value("${nspace.service.project.key:nothing}")
    public String projectServiceKey;

    protected HttpHeaders createHeader(Long cid, String uid) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", projectServiceKey);
        headers.set("cid", String.valueOf(cid));
        headers.set("uid", uid);
        return headers;
    }

    @Override
    public List<Map<String, Object>> getAllProject(Long cid, String uid) {
        try {
            URI uri = new URI(projectServiceURL + "/project/search?page=0&size=999&listShow=1&sort=id");
            HttpMethod method = HttpMethod.GET;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(cid, uid);
            var request = new HttpEntity<>(headers);

            ParameterizedTypeReference<BaseResponse<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
            };
            var response = restTemplate.exchange(uri, method, request, responseType);
            var resp = response.getBody();
            assert resp != null;
            return (List<Map<String, Object>>) resp.getData().get("content");
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllPhaseByProjectId(Long cid, String uid, Long projectId) {
        try {
            URI uri = new URI(projectServiceURL + "/project/unique-phases/" + projectId);
            HttpMethod method = HttpMethod.GET;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(cid, uid);
            var request = new HttpEntity<>(headers);

            ParameterizedTypeReference<BaseResponse<?>> responseType = new ParameterizedTypeReference<>() {
            };
            var response = restTemplate.exchange(uri, method, request, responseType);
            var resp = response.getBody();
            assert resp != null;
            return (List<Map<String, Object>>) resp.getData();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
