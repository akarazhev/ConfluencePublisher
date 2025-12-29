package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ScheduleCreateRequest;
import com.confluence.publisher.dto.ScheduleResponse;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        Schedule schedule = scheduleService.createSchedule(request.getPageId(), request.getScheduledAt());
        return toResponse(schedule);
    }

    @GetMapping("/{scheduleId}")
    public ScheduleResponse getSchedule(@PathVariable Long scheduleId) {
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        return toResponse(schedule);
    }

    @GetMapping
    public List<ScheduleResponse> listSchedules() {
        // Return the 50 most recent schedules ordered by ID descending
        List<Schedule> schedules = scheduleService.listSchedules(50);
        return schedules.stream()
                .map(this::toResponse)
                .toList();
    }

    private ScheduleResponse toResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .pageId(schedule.getPageId())
                .status(schedule.getStatus())
                .scheduledAt(schedule.getScheduledAt())
                .attemptCount(schedule.getAttemptCount())
                .lastError(schedule.getLastError())
                .build();
    }
}

