package vn.ngs.nspace.hcm.sd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.ngs.nspace.hcm.sd.share.dto.HeirSetupDTO;

import java.util.Map;

public interface HeirSetupService {

    Map<String, Object> initSetup(Long cid, String uid);

    Map<String, Object> getDetail(Long cid, String uid, Long id);

    HeirSetupDTO update(Long cid, String uid, HeirSetupDTO dto) throws JsonProcessingException;

}
