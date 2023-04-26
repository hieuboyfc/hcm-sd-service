package com.xdp.module_spfood.service.impl;

import com.xdp.lib.dto.BaseResponse;
import com.xdp.lib.exceptions.BusinessException;
import com.xdp.module_spfood.service.ExecuteUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Map;

@Service
@Component
public class ExecuteUserServiceImpl implements ExecuteUserService {

    @Value("${nspace.service.user.url:nothing}")
    public String userServiceURL;

    @Value("${nspace.service.user.key:nothing}")
    public String userServiceKey;

    protected HttpHeaders createHeader(Long cid, String uid) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", userServiceKey);
        headers.set("uid", uid);
        headers.set("cid", String.valueOf(cid));
        return headers;
    }

    @Override
    @Transactional
    public Boolean updatePassword(Long cid, String uid, Map<String, Object> user) {
        try {
            URI url = new URI(userServiceURL + "/user/updatePassword");
            HttpMethod method = HttpMethod.PUT;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(cid, uid);
            var request = new HttpEntity<>(user, headers);
            ParameterizedTypeReference<BaseResponse<?>> responseType = new ParameterizedTypeReference<>() {
            };
            var response = restTemplate.exchange(url, method, request, responseType);
            var resp = response.getStatusCodeValue();
            if (resp == 200) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error Call API Method updatePassword() of ExecuteUserServiceImpl.class: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

}
