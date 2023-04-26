package com.xdp.module_spfood.service;

import java.util.Map;

public interface ExecuteUserService {

    Boolean updatePassword(Long cid, String uid, Map<String, Object> user);

}
