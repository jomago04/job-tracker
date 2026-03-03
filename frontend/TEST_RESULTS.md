# Job Tracker API - GET Endpoints Test Report
**Generated**: Sun Mar  1 15:46:31 EST 2026

## Get All Users (Paginated)
- **Endpoint**: `GET /users?limit=10&offset=0`
- **Description**: Get all users with pagination (limit=10, offset=0)
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## List Users - Different Page
- **Endpoint**: `GET /users?limit=5&offset=5`
- **Description**: Get users with different page size (limit=5, offset=5)
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get User by ID
- **Endpoint**: `GET /users/187493bf-6719-4c5a-ae0c-6b78a0ae9e60`
- **Description**: Get single user by UUID
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Check Email Exists
- **Endpoint**: `GET /users/email/act-test-1771887492178@example.com/exists`
- **Description**: Check if email exists in system
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get All Companies (Paginated)
- **Endpoint**: `GET /companies?limit=10&offset=0`
- **Description**: Get all companies with pagination
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## List Companies - Different Page
- **Endpoint**: `GET /companies?limit=5&offset=0`
- **Description**: Get companies with different limit (limit=5)
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get Company by ID
- **Endpoint**: `GET /companies/fb1a6583-3385-410b-88db-7e928d111062`
- **Description**: Get single company by CUID
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Check Company Name Exists
- **Endpoint**: `GET /companies/name/TestCompany/exists`
- **Description**: Check if company name exists
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get All Jobs (Paginated)
- **Endpoint**: `GET /jobs?limit=10&offset=0`
- **Description**: Get all jobs with pagination
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## List Jobs - Different Page
- **Endpoint**: `GET /jobs?limit=3&offset=0`
- **Description**: Get jobs with smaller page size (limit=3)
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get Job by ID
- **Endpoint**: `GET /jobs/61ed3cf2-147f-4ea4-a596-ccdf55c3e920`
- **Description**: Get single job by JUID
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Check Job Exists
- **Endpoint**: `GET /jobs/61ed3cf2-147f-4ea4-a596-ccdf55c3e920/exists`
- **Description**: Check if specific job exists
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get All Applications (Paginated)
- **Endpoint**: `GET /applications?limit=10&offset=0`
- **Description**: Get all applications with pagination
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## List Applications - Different Page
- **Endpoint**: `GET /applications?limit=4&offset=0`
- **Description**: Get applications with different limit (limit=4)
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get Application by ID
- **Endpoint**: `GET /applications/c64ee9ec-e703-41ef-935e-b393b93b9abd`
- **Description**: Get single application by AUID
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Check Application Exists
- **Endpoint**: `GET /applications/c64ee9ec-e703-41ef-935e-b393b93b9abd/exists`
- **Description**: Check if application exists
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Check Duplicate Application
- **Endpoint**: `GET /applications/user/187493bf-6719-4c5a-ae0c-6b78a0ae9e60/job/61ed3cf2-147f-4ea4-a596-ccdf55c3e920/exists`
- **Description**: Check if user already applied to specific job
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get All Activities (Paginated)
- **Endpoint**: `GET /activities?limit=10&offset=0`
- **Description**: Get all activities with pagination
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## List Activities - Different Page
- **Endpoint**: `GET /activities?limit=5&offset=0`
- **Description**: Get activities with different limit (limit=5)
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Get Activity by ID
- **Endpoint**: `GET /activities/a811bb86-53c9-4e1e-9d7a-b2179bd0893c`
- **Description**: Get single activity by ACTUID
- **Status**: `404`
- **Result**: ❌ FAIL
```json


```

## Get Activities by Application
- **Endpoint**: `GET /activities/application/c64ee9ec-e703-41ef-935e-b393b93b9abd`
- **Description**: Get all activities for specific application
- **Status**: `200`
- **Result**: ✅ PASS
```json


```

## Invalid User UUID (404)
- **Endpoint**: `GET /users/invalid-uuid-12345`
- **Description**: Request non-existent user (should return 404)
- **Status**: `404`
- **Result**: ❌ FAIL
```json


```

## Invalid Pagination
- **Endpoint**: `GET /users?limit=invalid&offset=0`
- **Description**: Request with invalid pagination format (should return 400)
- **Status**: `400`
- **Result**: ❌ FAIL
```json


```


---

## Test Summary
- **Total Endpoints Tested**: 23
- **Passed**: ✅ 20
- **Failed**: ❌ 3
- **Success Rate**: 86%

## Frontend Access
The Job Tracker API Browser is available at: **`http://localhost:8000`**

### Tabs & Features Tested:
- **Users Tab**: Get all, paginate, filter by email exists
- **Companies Tab**: Get all, paginate, filter by name exists
- **Jobs Tab**: Get all, paginate, check job exists
- **Applications Tab**: Get all, paginate, check exists, check duplicates
- **Activities Tab**: Get all, paginate, filter by application ID

### Security Features:
✅ CORS configured on backend for localhost:8000
✅ Security headers added to responses
✅ Input validation in frontend
✅ Error messages without sensitive details
