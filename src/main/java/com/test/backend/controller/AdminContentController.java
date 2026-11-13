package com.test.backend.controller;

import com.test.backend.dto.ConfigDTO;
import com.test.backend.dto.GreetingDTO;
import com.test.backend.dto.SongDTO;
import com.test.backend.dto.TimelineItemDTO;
import com.test.backend.dto.UserDTO;
import com.test.backend.service.ConfigService;
import com.test.backend.service.GreetingService;
import com.test.backend.service.SongService;
import com.test.backend.service.TimelineItemService;
import com.test.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/content")
public class AdminContentController {

    private final ConfigService configService;
    private final UserService userService;
    private final GreetingService greetingService;
    private final TimelineItemService timelineItemService;
    private final SongService songService;

    public AdminContentController(
            ConfigService configService,
            UserService userService,
            GreetingService greetingService,
            TimelineItemService timelineItemService,
            SongService songService) {
        this.configService = configService;
        this.userService = userService;
        this.greetingService = greetingService;
        this.timelineItemService = timelineItemService;
        this.songService = songService;
    }

    @GetMapping("/configs")
    public List<ConfigDTO> getConfigs() {
        return configService.getAllConfigs();
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return userService.getAllUsersForAdmin();
    }

    @GetMapping("/greetings")
    public List<GreetingDTO> getGreetings() {
        return greetingService.getAllGreetingsForAdmin();
    }

    @GetMapping("/timeline-items")
    public List<TimelineItemDTO> getTimelineItems() {
        return timelineItemService.getAllTimelineItemsForAdmin();
    }

    @GetMapping("/songs")
    public List<SongDTO> getSongs() {
        return songService.getAllSongsForAdmin();
    }
}
