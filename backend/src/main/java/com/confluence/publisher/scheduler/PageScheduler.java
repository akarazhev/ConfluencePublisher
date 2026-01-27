package com.confluence.publisher.scheduler;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.PublishLog;
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
    // appProperties is used in the @Scheduled annotation's SpEL expression
    @SuppressWarnings("unused")
    private final AppProperties appProperties;
    
    @Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")
    public void processScheduledPosts() {
        log.debug("Processing scheduled posts...");
        
        Instant now = Instant.now();
        List<Schedule> queuedSchedules = scheduleService.findQueuedSchedules(now);
        
        if (queuedSchedules.isEmpty()) {
            log.debug("No queued schedules to process");
            return;
        }
        
        log.info("Found {} queued schedule(s) to process", queuedSchedules.size());
        
        for (Schedule schedule : queuedSchedules) {
            try {
                log.info("Processing schedule ID: {} for page ID: {}", schedule.getId(), schedule.getPageId());
                
                // Try to publish the page via PublishService
                PublishLog publishLog = publishService.publishPage(schedule.getPageId());
                
                // Check if publish was successful
                if ("success".equals(publishLog.getStatus())) {
                    // On success: update status to "posted"
                    scheduleService.updateScheduleStatus(schedule, "posted", null);
                    log.info("Schedule ID: {} successfully posted to Confluence (page ID: {})", 
                            schedule.getId(), schedule.getPageId());
                } else {
                    // On failure: update status to "failed" with error message
                    String errorMessage = publishLog.getMessage() != null 
                            ? publishLog.getMessage() 
                            : "Publish failed with unknown error";
                    scheduleService.updateScheduleStatus(schedule, "failed", errorMessage);
                    log.warn("Schedule ID: {} failed to post (page ID: {}): {}", 
                            schedule.getId(), schedule.getPageId(), errorMessage);
                }
            } catch (Exception e) {
                // On exception: update status to "failed" with error message
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Unexpected error during publish";
                scheduleService.updateScheduleStatus(schedule, "failed", errorMessage);
                log.error("Error processing schedule ID: {} for page ID: {}", 
                        schedule.getId(), schedule.getPageId(), e);
            }
        }
        
        log.debug("Finished processing scheduled posts");
    }
}
