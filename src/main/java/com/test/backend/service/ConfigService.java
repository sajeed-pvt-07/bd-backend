package com.test.backend.service;

import com.test.backend.dto.ConfigDTO;
import com.test.backend.entity.Config;
import com.test.backend.repository.ConfigRepository;
import com.test.backend.utils.EntityDTOMapper;
import com.test.backend.utils.ConfigTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConfigService {

    @Autowired
    private ConfigRepository configRepository;


    public List<ConfigDTO> getAllConfigs() {
        return configRepository.findAll().stream().map(config ->
             EntityDTOMapper.toDTO(config, ConfigDTO.class)
        ).toList();
    }

    /**
     * Get all configs as a single object with keys as properties
     * Example: {name: "ravi", age: "15"}
     */
    public Map<String, String> getAllConfigsAsObject() {
        List<ConfigDTO> configDTOs = getAllConfigs();
        log.info("Converting {} configs to single object", configDTOs.size());
        return ConfigTransformer.toSingleObject(configDTOs);
    }


    public ConfigDTO createConfig(ConfigDTO configDTO) {
        Config savedConfig = configRepository.save(toConfig(configDTO));
        return EntityDTOMapper.toDTO(savedConfig, ConfigDTO.class);
    }

    public void deleteConfig(String key) {
        if (configRepository.findFirstByKeyOrderByIdDesc(key).isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Config not found");
        }
        configRepository.deleteByKey(key);
    }

    public List<ConfigDTO> createConfigs(List<ConfigDTO> configDTOs) {
        List<Config> configs = configDTOs.stream()
                .map(this::toConfig)
                .toList();
        return configRepository.saveAll(configs).stream()
                .map(config -> EntityDTOMapper.toDTO(config, ConfigDTO.class))
                .toList();
    }

    private Config toConfig(ConfigDTO configDTO) {
        Config config = configRepository.findFirstByKeyOrderByIdDesc(configDTO.getKey())
                .orElseGet(Config::new);
        config.setKey(configDTO.getKey());
        config.setValue(configDTO.getValue());
        return config;
    }
}
