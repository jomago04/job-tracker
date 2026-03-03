/**
 * Job Tracker API Client
 * Handles all REST API calls to the backend
 */
class ApiClient {
    constructor(baseUrl = 'http://localhost:8080') {
        this.baseUrl = baseUrl;
        this.apiPath = '/api';
    }

    /**
     * Generic fetch wrapper with error handling
     */
    async fetch(endpoint, options = {}) {
        const url = `${this.baseUrl}${this.apiPath}${endpoint}`;
        const startTime = performance.now();

        try {
            console.log(`Fetching: ${url}`);
            const response = await fetch(url, {
                method: options.method || 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                body: options.body ? JSON.stringify(options.body) : undefined
            });

            const data = await response.json();
            const endTime = performance.now();
            const duration = (endTime - startTime).toFixed(2);

            return {
                success: response.ok,
                status: response.status,
                data: data,
                duration: duration,
                raw: response
            };
        } catch (error) {
            const endTime = performance.now();
            const duration = (endTime - startTime).toFixed(2);
            console.error(`Error fetching ${url}:`, error);
            return {
                success: false,
                status: 0,
                error: error.message,
                duration: duration
            };
        }
    }

    // =====================================================================
    // USER ENDPOINTS
    // =====================================================================

    async getUser(uuid) {
        if (!uuid || uuid.trim() === '') {
            uiManager.displayError('users', 'Please enter a User UUID');
            return;
        }
        const result = await this.fetch(`/users/${encodeURIComponent(uuid)}`);
        uiManager.displayResult('users', result);
    }

    async getAllUsers(limit = 10, offset = 0) {
        const result = await this.fetch(`/users?limit=${limit}&offset=${offset}`);
        userPaginationState.limit = limit;
        userPaginationState.offset = offset;
        uiManager.displayResult('users', result, 'users');
    }

    async checkEmailExists(email) {
        if (!email || email.trim() === '') {
            uiManager.displayError('users', 'Please enter an email address');
            return;
        }
        const result = await this.fetch(`/users/email/${encodeURIComponent(email)}/exists`);
        uiManager.displayResult('users', result);
    }

    prevPageUsers() {
        const newOffset = Math.max(0, userPaginationState.offset - userPaginationState.limit);
        this.getAllUsers(userPaginationState.limit, newOffset);
    }

    nextPageUsers() {
        const newOffset = userPaginationState.offset + userPaginationState.limit;
        this.getAllUsers(userPaginationState.limit, newOffset);
    }

    // =====================================================================
    // COMPANY ENDPOINTS
    // =====================================================================

    async getCompany(cuid) {
        if (!cuid || cuid.trim() === '') {
            uiManager.displayError('companies', 'Please enter a Company CUID');
            return;
        }
        const result = await this.fetch(`/companies/${encodeURIComponent(cuid)}`);
        uiManager.displayResult('companies', result);
    }

    async getAllCompanies(limit = 10, offset = 0) {
        const result = await this.fetch(`/companies?limit=${limit}&offset=${offset}`);
        companyPaginationState.limit = limit;
        companyPaginationState.offset = offset;
        uiManager.displayResult('companies', result, 'companies');
    }

    async checkCompanyNameExists(name) {
        if (!name || name.trim() === '') {
            uiManager.displayError('companies', 'Please enter a company name');
            return;
        }
        const result = await this.fetch(`/companies/name/${encodeURIComponent(name)}/exists`);
        uiManager.displayResult('companies', result);
    }

    prevPageCompanies() {
        const newOffset = Math.max(0, companyPaginationState.offset - companyPaginationState.limit);
        this.getAllCompanies(companyPaginationState.limit, newOffset);
    }

    nextPageCompanies() {
        const newOffset = companyPaginationState.offset + companyPaginationState.limit;
        this.getAllCompanies(companyPaginationState.limit, newOffset);
    }

    // =====================================================================
    // JOB ENDPOINTS
    // =====================================================================

    async getJob(juid) {
        if (!juid || juid.trim() === '') {
            uiManager.displayError('jobs', 'Please enter a Job JUID');
            return;
        }
        const result = await this.fetch(`/jobs/${encodeURIComponent(juid)}`);
        uiManager.displayResult('jobs', result);
    }

    async getAllJobs(limit = 10, offset = 0) {
        const result = await this.fetch(`/jobs?limit=${limit}&offset=${offset}`);
        jobPaginationState.limit = limit;
        jobPaginationState.offset = offset;
        uiManager.displayResult('jobs', result, 'jobs');
    }

    async checkJobExists(juid) {
        if (!juid || juid.trim() === '') {
            uiManager.displayError('jobs', 'Please enter a Job JUID');
            return;
        }
        const result = await this.fetch(`/jobs/${encodeURIComponent(juid)}/exists`);
        uiManager.displayResult('jobs', result);
    }

    prevPageJobs() {
        const newOffset = Math.max(0, jobPaginationState.offset - jobPaginationState.limit);
        this.getAllJobs(jobPaginationState.limit, newOffset);
    }

