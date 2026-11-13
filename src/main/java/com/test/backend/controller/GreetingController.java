package com.test.backend.controller;

import java.util.List;

import com.test.backend.dto.GreetingDTO;
import com.test.backend.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingController {

    @Autowired
    private GreetingService greetingService;

    @GetMapping
    public ResponseEntity<List<GreetingDTO>> getAllGreetings() {
        return new ResponseEntity<>(greetingService.getAllGreetings(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GreetingDTO> getGreetingById(@PathVariable String id) {
        return new ResponseEntity<>(greetingService.getGreetingById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GreetingDTO> getGreetingByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(greetingService.getGreetingByUserId(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GreetingDTO> createGreeting(@RequestBody GreetingDTO greetingDTO) {
        return new ResponseEntity<>(greetingService.createGreeting(greetingDTO), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<GreetingDTO>> createGreetings(@RequestBody List<GreetingDTO> greetingDTOs) {
        return new ResponseEntity<>(greetingService.createGreetings(greetingDTOs), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GreetingDTO> updateGreeting(
            @PathVariable String id,
            @RequestBody GreetingDTO greetingDTO) {
        return new ResponseEntity<>(greetingService.updateGreeting(id, greetingDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGreeting(@PathVariable String id) {
        greetingService.deleteGreeting(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
