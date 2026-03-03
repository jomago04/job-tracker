# Job Tracker API Browser Frontend

A responsive, single-page web application for testing and demonstrating all GET endpoints of the Job Tracker REST API.

## Features

✓ **Complete API Coverage**: Tests all GET endpoints for all 5 resources (Users, Companies, Jobs, Applications, Activities)
✓ **Paginated Results**: Navigate through large datasets with pagination controls
✓ **Single Record Lookup**: Fetch individual records by ID
✓ **Special Queries**: Check email exists, company name exists, duplicate applications, etc.
✓ **Real-time Feedback**: Shows response status, duration, and formatted data
✓ **Error Handling**: Graceful error messages for invalid requests
✓ **Responsive Design**: Works on desktop and mobile devices

## Requirements

- **Backend**: Job Tracker REST API running on `http://localhost:8080`
- **Browser**: Modern browser with ES6 support (Chrome, Firefox, Safari, Edge)
- **Web Server**: Optional - for local testing, can open HTML directly

## Setup & Running

### Option 1: Direct File Opening (Simplest)

1. Navigate to this directory
2. Open `index.html` directly in your browser:
   - File → Open File (in browser menu)
   - Or drag `index.html` into browser window

### Option 2: Python Web Server (Recommended)

```bash
# In the frontend directory
python -m http.server 8000

# Then open browser to: http://localhost:8000
```

For Python 2 (legacy):
```bash
python -m SimpleHTTPServer 8000
```

### Option 3: Node.js HTTP Server

```bash
# If you have Node.js installed
npx http-server

# Then open browser to: http://localhost:8080
```

## Starting the Backend API

Before using the frontend, start the Job Tracker REST API:

```bash
# From the job-tracker root directory
mvn compile exec:java -Dexec.mainClass=jobtracker.service.JobTrackerRestService
```

The API will be available at `http://localhost:8080`

## Usage Guide

### Navigation
- Click the resource tabs (Users, Companies, Jobs, Applications, Activities) to switch between endpoints
- Each tab provides controls for that resource

### Single Record Lookup
1. Enter the resource ID (UUID, CUID, JUID, AUID, or ACTUID) in the input field
2. Click the "Get" button
3. View the full record in the response panel

### List All Records (Paginated)
1. Click "First" to load the first page
2. Use "Prev" and "Next" to navigate between pages
3. Use "Refresh" to reload the current page
4. Page information shows current position (e.g., "Page: 0-9 of ?")

### Special Queries
- **Users**: Check if email exists
- **Companies**: Check if company name exists
- **Jobs**: Check if job exists
- **Applications**: Check if application exists, check for duplicate (user + job combination)
- **Activities**: Filter by application ID, get activities for specific application

## API Endpoints Tested

### Users Resources
```
GET /api/users/:uuid                     - Get single user
GET /api/users?limit=10&offset=0         - List users (paginated)
GET /api/users/email/:email/exists       - Check email exists
```

### Companies Resources
```
GET /api/companies/:cuid                 - Get single company
GET /api/companies?limit=10&offset=0     - List companies (paginated)
GET /api/companies/name/:name/exists     - Check company name exists
```

### Jobs Resources
```
GET /api/jobs/:juid                      - Get single job
GET /api/jobs?limit=10&offset=0          - List jobs (paginated)
GET /api/jobs/:juid/exists               - Check job exists
```

### Applications Resources
```
GET /api/applications/:auid              - Get single application
GET /api/applications?limit=10&offset=0  - List applications (paginated)
GET /api/applications/:auid/exists       - Check application exists
GET /api/applications/user/:uuid/job/:juid/exists - Check duplicate
```

### Activities Resources
```
GET /api/activities/:actuid              - Get single activity
GET /api/activities?limit=10&offset=0    - List activities (paginated)
GET /api/activities/application/:auid    - Get activities by application ID
```

## Features & Security

### Secure Practices Implemented

1. **CORS Configuration**: Backend configured to accept requests only from `localhost:8000` and `localhost:3000`
2. **Security Headers**: Responses include:
   - `X-Content-Type-Options: nosniff`
   - `X-Frame-Options: DENY`
   - `X-XSS-Protection: 1; mode=block`
3. **No Hardcoded Secrets**: No credentials or sensitive data in frontend code
4. **Input Validation**: Client-side validation before API calls
5. **Error Handling**: Proper error messages without exposing sensitive details
6. **Safe Serialization**: Uses `JSON.stringify()` with proper escaping

### Frontend Architecture

- **ApiClient Class**: Centralized API communication with error handling
- **UIManager Class**: Separated presentation logic from API calls
- **Pagination State**: Tracks pagination per resource independently
- **Responsive Design**: Mobile-first CSS approach
- **Accessibility**: Semantic HTML, ARIA labels, keyboard navigation support

### Performance

- **Loading States**: Shows spinner while fetching
- **Request Duration**: Displays API response time in milliseconds
- **Sticky Response Panel**: Response stays visible while scrolling
- **Efficient Rendering**: Only re-renders affected UI sections

## Testing Checklist

Before deployment, verify:

- [ ] All Users tab functions work (get, list, email check)
- [ ] All Companies tab functions work (get, list, name check)
- [ ] All Jobs tab functions work (get, list, exists check)
- [ ] All Applications tab functions work (get, list, exists, duplicate check)
- [ ] All Activities tab functions work (get, list, by application ID)
- [ ] Error handling for invalid IDs (404 responses)
- [ ] Pagination works correctly (First, Prev, Next buttons)
- [ ] Response times are displayed
- [ ] Server status indicator shows "Connected"
- [ ] Page is responsive on mobile devices
- [ ] Beautiful, readable JSON display in responses

## Browser Support

- Chrome/Chromium 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Troubleshooting

### "Cannot connect to API server"
- Ensure Java REST API is running: `mvn compile exec:java -Dexec.mainClass=jobtracker.service.JobTrackerRestService`
- Check that it's listening on `http://localhost:8080`
- Look for database connection success message in console

### CORS Errors
- The backend must have CORS configured for your frontend origin
- Default allowed: `http://localhost:8000` and `http://localhost:3000`
- Modify `JobTrackerRestService.java` if using different port

### Blank Response Display
- Click the Refresh button for the current tab
- Check browser console for errors (F12 → Console tab)
- Verify API server is still running

### Pagination Not Working
- Ensure you're on the first page before going backward
- Check pagination info shows expected record count
- Try "First" button to reset to page 0

## Development Notes

### File Structure
```
frontend/
├── index.html      - Responsive HTML UI with all forms
├── main.js         - ApiClient and UIManager classes
├── style.css       - Custom styling and responsive design
└── README.md       - This file
```

### Adding New Endpoints

To add a new API endpoint:

1. Add method to `ApiClient` class in `main.js`:
   ```javascript
   async newEndpoint(param) {
       const result = await this.fetch(`/new-endpoint/${encodeURIComponent(param)}`);
       uiManager.displayResult('resource', result);
   }
   ```

2. Add UI controls to `index.html`

3. Update this README with new endpoint information

## License

Part of Job Tracker Project 3 (CSCE 548)

## Support

For issues or questions, check:
1. Browser console for error messages (F12)
2. Backend API logs for server-side errors
3. Network tab in developer tools to inspect requests
4. README troubleshooting section above
