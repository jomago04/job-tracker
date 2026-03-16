# Job Tracker (CSCE548 Project)

A full‑stack “job application tracker” built to practice **layered architecture** and **secure software basics**.

- **Backend REST API**: Java 11 + Spark Java (`/api/...`)
- **Database**: MySQL 8 (`job_tracker`)
- **Frontend UI**: static HTML/CSS/JS served by the backend at `/`

## Quick start (works on a fresh clone)
For the complete step‑by‑step guide, see **[DEPLOYMENT.md](./DEPLOYMENT.md)**.

1) Create + seed the MySQL database:
```bash
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/seed.sql
```

2) Set DB environment variables:
```bash
export JOBTRACKER_DB_URL='jdbc:mysql://localhost:3306/job_tracker'
export JOBTRACKER_DB_USER='root'
export JOBTRACKER_DB_PASSWORD='YOUR_PASSWORD_HERE'
```

3) Build + run:
```bash
mvn clean package
java -jar target/job-tracker-1.0-SNAPSHOT.jar
```

4) Open the UI:
- http://localhost:8080/

## How to tell it’s working
- Console prints: `✓ Database connection successful!`
- UI loads at `/` and shows **Connected**
- API returns JSON:
```bash
curl "http://localhost:8080/api/users?limit=1&offset=0"
```

## Architecture (layers)
**Client (Browser UI)** → **Service Layer (Spark routes)** → **Business Layer (Managers)** → **Data Layer (JDBC DAO)** → **MySQL**

Key packages:
- `jobtracker.service` — REST API routes + response/error helpers
- `jobtracker.business` — business logic (Managers)
- `jobtracker.dao` — JDBC queries (`ReportDaoJdbc`)
- `jobtracker.db` — DB connection (`Db` uses env vars)

## Optional: run API endpoint tests
With the service running:
```bash
mvn compile exec:java -Dexec.mainClass=jobtracker.testing.EndpointTestSuiteRunner
```

## Tech notes
- DB credentials are **not** hardcoded; they come from environment variables.
- The UI is served from `src/main/resources/public/`.