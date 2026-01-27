package com.confluence.publisher.service;

import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    
    @Transactional
    public Schedule createSchedule(Long pageId, Instant scheduledAt) {
        log.info("Creating schedule for pageId: {}, scheduledAt: {}", pageId, scheduledAt);
        
        // Use current time if scheduledAt is null
        Instant scheduleTime = scheduledAt != null ? scheduledAt : Instant.now();
        
        // Create schedule with status "queued"
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
    
    public Schedule getSchedule(Long scheduleId) {
        log.debug("Getting schedule with ID: {}", scheduleId);
        
        // Find by ID or throw exception
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + scheduleId));
    }
    
    public List<Schedule> listSchedules(Integer limit) {
        log.debug("Listing schedules with limit: {}", limit);
        
        // Return the latest schedules sorted by ID descending, limited by the provided limit
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Schedule> schedules = scheduleRepository.findAll(sort);
        
        if (limit != null && limit > 0 && schedules.size() > limit) {
            return schedules.subList(0, limit);
        }
        
        return schedules;
    }
    
    public List<Schedule> findQueuedSchedules(Instant now) {
        log.debug("Finding queued schedules before: {}", now);
        
        // Find schedules with status "queued" and scheduledAt <= now
        return scheduleRepository.findQueuedSchedulesBefore(now);
    }
    
    @Transactional
    public void updateScheduleStatus(Schedule schedule, String status, String error) {
        log.info("Updating schedule ID: {} to status: {}, error: {}", schedule.getId(), status, error);
        
        // Update status, increment attemptCount, set lastError
        schedule.setStatus(status);
        schedule.setAttemptCount(schedule.getAttemptCount() + 1);
        schedule.setLastError(error);
        
        scheduleRepository.save(schedule);
        log.debug("Schedule updated successfully");
    }
}