    nextPageJobs() {
        const newOffset = jobPaginationState.offset + jobPaginationState.limit;
        this.getAllJobs(jobPaginationState.limit, newOffset);
    }

    // =====================================================================
    // APPLICATION ENDPOINTS
    // =====================================================================

    async getApplication(auid) {
        if (!auid || auid.trim() === '') {
            uiManager.displayError('applications', 'Please enter an Application AUID');
            return;
        }
        const result = await this.fetch(`/applications/${encodeURIComponent(auid)}`);
        uiManager.displayResult('applications', result);
    }

    async getAllApplications(limit = 10, offset = 0) {
        const result = await this.fetch(`/applications?limit=${limit}&offset=${offset}`);
        appPaginationState.limit = limit;
        appPaginationState.offset = offset;
        uiManager.displayResult('applications', result, 'applications');
    }

    async checkAppExists(auid) {
        if (!auid || auid.trim() === '') {
            uiManager.displayError('applications', 'Please enter an Application AUID');
            return;
        }
        const result = await this.fetch(`/applications/${encodeURIComponent(auid)}/exists`);
        uiManager.displayResult('applications', result);
    }

    async checkDuplicateApplication(uuid, juid) {
        if (!uuid || uuid.trim() === '' || !juid || juid.trim() === '') {
            uiManager.displayError('applications', 'Please enter both User UUID and Job JUID');
            return;
        }
        const result = await this.fetch(`/applications/user/${encodeURIComponent(uuid)}/job/${encodeURIComponent(juid)}/exists`);
        uiManager.displayResult('applications', result);
    }

    async createApplication(uuid, juid, status, source = '', notes = '', appliedAt = null) {
        if (!uuid || uuid.trim() === '') {
            uiManager.displayError('applications', 'Please enter a User UUID');
            return;
        }
        if (!juid || juid.trim() === '') {
            uiManager.displayError('applications', 'Please enter a Job JUID');
            return;
        }
        if (!status || status.trim() === '') {
            uiManager.displayError('applications', 'Please select an application status');
            return;
        }

        const body = {
            uuid: uuid,
            juid: juid,
            status: status,
            source: source || null,
            notes: notes || null,
            appliedAt: appliedAt || new Date().toISOString()
        };

        const result = await this.fetch('/applications', {
            method: 'POST',
            body: body
        });
        uiManager.displayResult('applications', result);
    }

    prevPageApplications() {
        const newOffset = Math.max(0, appPaginationState.offset - appPaginationState.limit);
        this.getAllApplications(appPaginationState.limit, newOffset);
    }

    nextPageApplications() {
        const newOffset = appPaginationState.offset + appPaginationState.limit;
        this.getAllApplications(appPaginationState.limit, newOffset);
    }

    // =====================================================================
    // ACTIVITY ENDPOINTS
    // =====================================================================

    async getActivity(actuid) {
        if (!actuid || actuid.trim() === '') {
            uiManager.displayError('activities', 'Please enter an Activity ACTUID');
            return;
        }
        const result = await this.fetch(`/activities/${encodeURIComponent(actuid)}`);
        uiManager.displayResult('activities', result);
    }

    async getAllActivities(limit = 10, offset = 0, auidFilter = null) {
        let endpoint = `/activities?limit=${limit}&offset=${offset}`;
        if (auidFilter) {
            endpoint += `&auid=${encodeURIComponent(auidFilter)}`;
        }
        const result = await this.fetch(endpoint);
        activityPaginationState.limit = limit;
        activityPaginationState.offset = offset;
        activityPaginationState.auidFilter = auidFilter;
        uiManager.displayResult('activities', result, 'activities');
    }

    async getActivitiesByApplicationId(auid) {
        if (!auid || auid.trim() === '') {
            uiManager.displayError('activities', 'Please enter an Application AUID');
            return;
        }
        const result = await this.fetch(`/activities/application/${encodeURIComponent(auid)}`);
        uiManager.displayResult('activities', result);
    }

    prevPageActivities() {
        const newOffset = Math.max(0, activityPaginationState.offset - activityPaginationState.limit);
        this.getAllActivities(activityPaginationState.limit, newOffset, activityPaginationState.auidFilter);
    }

    nextPageActivities() {
        const newOffset = activityPaginationState.offset + activityPaginationState.limit;
        this.getAllActivities(activityPaginationState.limit, newOffset, activityPaginationState.auidFilter);
    }
}

/**
 * UI Manager - Handles all UI updates
 */
class UIManager {
    displayError(resource, message) {
        const responseDiv = document.getElementById(`${resource}Response`);
        responseDiv.innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> <strong>Error:</strong> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }

    displayLoading(resource) {
        const responseDiv = document.getElementById(`${resource}Response`);
        responseDiv.innerHTML = `
            <div class="text-center">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2 text-muted">Loading...</p>
            </div>
        `;
    }

