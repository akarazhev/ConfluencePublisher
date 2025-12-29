#!/bin/bash

# Verification script for Step 08: Backend Scheduler and Exception Handler

echo "================================"
echo "Step 08 Verification"
echo "================================"
echo ""

# Check if scheduler component exists
echo "✓ Checking PageScheduler..."
if [ -f "backend/src/main/java/com/confluence/publisher/scheduler/PageScheduler.java" ]; then
    echo "  ✓ PageScheduler.java exists"
    
    # Check for @Scheduled annotation
    if grep -q "@Scheduled" backend/src/main/java/com/confluence/publisher/scheduler/PageScheduler.java; then
        echo "  ✓ @Scheduled annotation found"
    fi
    
    # Check for processScheduledPosts method
    if grep -q "processScheduledPosts" backend/src/main/java/com/confluence/publisher/scheduler/PageScheduler.java; then
        echo "  ✓ processScheduledPosts() method found"
    fi
else
    echo "  ✗ PageScheduler.java not found"
fi

echo ""

# Check if exception handler exists
echo "✓ Checking GlobalExceptionHandler..."
if [ -f "backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java" ]; then
    echo "  ✓ GlobalExceptionHandler.java exists"
    
    # Check for @RestControllerAdvice annotation
    if grep -q "@RestControllerAdvice" backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java; then
        echo "  ✓ @RestControllerAdvice annotation found"
    fi
    
    # Check for exception handlers
    if grep -q "handleRuntimeException" backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java; then
        echo "  ✓ RuntimeException handler found"
    fi
    
    if grep -q "handleValidationException" backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java; then
        echo "  ✓ MethodArgumentNotValidException handler found"
    fi
    
    if grep -q "handleGenericException" backend/src/main/java/com/confluence/publisher/exception/GlobalExceptionHandler.java; then
        echo "  ✓ Generic Exception handler found"
    fi
else
    echo "  ✗ GlobalExceptionHandler.java not found"
fi

echo ""

# Check if @EnableScheduling is present in main application
echo "✓ Checking Spring Scheduling Configuration..."
if grep -q "@EnableScheduling" backend/src/main/java/com/confluence/publisher/ConfluencePublisherApplication.java; then
    echo "  ✓ @EnableScheduling annotation found in main application"
else
    echo "  ✗ @EnableScheduling annotation not found"
fi

echo ""

# Check if schedulerIntervalSeconds is configured
echo "✓ Checking Scheduler Configuration..."
if grep -q "schedulerIntervalSeconds" backend/src/main/java/com/confluence/publisher/config/AppProperties.java; then
    echo "  ✓ schedulerIntervalSeconds property found in AppProperties"
else
    echo "  ✗ schedulerIntervalSeconds property not found"
fi

echo ""

# Try to build the project
echo "✓ Building project..."
cd backend
if JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null) ./gradlew build -x test > /dev/null 2>&1; then
    echo "  ✓ Build successful"
else
    echo "  ✗ Build failed - check for compilation errors"
fi
cd ..

echo ""
echo "================================"
echo "Verification Complete"
echo "================================"
echo ""
echo "Next steps:"
echo "1. Start the application: cd backend && ./gradlew bootRun"
echo "2. Test scheduler by creating a page and scheduling it"
echo "3. Test exception handler by triggering various errors"
echo "4. Monitor logs to see scheduler processing schedules"

