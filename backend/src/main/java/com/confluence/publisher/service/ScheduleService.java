package com.confluence.publisher.service;

import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule createSchedule(Long pageId, Instant scheduledAt) {
        log.info("Creating schedule for page ID: {}", pageId);
        
        Instant scheduleTime = scheduledAt != null ? scheduledAt : Instant.now();
        
        Schedule schedule = Schedule.builder()
                .pageId(pageId)
                .scheduledAt(scheduleTime)
                .status("queued")
                .attemptCount(0)
                .build();
        
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Schedule created with ID: {}", savedSchedule.getId());
        
        return savedSchedule;
    }

    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId) {
        log.info("Fetching schedule with ID: {}", scheduleId);
        
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + scheduleId));
    }

    @Transactional(readOnly = true)
    public List<Schedule> listSchedules(int limit) {
        log.info("Listing schedules with limit: {}", limit);
        
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        return scheduleRepository.findAll(pageRequest).getContent();
    }

    @Transactional(readOnly = true)
    public List<Schedule> findQueuedSchedules(Instant now) {
        log.debug("Finding queued schedules up to: {}", now);
        
        return scheduleRepository.findByStatusAndScheduledAtLessThanEqual("queued", now);
    }

    @Transactional
    public void updateScheduleStatus(Schedule schedule, String status, String error) {
        log.info("Updating schedule {} status to: {}", schedule.getId(), status);
        
        schedule.setStatus(status);
        schedule.setAttemptCount(schedule.getAttemptCount() + 1);
        schedule.setLastError(error);
        
        scheduleRepository.save(schedule);
    }
}