    displayResult(resource, result, paginationType = null) {
        const responseDiv = document.getElementById(`${resource}Response`);

        if (!result.success) {
            let errorMsg = result.error || 'Unknown error occurred';
            if (result.status === 404) {
                errorMsg = 'Resource not found (404)';
            } else if (result.status === 400) {
                errorMsg = 'Bad request (400)';
            }
            this.displayError(resource, errorMsg);
            return;
        }

        let html = `
            <div class="response-card">
                <div class="response-header">
                    <strong>Status:</strong> <span class="badge bg-success">${result.status}</span>
                    <strong class="ms-2">Time:</strong> <span class="badge bg-info">${result.duration}ms</span>
                </div>
        `;

        // Handle different response types
        if (Array.isArray(result.data.data)) {
            html += this.formatArrayResponse(result.data.data, resource, paginationType);
        } else if (typeof result.data.data === 'boolean') {
            html += this.formatBooleanResponse(result.data.data);
        } else if (typeof result.data.data === 'object' && result.data.data !== null) {
            html += this.formatObjectResponse(result.data.data);
        } else {
            html += this.formatPrimitiveResponse(result.data.data);
        }

        html += '</div>';
        responseDiv.innerHTML = html;

        // Update pagination info if applicable
        if (paginationType && Array.isArray(result.data.data)) {
            this.updatePaginationInfo(paginationType, result.data.data.length);
        }
    }

    formatArrayResponse(data, resource, paginationType) {
        if (data.length === 0) {
            return '<p class="text-muted text-center py-3">No results found</p>';
        }

        let html = '<div class="table-responsive"><table class="table table-sm table-hover mb-0">';
        html += '<thead class="table-light"><tr>';

        // Get column names from first object
        const columns = Object.keys(data[0]);
        columns.forEach(col => {
            html += `<th>${col}</th>`;
        });
        html += '</tr></thead><tbody>';

        // Add rows
        data.forEach(item => {
            html += '<tr>';
            columns.forEach(col => {
                let value = item[col];
                // Truncate long values
                if (typeof value === 'string' && value.length > 30) {
                    value = value.substring(0, 30) + '...';
                }
                html += `<td><code class="small">${value || '-'}</code></td>`;
            });
            html += '</tr>';
        });

        html += '</tbody></table></div>';
        return html;
    }

    formatObjectResponse(data) {
        let html = '<div class="response-json"><pre>';
        html += JSON.stringify(data, null, 2);
        html += '</pre></div>';
        return html;
    }

    formatBooleanResponse(data) {
        const badge = data ? 'bg-success' : 'bg-danger';
        const text = data ? 'EXISTS' : 'NOT FOUND';
        return `<div class="text-center py-3"><span class="badge ${badge} p-3" style="font-size: 1.2em;">${text}</span></div>`;
    }

    formatPrimitiveResponse(data) {
        return `<div class="response-json"><pre>${JSON.stringify(data, null, 2)}</pre></div>`;
    }

    updatePaginationInfo(resource, itemCount) {
        let state;
        if (resource === 'users') state = userPaginationState;
        else if (resource === 'companies') state = companyPaginationState;
        else if (resource === 'jobs') state = jobPaginationState;
        else if (resource === 'applications') state = appPaginationState;
        else if (resource === 'activities') state = activityPaginationState;

        if (!state) return;

        const infoDiv = document.getElementById(`${resource}PaginationInfo`);
        const start = state.offset;
        const end = start + itemCount - 1;
        infoDiv.textContent = `Page: ${start}-${end} | Items on page: ${itemCount}`;
    }
}

// =====================================================================
// PAGINATION STATE MANAGEMENT
// =====================================================================
const userPaginationState = { limit: 10, offset: 0 };
const companyPaginationState = { limit: 10, offset: 0 };
const jobPaginationState = { limit: 10, offset: 0 };
const appPaginationState = { limit: 10, offset: 0 };
const activityPaginationState = { limit: 10, offset: 0, auidFilter: null };

// =====================================================================
// INITIALIZATION
// =====================================================================
const apiClient = new ApiClient('http://localhost:8080');
const uiManager = new UIManager();

// Check API connectivity on page load
document.addEventListener('DOMContentLoaded', async () => {
    console.log('Initializing Job Tracker API Browser...');

    // Test connectivity
    const testResult = await apiClient.fetch('/users?limit=1&offset=0');
    const statusBadge = document.getElementById('serverStatus');

    if (testResult.success) {
        statusBadge.textContent = 'Connected';
        statusBadge.className = 'badge bg-success';
        console.log('API Server is reachable');
    } else {
        statusBadge.textContent = 'Disconnected';
        statusBadge.className = 'badge bg-danger';
        console.error('Cannot reach API server at http://localhost:8080');
        alert('Warning: Cannot connect to API server at http://localhost:8080\n\nMake sure the Java REST service is running:\nmvn compile exec:java -Dexec.mainClass=jobtracker.service.JobTrackerRestService');
    }
});
