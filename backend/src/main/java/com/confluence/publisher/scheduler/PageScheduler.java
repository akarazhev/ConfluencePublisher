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

@Component
@RequiredArgsConstructor
@Slf4j
public class PageScheduler {

    private final ScheduleService scheduleService;
    private final PublishService publishService;
    private final AppProperties appProperties;

    @Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")
    public void processScheduledPosts() {
        log.debug("Running scheduled post processor");
        
        Instant now = Instant.now();
        List<Schedule> queuedSchedules = scheduleService.findQueuedSchedules(now);
        
        if (queuedSchedules.isEmpty()) {
            log.debug("No queued schedules found");
            return;
        }
        
        log.info("Found {} queued schedule(s) to process", queuedSchedules.size());
        
        for (Schedule schedule : queuedSchedules) {
            try {
                log.info("Processing schedule ID: {} for page ID: {}", 
                        schedule.getId(), schedule.getPageId());
                
                PublishLog publishLog = publishService.publishPage(schedule.getPageId());
                
                if ("success".equals(publishLog.getStatus())) {
                    scheduleService.updateScheduleStatus(schedule, "posted", null);
                    log.info("Successfully published schedule ID: {}", schedule.getId());
                } else {
                    scheduleService.updateScheduleStatus(schedule, "failed", publishLog.getMessage());
                    log.error("Failed to publish schedule ID: {}, error: {}", 
                            schedule.getId(), publishLog.getMessage());
                }
                
            } catch (Exception e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                scheduleService.updateScheduleStatus(schedule, "failed", errorMessage);
                log.error("Exception while processing schedule ID: {}, error: {}", 
                        schedule.getId(), errorMessage, e);
            }
        }
        
        log.info("Completed processing {} schedule(s)", queuedSchedules.size());
    }
}

