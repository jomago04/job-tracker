# Deployment / Setup Guide

## What you are running
- **Backend**: Java 11 + Maven + Spark Java (REST API)
- **Database**: MySQL 8 (schema + seed data included)
- **Frontend (client)**: Static HTML/CSS/JS served by the backend from `src/main/resources/public/`

By default everything runs on **http://localhost:8080**:
- UI: `http://localhost:8080/`
- API base: `http://localhost:8080/api/...`

---

## 1) Prerequisites
Install these before you start:

### Required
- **Java 11 (JDK 11)**
- **Maven 3.x**
- **MySQL 8.x** (and a way to run SQL: MySQL Workbench or `mysql` CLI)

### Helpful
- **VS Code / IntelliJ** (any Java IDE is fine)
- **curl** (for quick API checks)

Verify in a terminal:
```bash
java -version
mvn -version
mysql --version
```

---

## 2) Download and unpack
1. Open the GitHub repo
2. Click **Code → Download ZIP**
3. Unzip it
4. Open a terminal in the unzipped folder (the folder that contains `pom.xml`)

---

## 3) Set up the database (schema + seed)
The SQL scripts are in:
- `src/main/resources/db/schema.sql`
- `src/main/resources/db/seed.sql`

### 3.1 Create schema
Run `schema.sql` first.

**Option A: MySQL Workbench**
- Open `schema.sql` → run

**Option B: mysql CLI**
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

This creates the database named: **`job_tracker`**.

### 3.2 Seed sample data
Run `seed.sql` second.

Workbench: open + run.

CLI:
```bash
mysql -u root -p < src/main/resources/db/seed.sql
```

---

## 4) Configure DB connection (environment variables)
The backend reads DB settings from environment variables:
- `JOBTRACKER_DB_URL`
- `JOBTRACKER_DB_USER`
- `JOBTRACKER_DB_PASSWORD`

Example values (adjust username/password for your MySQL):

**macOS / Linux (bash/zsh):**
```bash
export JOBTRACKER_DB_URL='jdbc:mysql://localhost:3306/job_tracker'
export JOBTRACKER_DB_USER='root'
export JOBTRACKER_DB_PASSWORD='YOUR_PASSWORD_HERE'
```

**Windows (PowerShell):**
```powershell
setx JOBTRACKER_DB_URL "jdbc:mysql://localhost:3306/job_tracker"
setx JOBTRACKER_DB_USER "root"
setx JOBTRACKER_DB_PASSWORD "YOUR_PASSWORD_HERE"
```
Then restart your terminal so the variables take effect.

---

## 5) Build the project
From the repo root (where `pom.xml` is):

```bash
mvn clean package
```

This produces a runnable JAR in `target/`.

---

## 6) Run the backend (and the frontend UI)
### Option A (recommended): run the packaged JAR
```bash
java -jar target/job-tracker-1.0-SNAPSHOT.jar
```

### Option B: run via Maven exec
```bash
mvn compile exec:java -Dexec.mainClass=jobtracker.service.JobTrackerRestService
```

When startup succeeds you should see something like:
- `✓ Database connection successful!`
- row counts printed

---

## 7) Verify the setup worked
### 7.1 Verify the UI loads
Open:
- `http://localhost:8080/`

You should see the **Job Tracker API Browser** UI.

### 7.2 Verify the API responds
In a terminal:
```bash
curl "http://localhost:8080/api/users?limit=1&offset=0"
```
You should get JSON back (HTTP 200).

### 7.3 (Optional) Run the automated endpoint test suite
With the service running in one terminal, open a second terminal and run:
```bash
mvn compile exec:java -Dexec.mainClass=jobtracker.testing.EndpointTestSuiteRunner
```

---

## Troubleshooting
### “Missing DB environment variables”
Set `JOBTRACKER_DB_URL`, `JOBTRACKER_DB_USER`, `JOBTRACKER_DB_PASSWORD` and restart your terminal.

### “Failed to connect to database”
- Confirm MySQL is running
- Confirm the database exists: `job_tracker`
- Confirm credentials work: `mysql -u root -p`
- Confirm your JDBC URL is correct

### Port 8080 already in use
Stop the other process using 8080, then restart the service.
