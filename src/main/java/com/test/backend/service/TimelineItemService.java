package com.test.backend.service;

import java.util.List;

import com.test.backend.dto.TimelineItemDTO;
import com.test.backend.entity.TimelineItem;
import com.test.backend.repository.TimelineItemRepository;
import com.test.backend.utils.EntityDTOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TimelineItemService {

    @Autowired
    private TimelineItemRepository timelineItemRepository;

    public List<TimelineItemDTO> getAllTimelineItems() {
        return timelineItemRepository.findAll().stream()
                .filter(item -> !Boolean.FALSE.equals(item.getActive()))
                .map(this::toDTO)
                .toList();
    }

    public List<TimelineItemDTO> getAllTimelineItemsForAdmin() {
        return timelineItemRepository.findAll().stream().map(this::toDTO).toList();
    }

    public TimelineItemDTO getTimelineItemById(String id) {
        return toDTO(findTimelineItem(id));
    }

    public List<TimelineItemDTO> getTimelineItemsByUserId(String userId) {
        return timelineItemRepository.findByUserIdOrderByEventDateDesc(userId).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getActive()))
                .map(this::toDTO)
                .toList();
    }

    public TimelineItemDTO createTimelineItem(TimelineItemDTO timelineItemDTO) {
        return toDTO(timelineItemRepository.save(toEntity(timelineItemDTO)));
    }

    public List<TimelineItemDTO> createTimelineItems(List<TimelineItemDTO> timelineItemDTOs) {
        List<TimelineItem> timelineItems = timelineItemDTOs.stream()
                .map(this::toEntity)
                .toList();

        return timelineItemRepository.saveAll(timelineItems).stream()
                .map(this::toDTO)
                .toList();
    }

    public TimelineItemDTO updateTimelineItem(String id, TimelineItemDTO timelineItemDTO) {
        TimelineItem timelineItem = findTimelineItem(id);
        BeanUtils.copyProperties(timelineItemDTO, timelineItem, "id");
        timelineItem.setId(id);
        return toDTO(timelineItemRepository.save(timelineItem));
    }

    public void deleteTimelineItem(String id) {
        TimelineItem timelineItem = findTimelineItem(id);
        timelineItem.setActive(false);
        timelineItemRepository.save(timelineItem);
    }

    private TimelineItem findTimelineItem(String id) {
        return timelineItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Timeline item not found"));
    }

    private TimelineItemDTO toDTO(TimelineItem timelineItem) {
        return EntityDTOMapper.toDTO(timelineItem, TimelineItemDTO.class);
    }

    private TimelineItem toEntity(TimelineItemDTO timelineItemDTO) {
        return EntityDTOMapper.toEntity(timelineItemDTO, TimelineItem.class);
    }
}
