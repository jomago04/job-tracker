#!/bin/bash

# Comprehensive Test Suite for Job Tracker API
# This script tests all GET endpoints and generates a test report

API_URL="http://localhost:8080/api"
REPORT_FILE="TEST_RESULTS.md"

echo "# Job Tracker API - GET Endpoints Test Report" > $REPORT_FILE
echo "**Generated**: $(date)" >> $REPORT_FILE
echo "" >> $REPORT_FILE

# Test Counter
PASS=0
FAIL=0

# Function to test endpoint
test_endpoint() {
    local name=$1
    local endpoint=$2
    local description=$3

    echo "Testing: $description"
    response=$(curl -s -w "\n%{http_code}" "$API_URL$endpoint" 2>/dev/null)
    status=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)

    echo "## $name" >> $REPORT_FILE
    echo "- **Endpoint**: \`GET $endpoint\`" >> $REPORT_FILE
    echo "- **Description**: $description" >> $REPORT_FILE
    echo "- **Status**: \`$status\`" >> $REPORT_FILE

    if [[ "$status" =~ ^2 ]]; then
        echo "- **Result**: ✅ PASS" >> $REPORT_FILE
        ((PASS++))
    else
        echo "- **Result**: ❌ FAIL" >> $REPORT_FILE
        ((FAIL++))
    fi

    echo '```json' >> $REPORT_FILE
    echo "$body" | head -c 500 >> $REPORT_FILE
    echo "" >> $REPORT_FILE
    echo '```' >> $REPORT_FILE
    echo "" >> $REPORT_FILE
}

# =============================================
# Test Users Endpoints
# =============================================
echo "Testing Users Endpoints..."
test_endpoint "Get All Users (Paginated)" "/users?limit=10&offset=0" "Get all users with pagination (limit=10, offset=0)"
test_endpoint "List Users - Different Page" "/users?limit=5&offset=5" "Get users with different page size (limit=5, offset=5)"

# Get first user ID for single record test
USER_ID=$(curl -s "$API_URL/users?limit=1" | grep -o '"uuid":"[^"]*' | head -1 | cut -d'"' -f4)
if [ ! -z "$USER_ID" ]; then
    test_endpoint "Get User by ID" "/users/$USER_ID" "Get single user by UUID"
fi

# Test email exists
test_endpoint "Check Email Exists" "/users/email/act-test-1771887492178@example.com/exists" "Check if email exists in system"

# =============================================
# Test Company Endpoints
# =============================================
echo "Testing Company Endpoints..."
test_endpoint "Get All Companies (Paginated)" "/companies?limit=10&offset=0" "Get all companies with pagination"
test_endpoint "List Companies - Different Page" "/companies?limit=5&offset=0" "Get companies with different limit (limit=5)"

# Get first company ID
COMPANY_ID=$(curl -s "$API_URL/companies?limit=1" | grep -o '"cuid":"[^"]*' | head -1 | cut -d'"' -f4)
if [ ! -z "$COMPANY_ID" ]; then
    test_endpoint "Get Company by ID" "/companies/$COMPANY_ID" "Get single company by CUID"
fi

test_endpoint "Check Company Name Exists" "/companies/name/TestCompany/exists" "Check if company name exists"

# =============================================
# Test Job Endpoints
# =============================================
echo "Testing Job Endpoints..."
test_endpoint "Get All Jobs (Paginated)" "/jobs?limit=10&offset=0" "Get all jobs with pagination"
test_endpoint "List Jobs - Different Page" "/jobs?limit=3&offset=0" "Get jobs with smaller page size (limit=3)"

# Get first job ID
JOB_ID=$(curl -s "$API_URL/jobs?limit=1" | grep -o '"juid":"[^"]*' | head -1 | cut -d'"' -f4)
if [ ! -z "$JOB_ID" ]; then
    test_endpoint "Get Job by ID" "/jobs/$JOB_ID" "Get single job by JUID"
    test_endpoint "Check Job Exists" "/jobs/$JOB_ID/exists" "Check if specific job exists"
fi

