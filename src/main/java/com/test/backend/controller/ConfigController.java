package com.test.backend.controller;

import com.test.backend.dto.ConfigDTO;
import com.test.backend.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

//    @GetMapping
//    public ResponseEntity<List<ConfigDTO>> getConfig() {
//        return new ResponseEntity<>(configService.getAllConfigs(), HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getConfigAsObject() {
        return new ResponseEntity<>(configService.getAllConfigsAsObject(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ConfigDTO> createConfig(@RequestBody ConfigDTO configDTO) {
        return new ResponseEntity<>(configService.createConfig(configDTO), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ConfigDTO>> createConfigs(@RequestBody List<ConfigDTO> configDTOs) {
        return new ResponseEntity<>(configService.createConfigs(configDTOs), HttpStatus.CREATED);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String key) {
        configService.deleteConfig(key);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
