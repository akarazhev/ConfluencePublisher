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

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule createSchedule(Long pageId, Instant scheduledAt) {
        Instant scheduleTime = scheduledAt != null ? scheduledAt : Instant.now();
        
        Schedule schedule = Schedule.builder()
                .pageId(pageId)
                .scheduledAt(scheduleTime)
                .status("queued")
                .attemptCount(0)
                .build();
        
        Schedule saved = scheduleRepository.save(schedule);
        log.info("Created schedule with ID: {} for page ID: {}", saved.getId(), pageId);
        return saved;
    }

    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + scheduleId));
    }

    @Transactional(readOnly = true)
    public List<Schedule> listSchedules(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
        return scheduleRepository.findAll(pageRequest).getContent();
    }

    @Transactional(readOnly = true)
    public List<Schedule> findQueuedSchedules(Instant now) {
        return scheduleRepository.findQueuedSchedulesBefore(now);
    }

    @Transactional
    public void updateScheduleStatus(Schedule schedule, String status, String error) {
        schedule.setStatus(status);
        schedule.setAttemptCount(schedule.getAttemptCount() + 1);
        schedule.setLastError(error);
        scheduleRepository.save(schedule);
        log.info("Updated schedule ID: {} to status: {}", schedule.getId(), status);
    }
}