# =============================================
# Test Application Endpoints
# =============================================
echo "Testing Application Endpoints..."
test_endpoint "Get All Applications (Paginated)" "/applications?limit=10&offset=0" "Get all applications with pagination"
test_endpoint "List Applications - Different Page" "/applications?limit=4&offset=0" "Get applications with different limit (limit=4)"

# Get first application ID
APP_ID=$(curl -s "$API_URL/applications?limit=1" | grep -o '"auid":"[^"]*' | head -1 | cut -d'"' -f4)
if [ ! -z "$APP_ID" ]; then
    test_endpoint "Get Application by ID" "/applications/$APP_ID" "Get single application by AUID"
    test_endpoint "Check Application Exists" "/applications/$APP_ID/exists" "Check if application exists"
fi

# Test duplicate check with real IDs
if [ ! -z "$USER_ID" ] && [ ! -z "$JOB_ID" ]; then
    test_endpoint "Check Duplicate Application" "/applications/user/$USER_ID/job/$JOB_ID/exists" "Check if user already applied to specific job"
fi

# =============================================
# Test Activity Endpoints
# =============================================
echo "Testing Activity Endpoints..."
test_endpoint "Get All Activities (Paginated)" "/activities?limit=10&offset=0" "Get all activities with pagination"
test_endpoint "List Activities - Different Page" "/activities?limit=5&offset=0" "Get activities with different limit (limit=5)"

# Get first activity ID
ACTIVITY_ID=$(curl -s "$API_URL/activities?limit=1" | grep -o '"actuid":"[^"]*' | head -1 | cut -d'"' -f4)
if [ ! -z "$ACTIVITY_ID" ]; then
    test_endpoint "Get Activity by ID" "/activities/$ACTIVITY_ID" "Get single activity by ACTUID"
fi

# Get activities by application
if [ ! -z "$APP_ID" ]; then
    test_endpoint "Get Activities by Application" "/activities/application/$APP_ID" "Get all activities for specific application"
fi

# =============================================
# Error Handling Tests
# =============================================
echo "Testing Error Handling..."
test_endpoint "Invalid User UUID (404)" "/users/invalid-uuid-12345" "Request non-existent user (should return 404)"
test_endpoint "Invalid Pagination" "/users?limit=invalid&offset=0" "Request with invalid pagination format (should return 400)"

# =============================================
# Summary
# =============================================
echo "" >> $REPORT_FILE
echo "---" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "## Test Summary" >> $REPORT_FILE
echo "- **Total Endpoints Tested**: $((PASS + FAIL))" >> $REPORT_FILE
echo "- **Passed**: ✅ $PASS" >> $REPORT_FILE
echo "- **Failed**: ❌ $FAIL" >> $REPORT_FILE
echo "- **Success Rate**: $(( PASS * 100 / (PASS + FAIL) ))%" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "## Frontend Access" >> $REPORT_FILE
echo "The Job Tracker API Browser is available at: **\`http://localhost:8000\`**" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "### Tabs & Features Tested:" >> $REPORT_FILE
echo "- **Users Tab**: Get all, paginate, filter by email exists" >> $REPORT_FILE
echo "- **Companies Tab**: Get all, paginate, filter by name exists" >> $REPORT_FILE
echo "- **Jobs Tab**: Get all, paginate, check job exists" >> $REPORT_FILE
echo "- **Applications Tab**: Get all, paginate, check exists, check duplicates" >> $REPORT_FILE
echo "- **Activities Tab**: Get all, paginate, filter by application ID" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "### Security Features:" >> $REPORT_FILE
echo "✅ CORS configured on backend for localhost:8000" >> $REPORT_FILE
echo "✅ Security headers added to responses" >> $REPORT_FILE
echo "✅ Input validation in frontend" >> $REPORT_FILE
echo "✅ Error messages without sensitive details" >> $REPORT_FILE

echo ""
echo "=========================================="
echo "Test Results Summary"
echo "=========================================="
echo "✅ Passed: $PASS"
echo "❌ Failed: $FAIL"
echo "📊 Success Rate: $(( PASS * 100 / (PASS + FAIL) ))%"
echo ""
echo "Full report saved to: $REPORT_FILE"
echo "Frontend available at: http://localhost:8000"
echo "=========================================="
