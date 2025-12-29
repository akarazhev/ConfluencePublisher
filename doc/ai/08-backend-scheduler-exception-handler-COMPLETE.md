# Step 08: Backend Scheduler and Exception Handler - COMPLETE

## Overview

Successfully implemented the background scheduler for processing queued publications and the global exception handler for consistent error responses.

## Components Created

### 1. PageScheduler

**Location**: `backend/src/main/java/com/confluence/publisher/scheduler/PageScheduler.java`

**Implementation Details**:
- Annotated with `@Component` for Spring component scanning
- Uses `@Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")` to run at configured intervals
- Processes queued schedules where `scheduledAt <= now`
- For each schedule:
  - Calls `publishService.publishPage(pageId)`
  - On success: updates status to "posted" with no error
  - On failure: updates status to "failed" with error message
- Comprehensive logging at INFO and DEBUG levels
- Handles exceptions gracefully to prevent scheduler from stopping

**Dependencies**:
- ScheduleService - to find and update schedules
- PublishService - to publish pages
- AppProperties - to get scheduler interval configuration

### 2. GlobalExceptionHandler

**Location**: `backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java`

**Implementation Details**:
- Annotated with `@RestControllerAdvice` for global exception handling
- Logs all exceptions for debugging
- Three exception handlers:

#### RuntimeException Handler
- Returns 404 if message contains "not found" (case-insensitive)
- Returns 500 for all other runtime exceptions
- Response format: `{"detail": "<message>"}`

#### MethodArgumentNotValidException Handler
- Returns 400 for validation errors
- Extracts field-level error messages
- Response format: `{"errors": {"field1": "message1", "field2": "message2"}}`

#### Generic Exception Handler
- Returns 500 for unexpected exceptions
- Does not expose internal details
- Response format: `{"detail": "Internal server error"}`

## Scheduler Flow

```
Every N seconds (configured via app.schedulerIntervalSeconds):
1. Query schedules: status='queued' AND scheduledAt <= NOW
2. For each schedule:
   - Call publishService.publishPage(pageId)
   - Check PublishLog status
   - Update schedule:
     * success → status='posted', attemptCount++
     * error → status='failed', attemptCount++, lastError=message
3. Log processing results
```

## Error Response Formats

### Validation Error (400)
```json
{
  "errors": {
    "title": "Title is required",
    "content": "Content is required"
  }
}
```

### Not Found (404)
```json
{
  "detail": "Page not found: 123"
}
```

### Server Error (500)
```json
{
  "detail": "Internal server error"
}
```

## Configuration

### Scheduler Enabled
- `@EnableScheduling` is present in `ConfluencePublisherApplication.java` (line 10)
- Scheduler interval configured via `app.schedulerIntervalSeconds` property (default: 5 seconds)

### Application Properties
From `AppProperties.java`:
- `schedulerIntervalSeconds: 5` - default interval for scheduler

## Verification Results

✅ **Build Status**: SUCCESS
- Project compiles without errors
- All dependencies resolved
- No linter errors

✅ **Scheduler Configuration**: VERIFIED
- `@EnableScheduling` annotation present in main application class
- `@Scheduled` annotation properly configured with SpEL expression
- Uses `fixedDelay` strategy (waits for previous execution to complete)

✅ **Exception Handling**: IMPLEMENTED
- Global exception handler registered via `@RestControllerAdvice`
- Three exception types handled: RuntimeException, MethodArgumentNotValidException, Exception
- Proper HTTP status codes mapped (400, 404, 500)
- Field-level validation errors extracted correctly
- Not-found detection via message content

✅ **Integration**:
- PageScheduler has access to required services
- ScheduleService provides `findQueuedSchedules()` method
- PublishService returns PublishLog with status
- Schedule entity has status, attemptCount, and lastError fields

## Key Features

1. **Automatic Processing**: Scheduler runs continuously at configured intervals
2. **Robust Error Handling**: Exceptions don't stop the scheduler
3. **Detailed Logging**: All operations logged with appropriate levels
4. **Status Tracking**: Schedules track attempt count and last error
5. **Consistent Responses**: All API errors follow standard format
6. **Smart 404 Detection**: Runtime exceptions with "not found" in message return 404
7. **Validation Support**: Field-level validation errors properly formatted

## Testing Recommendations

To verify the implementation:

1. **Scheduler Test**:
   ```bash
   # Create a page
   POST /api/pages {"title": "Test", "content": "Test", "spaceKey": "DEV"}
   
   # Schedule it for immediate posting
   POST /api/schedules {"pageId": 1, "scheduledAt": "2025-12-29T12:00:00Z"}
   
   # Wait for scheduler interval (5 seconds by default)
   # Check schedule status
   GET /api/schedules
   
   # Verify status changed from "queued" to "posted" or "failed"
   ```

2. **Exception Handler Test**:
   ```bash
   # Test 404 - not found
   GET /api/pages/999999
   # Should return: {"detail": "Page not found: 999999"} with 404
   
   # Test 400 - validation error (requires validation annotations on DTOs)
   POST /api/pages {"title": "", "content": ""}
   # Should return: {"errors": {"title": "...", "content": "..."}} with 400
   
   # Test 500 - server error
   # Trigger any runtime exception
   # Should return: {"detail": "<error message>"} with 500
   ```

## Files Modified/Created

### Created:
- `backend/src/main/java/com/confluence/publisher/scheduler/PageScheduler.java`
- `backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java`

### No modifications needed:
- ConfluencePublisherApplication.java already has `@EnableScheduling`
- AppProperties.java already has `schedulerIntervalSeconds` property
- ScheduleService already has `findQueuedSchedules()` and `updateScheduleStatus()` methods
- PublishService already returns PublishLog with status

## Next Steps

The backend scheduler and exception handler are complete. Possible next steps:

1. **Frontend Development**: Implement Angular components (Steps 09-12)
2. **Docker Deployment**: Containerize the application (Step 13)
3. **Testing**: Add unit and integration tests
4. **Monitoring**: Add scheduler metrics and health checks
5. **Configuration**: Add ability to disable/enable scheduler via properties

## Notes

- Scheduler uses `fixedDelay` which waits for the previous execution to complete before starting the next one
- This prevents overlapping executions if publishing takes longer than the interval
- Error messages are preserved in the `lastError` field for debugging
- The scheduler will continue running even if individual schedules fail
- Exception handler logs all errors for debugging while providing safe responses to clients

