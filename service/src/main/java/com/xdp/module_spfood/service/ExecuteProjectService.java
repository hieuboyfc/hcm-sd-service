package com.xdp.module_spfood.service;

import java.util.List;
import java.util.Map;

public interface ExecuteProjectService {

    List<Map<String, Object>> getAllProject(Long cid, String uid);

    List<Map<String, Object>> getAllPhaseByProjectId(Long cid, String uid, Long projectId);

}
