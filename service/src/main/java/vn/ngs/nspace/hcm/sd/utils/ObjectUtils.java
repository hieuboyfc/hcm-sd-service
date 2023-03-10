package vn.ngs.nspace.hcm.sd.utils;

import camundajar.impl.com.google.gson.Gson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUtils extends org.springframework.util.ObjectUtils {

    public static boolean compareObject(Object newObject, Object oldObject, String... ignore) {
        boolean result = true;
        Map<String, Object> newMap = objectToMap(newObject);
        Map<String, Object> oldMap = objectToMap(oldObject);
        List<String> keyIgnores = Arrays.asList(ignore);

        for (Map.Entry<String, Object> element : newMap.entrySet()) {
            String key = element.getKey();
            Object value = element.getValue();
            if (keyIgnores.contains(key)) {
                continue;
            }
            for (Map.Entry<String, Object> oldElement : oldMap.entrySet()) {
                String oldKey = oldElement.getKey();
                if (keyIgnores.contains(oldKey)) continue;
                if (key.equals(oldKey)) {
                    Object newValue = oldElement.getValue();
                    if (!value.equals(newValue)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static Map<String, Object> objectToMap(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        Map<String, Object> result = new HashMap<>();
        try {
            result = new ObjectMapper().readValue(json, HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
