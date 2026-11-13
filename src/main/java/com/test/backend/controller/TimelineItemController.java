package com.test.backend.controller;

import java.util.List;

import com.test.backend.dto.TimelineItemDTO;
import com.test.backend.service.TimelineItemService;
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
@RequestMapping("/api/v1/timeline-items")
public class TimelineItemController {

    @Autowired
    private TimelineItemService timelineItemService;

    @GetMapping
    public ResponseEntity<List<TimelineItemDTO>> getAllTimelineItems() {
        return new ResponseEntity<>(timelineItemService.getAllTimelineItems(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimelineItemDTO> getTimelineItemById(@PathVariable String id) {
        return new ResponseEntity<>(timelineItemService.getTimelineItemById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TimelineItemDTO>> getTimelineItemsByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(timelineItemService.getTimelineItemsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TimelineItemDTO> createTimelineItem(@RequestBody TimelineItemDTO timelineItemDTO) {
        return new ResponseEntity<>(timelineItemService.createTimelineItem(timelineItemDTO), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<TimelineItemDTO>> createTimelineItems(@RequestBody List<TimelineItemDTO> timelineItemDTOs) {
        return new ResponseEntity<>(timelineItemService.createTimelineItems(timelineItemDTOs), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimelineItemDTO> updateTimelineItem(
            @PathVariable String id,
            @RequestBody TimelineItemDTO timelineItemDTO) {
        return new ResponseEntity<>(timelineItemService.updateTimelineItem(id, timelineItemDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimelineItem(@PathVariable String id) {
        timelineItemService.deleteTimelineItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
