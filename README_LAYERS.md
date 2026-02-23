# Job Tracker - Service & Business Layers

quick overview of how the backend is set up for anyone reading through the code

## Business Layer

this is basically the middle-man between the REST API and the database. all the actual logic lives here.

there are 5 manager classes in `jobtracker.business`:

- **UserManager** - handles creating users (generates a UUID, hashes the password), looking them up, listing them, deleting them, and checking if an email is already taken
- **CompanyManager** - same idea but for companies. create, get, list, delete, and check if a company name already exists
- **JobManager** - manages job postings tied to companies. create, get by id, list all, delete, and check if a job exists
- **ApplicationManager** - this one does the most. creates applications (links a user to a job), gets/lists/deletes them, plus you can update the status, notes, and source separately. also checks for duplicate applications so a user cant apply to the same job twice
- **ActivityManager** - tracks events on applications (like status changes, notes added, interviews scheduled, etc). you can get activities by application, list all, get one by id, or update the details

all of these talk to the database through `ReportDaoJdbc` which just runs raw SQL queries with JDBC. nothing fancy, no ORM.

## Service Layer

this is the REST API layer built with **Spark Java**. it runs on port 8080 and exposes a bunch of endpoints.

the main file is `JobTrackerRestService.java` and it sets up all the routes:

| resource | endpoints |
|----------|-----------|
| users | POST, GET by id, GET all, DELETE, check email exists |
| companies | POST, GET by id, GET all, DELETE, check name exists |
| jobs | POST, GET by id, GET all, DELETE, check exists |
| applications | POST, GET by id, GET all, DELETE, PUT status, PUT notes, PUT source, check exists, check user+job combo exists |
| activities | GET by application, GET all, GET by id, PUT details |

theres also a few helper classes:
- **ResponseBuilder** - converts objects to JSON with Gson and wraps responses in a consistent format (success/error with messages and codes)
- **ErrorHandler** - handles setting the right HTTP status codes (400, 404, 409, 500) and returning error JSON
- **UserService** - a simpler service class for basic user operations (register, find by email, list). uses the DAO layer directly instead of going through a manager

everything returns JSON. the API does input validation (checks for missing fields, duplicates, etc) and uses the business layer managers to do the actual work. pretty standard layered architecture stuff.

## how they connect

```
REST Request → Service Layer (Spark routes) → Business Layer (Managers) → DAO (JDBC) → MySQL
```
