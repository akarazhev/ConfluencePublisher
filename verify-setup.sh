#!/bin/bash

# Confluence Publisher - Setup Verification Script
# This script verifies that all required files and configurations are in place

echo "🔍 Confluence Publisher - Setup Verification"
echo "=============================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Counter for checks
PASSED=0
FAILED=0

# Function to check if file exists
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        ((PASSED++))
    else
        echo -e "${RED}✗${NC} $1 (missing)"
        ((FAILED++))
    fi
}

# Function to check if directory exists
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        ((PASSED++))
    else
        echo -e "${RED}✗${NC} $1/ (missing)"
        ((FAILED++))
    fi
}

echo "📁 Checking Project Structure..."
echo "--------------------------------"
check_dir "backend"
check_dir "frontend"
check_dir "data"
check_dir "storage/attachments"
echo ""

echo "🔧 Checking Backend Files..."
echo "----------------------------"
check_file "backend/build.gradle.kts"
check_file "backend/settings.gradle.kts"
check_file "backend/gradlew"
check_file "backend/gradle/wrapper/gradle-wrapper.jar"
check_file "backend/gradle/wrapper/gradle-wrapper.properties"
check_file "backend/src/main/resources/application.yml"
check_file "backend/src/main/java/com/confluence/publisher/ConfluencePublisherApplication.java"
check_file "backend/Dockerfile"
check_file "backend/.dockerignore"
check_file "backend/.gitignore"
echo ""

echo "🎨 Checking Frontend Files..."
echo "-----------------------------"
check_file "frontend/package.json"
check_file "frontend/angular.json"
check_file "frontend/tailwind.config.js"
check_file "frontend/tsconfig.json"
check_file "frontend/tsconfig.app.json"
check_file "frontend/tsconfig.spec.json"
check_file "frontend/karma.conf.js"
check_file "frontend/nginx.conf"
check_file "frontend/src/index.html"
check_file "frontend/src/main.ts"
check_file "frontend/src/styles.css"
check_file "frontend/src/app/app.component.ts"
check_file "frontend/src/app/app.config.ts"
check_file "frontend/src/app/app.routes.ts"
check_file "frontend/src/app/pages/home/home.component.ts"
check_file "frontend/src/environments/environment.ts"
check_file "frontend/src/environments/environment.prod.ts"
check_file "frontend/Dockerfile"
check_file "frontend/.dockerignore"
check_file "frontend/.gitignore"
echo ""

echo "🐳 Checking Docker Files..."
echo "---------------------------"
check_file "docker-compose.yml"
echo ""

echo "⚙️  Checking Configuration Files..."
echo "-----------------------------------"
check_file ".gitignore"
check_file ".env.example"
check_file "README.md"
echo ""

echo "📊 Verification Summary"
echo "======================="
echo -e "Passed: ${GREEN}${PASSED}${NC}"
echo -e "Failed: ${RED}${FAILED}${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✅ All checks passed! Project setup is complete.${NC}"
    echo ""
    echo "🚀 Next Steps:"
    echo "   1. Copy .env.example to .env and configure"
    echo "   2. Run with: docker compose up --build"
    echo "   3. Access frontend: http://localhost:4200"
    echo "   4. Access backend: http://localhost:8080"
    exit 0
else
    echo -e "${RED}❌ Some checks failed. Please review the missing files.${NC}"
    exit 1
fi

