package com.test.backend.service;

import com.test.backend.dto.UserDTO;
import com.test.backend.entity.User;
import com.test.backend.repository.UserRepository;
import com.test.backend.repository.GreetingRepository;
import com.test.backend.repository.TimelineItemRepository;
import com.test.backend.utils.EntityDTOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GreetingRepository greetingRepository;
    @Autowired
    private TimelineItemRepository timelineItemRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !Boolean.FALSE.equals(user.getActive()))
                .map(user -> EntityDTOMapper.toDTO(user, UserDTO.class))
                .sorted(Comparator.comparingInt(UserDTO::getPrecedence))
                .toList();
    }

    public List<UserDTO> getAllUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(user -> EntityDTOMapper.toDTO(user, UserDTO.class))
                .sorted(Comparator.comparingInt(UserDTO::getPrecedence))
                .toList();
    }

    public UserDTO addUser(UserDTO userDTO) {
        if (userDTO.getActive() == null) {
            userDTO.setActive(true);
        }
        return EntityDTOMapper.toDTO(userRepository.save(EntityDTOMapper.toEntity(userDTO, User.class)), UserDTO.class);
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean wasActive = !Boolean.FALSE.equals(user.getActive());
        BeanUtils.copyProperties(userDTO, user, "id");
        user.setId(id);
        if (user.getActive() == null) {
            user.setActive(true);
        }
        boolean isActive = !Boolean.FALSE.equals(user.getActive());
        if (wasActive != isActive) {
            setRelatedContentActive(id, isActive);
        }
        return EntityDTOMapper.toDTO(userRepository.save(user), UserDTO.class);
    }

    public void archiveUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (Boolean.FALSE.equals(user.getActive())) {
            return;
        }
        user.setActive(false);
        userRepository.save(user);
        setRelatedContentActive(id, false);
    }

    private void setRelatedContentActive(String userId, boolean active) {
        var greetings = greetingRepository.findByUserId(userId);
        greetings.forEach(greeting -> greeting.setActive(active));
        greetingRepository.saveAll(greetings);
        var timelineItems = timelineItemRepository.findByUserIdOrderByEventDateDesc(userId);
        timelineItems
                .forEach(item -> item.setActive(active));
        timelineItemRepository.saveAll(timelineItems);
    }
}
