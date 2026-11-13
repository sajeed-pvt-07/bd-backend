package com.test.backend.utils;

import com.test.backend.dto.ConfigDTO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigTransformer {

    /**
     * Converts a list of ConfigDTO objects (key-value pairs) into a single Map object
     * Example: [{key:"name",value:"ravi"},{key:"age",value:"15"}]
     * Returns: {name: "ravi", age: "15"}
     *
     * @param configDTOs List of ConfigDTO objects with key-value pairs
     * @return Map with keys and values extracted from the list
     */
    public static Map<String, String> toSingleObject(List<ConfigDTO> configDTOs) {
        if (configDTOs == null || configDTOs.isEmpty()) {
            return Map.of();
        }

        return configDTOs.stream()
                .collect(Collectors.toMap(
                        ConfigDTO::getKey,
                        ConfigDTO::getValue,
                        (existing, replacement) -> replacement  // In case of duplicate keys, keep the latest value
                ));
    }

    /**
     * Alternative: Convert to a generic Map<String, Object> for flexibility
     */
    public static Map<String, Object> toSingleObjectGeneric(List<ConfigDTO> configDTOs) {
        if (configDTOs == null || configDTOs.isEmpty()) {
            return Map.of();
        }

        return configDTOs.stream()
                .collect(Collectors.toMap(
                        ConfigDTO::getKey,
                        ConfigDTO::getValue,
                        (existing, replacement) -> replacement
                ));
    }
}

