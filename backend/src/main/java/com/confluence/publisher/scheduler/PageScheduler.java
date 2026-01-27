package com.confluence.publisher.scheduler;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.PublishService;
import com.confluence.publisher.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PageScheduler {

    private final ScheduleService scheduleService;
    private final PublishService publishService;
    private final AppProperties appProperties;

    @Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")
    public void processScheduledPosts() {
        Instant now = Instant.now();
        List<Schedule> queuedSchedules = scheduleService.findQueuedSchedules(now);
        
        if (queuedSchedules.isEmpty()) {
            log.debug("No queued schedules to process");
            return;
        }
        
        log.info("Processing {} queued schedule(s)", queuedSchedules.size());
        
        for (Schedule schedule : queuedSchedules) {
            try {
                log.info("Publishing page ID: {} for schedule ID: {}", schedule.getPageId(), schedule.getId());
                publishService.publishPage(schedule.getPageId());
                
                // On success: update status to "posted"
                scheduleService.updateScheduleStatus(schedule, "posted", null);
                log.info("Successfully published page ID: {} for schedule ID: {}", schedule.getPageId(), schedule.getId());
                
            } catch (Exception e) {
                // On failure: update status to "failed" with error message
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                scheduleService.updateScheduleStatus(schedule, "failed", errorMessage);
                log.error("Failed to publish page ID: {} for schedule ID: {}. Error: {}", 
                        schedule.getPageId(), schedule.getId(), errorMessage, e);
            }
        }
        
        log.info("Completed processing {} schedule(s)", queuedSchedules.size());
    }
}
