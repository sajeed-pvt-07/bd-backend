package com.test.backend.service;

import java.util.List;

import com.test.backend.dto.GreetingDTO;
import com.test.backend.entity.Greeting;
import com.test.backend.repository.GreetingRepository;
import com.test.backend.utils.EntityDTOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GreetingService {

    @Autowired
    private GreetingRepository greetingRepository;

    public List<GreetingDTO> getAllGreetings() {
        return greetingRepository.findAll().stream()
                .filter(greeting -> !Boolean.FALSE.equals(greeting.getActive()))
                .map(this::toDTO)
                .toList();
    }

    public List<GreetingDTO> getAllGreetingsForAdmin() {
        return greetingRepository.findAll().stream().map(this::toDTO).toList();
    }

    public GreetingDTO getGreetingById(String id) {
        return toDTO(findGreeting(id));
    }

    public GreetingDTO getGreetingByUserId(String userId) {
        return greetingRepository.findByUserId(userId).stream()
                .filter(greeting -> !Boolean.FALSE.equals(greeting.getActive()))
                .findFirst()
                .map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Greeting not found"));
    }

    public GreetingDTO createGreeting(GreetingDTO greetingDTO) {
        return toDTO(greetingRepository.save(toEntity(greetingDTO)));
    }

    public List<GreetingDTO> createGreetings(List<GreetingDTO> greetingDTOs) {
        List<Greeting> greetings = greetingDTOs.stream()
                .map(this::toEntity)
                .toList();

        return greetingRepository.saveAll(greetings).stream()
                .map(this::toDTO)
                .toList();
    }

    public GreetingDTO updateGreeting(String id, GreetingDTO greetingDTO) {
        Greeting greeting = findGreeting(id);
        BeanUtils.copyProperties(greetingDTO, greeting, "id");
        greeting.setId(id);
        return toDTO(greetingRepository.save(greeting));
    }

    public void deleteGreeting(String id) {
        Greeting greeting = findGreeting(id);
        greeting.setActive(false);
        greetingRepository.save(greeting);
    }

    private Greeting findGreeting(String id) {
        return greetingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Greeting not found"));
    }

    private GreetingDTO toDTO(Greeting greeting) {
        return EntityDTOMapper.toDTO(greeting, GreetingDTO.class);
    }

    private Greeting toEntity(GreetingDTO greetingDTO) {
        return EntityDTOMapper.toEntity(greetingDTO, Greeting.class);
    }
}
