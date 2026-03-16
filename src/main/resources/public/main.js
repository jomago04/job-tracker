/**
 * Job Tracker API Client
 * Handles all REST API calls to the backend
 */
class ApiClient {
    constructor(baseUrl = window.location.origin) {
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

    // Helper for UI workflows (does not update the Companies tab UI)
    async fetchCompanies(limit = 1000, offset = 0) {
        return await this.fetch(`/companies?limit=${limit}&offset=${offset}`);
    }

    async findCompanyIdByName(name) {
        if (!name || name.trim() === '') {
            return { success: true, status: 200, data: null, duration: '0.00' };
        }

        const normalized = name.trim().toLowerCase();
        const result = await this.fetchCompanies(1000, 0);
        if (!result.success || !Array.isArray(result.data)) {
            return result;
        }

        const match = result.data.find(c => (c.name || '').trim().toLowerCase() === normalized);
        return {
            success: true,
            status: 200,
            data: match ? match.cuid : null,
            duration: result.duration
        };
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

    // =====================================================================
    // CREATE OPERATIONS (POST)
    // =====================================================================

    async createUser(email, password, name) {
        if (!email || email.trim() === '' || !password || password.trim() === '' || !name || name.trim() === '') {
            uiManager.displayError('users', 'Name, email, and password are required');
            return;
        }

        uiManager.displayLoading('users');
        const result = await this.fetch('/users', {
            method: 'POST',
            body: {
                email: email.trim(),
                passwordHash: password.trim(),
                name: name.trim()
            }
        });
        uiManager.displayResult('users', result);
    }

    async createCompany(name, industry, city, state, url) {
        if (!name || name.trim() === '') {
            uiManager.displayError('createApp', 'Company name is required');
            return null;
        }
        const result = await this.fetch('/companies', {
            method: 'POST',
            body: {
                name: name.trim(),
                industry: industry?.trim() || null,
                locationCity: city?.trim() || null,
                locationState: state?.trim() || null,
                companyUrl: url?.trim() || null
            }
        });
        return result;
    }

    async createJob(cuid, title, employmentType, workType, jobUrl, salaryMin, salaryMax) {
        if (!cuid || !title || !employmentType || !workType) {
            uiManager.displayError('createApp', 'Company, title, employment type, and work type are required');
            return null;
        }
        const result = await this.fetch('/jobs', {
            method: 'POST',
            body: {
                cuid: cuid.trim(),
                title: title.trim(),
                employmentType: employmentType.toLowerCase(),
                workType: workType.toLowerCase(),
                jobUrl: jobUrl?.trim() || null,
                salaryMin: salaryMin ? parseInt(salaryMin) : null,
                salaryMax: salaryMax ? parseInt(salaryMax) : null
            }
        });
        return result;
    }

    async createApplication(uuid, juid, status, source, notes) {
        if (!uuid || !juid) {
            uiManager.displayError('createApp', 'User ID and Job ID are required');
            return null;
        }
        const result = await this.fetch('/applications', {
            method: 'POST',
            body: {
                uuid: uuid.trim(),
                juid: juid.trim(),
                status: status || 'applied',
                source: source?.trim() || null,
                notes: notes?.trim() || null
            }
        });
        return result;
    }

    // =====================================================================
    // UPDATE OPERATIONS (PUT)
    // =====================================================================

    async updateApplicationStatus(auid, newStatus) {
        if (!auid || !newStatus) {
            uiManager.displayError('applications', 'Application ID and status are required');
            return null;
        }
        const result = await this.fetch(`/applications/${encodeURIComponent(auid)}/status`, {
            method: 'PUT',
            body: { status: newStatus }
        });
        return result;
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
        const data = result.data;
        if (Array.isArray(data)) {
            html += this.formatArrayResponse(data, resource, paginationType);
        } else if (typeof data === 'boolean') {
            html += this.formatBooleanResponse(data);
        } else if (typeof data === 'object' && data !== null) {
            html += this.formatObjectResponse(data);
        } else {
            html += this.formatPrimitiveResponse(data);
        }

        html += '</div>';
        responseDiv.innerHTML = html;

        // Update pagination info if applicable
        if (paginationType && Array.isArray(data)) {
            this.updatePaginationInfo(paginationType, data.length);
        }
    }

    // =========================================================================
    // Field display configuration per entity type
    // =========================================================================

    getFieldConfig(key) {
        const configs = {
            // User fields
            uuid:           { label: 'User ID',          icon: 'fa-fingerprint',    copyable: true },
            email:          { label: 'Email',             icon: 'fa-envelope' },
            passwordHash:   { label: 'Password Hash',    icon: 'fa-lock',           hidden: true },
            name:           { label: 'Name',              icon: 'fa-user' },
            // Company fields
            cuid:           { label: 'Company ID',        icon: 'fa-fingerprint',    copyable: true },
            industry:       { label: 'Industry',          icon: 'fa-industry' },
            locationCity:   { label: 'City',              icon: 'fa-map-marker-alt' },
            locationState:  { label: 'State',             icon: 'fa-map' },
            companyUrl:     { label: 'Website',           icon: 'fa-globe',          isLink: true },
            // Job fields
            juid:           { label: 'Job ID',            icon: 'fa-fingerprint',    copyable: true },
            title:          { label: 'Job Title',         icon: 'fa-briefcase' },
            employmentType: { label: 'Employment Type',   icon: 'fa-clock',          badge: true },
            workType:       { label: 'Work Type',         icon: 'fa-laptop-house',   badge: true },
            jobUrl:         { label: 'Job Posting',       icon: 'fa-external-link-alt', isLink: true },
            salaryMin:      { label: 'Salary Min',        icon: 'fa-dollar-sign',    isCurrency: true },
            salaryMax:      { label: 'Salary Max',        icon: 'fa-dollar-sign',    isCurrency: true },
            // Application fields
            auid:           { label: 'Application ID',    icon: 'fa-fingerprint',    copyable: true },
            status:         { label: 'Status',            icon: 'fa-flag',           statusBadge: true },
            appliedAt:      { label: 'Applied',           icon: 'fa-calendar-check', isDate: true },
            source:         { label: 'Source',             icon: 'fa-share-alt',      badge: true },
            notes:          { label: 'Notes',              icon: 'fa-sticky-note' },
            lastUpdatedAt:  { label: 'Last Updated',      icon: 'fa-clock',          isDate: true },
            // Activity fields
            actuid:         { label: 'Activity ID',       icon: 'fa-fingerprint',    copyable: true },
            eventType:      { label: 'Event Type',        icon: 'fa-bolt',           badge: true },
            oldStatus:      { label: 'Previous Status',   icon: 'fa-arrow-left',     statusBadge: true },
            newStatus:      { label: 'New Status',        icon: 'fa-arrow-right',    statusBadge: true },
            eventTime:      { label: 'Event Time',        icon: 'fa-clock',          isDate: true },
            details:        { label: 'Details',            icon: 'fa-info-circle' },
            // Generic
            createdAt:      { label: 'Created',           icon: 'fa-calendar-plus',  isDate: true },
            applicationIds: { label: 'Application IDs',   icon: 'fa-list' },
        };
        return configs[key] || { label: this.humanize(key), icon: 'fa-circle' };
    }

    humanize(str) {
        return str
            .replace(/([A-Z])/g, ' $1')
            .replace(/[_-]/g, ' ')
            .replace(/^\w/, c => c.toUpperCase())
            .trim();
    }

    getStatusColor(status) {
        const colors = {
            applied:             'primary',
            phone_screen:        'info',
            interview:           'warning',
            offer:               'success',
            rejected:            'danger',
            withdrawn:           'secondary',
            created:             'primary',
            status_change:       'warning',
            note_added:          'info',
            interview_scheduled: 'warning',
            followup_set:        'secondary',
        };
        return colors[(status || '').toLowerCase()] || 'secondary';
    }

    formatValue(value, config) {
        if (value === null || value === undefined || value === '') return '<span class="text-muted">—</span>';

        if (Array.isArray(value)) {
            if (value.length === 0) return '<span class="text-muted">—</span>';
            const joined = value.map(v => String(v)).join(', ');
            return `<code class="user-select-all small">${this.escapeHtml(joined)}</code>`;
        }

        if (config.hidden) return '<span class="text-muted fst-italic">••••••••</span>';

        if (config.isDate) {
            try {
                const d = new Date(value);
                return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
            } catch { return value; }
        }
        if (config.isCurrency) {
            return '$' + Number(value).toLocaleString('en-US');
        }
        if (config.isLink) {
            const url = value.startsWith('http') ? value : `https://${value}`;
            return `<a href="${url}" target="_blank" rel="noopener">${value} <i class="fas fa-external-link-alt fa-xs"></i></a>`;
        }
        if (config.statusBadge) {
            const color = this.getStatusColor(value);
            return `<span class="badge bg-${color}">${this.humanize(value)}</span>`;
        }
        if (config.badge) {
            return `<span class="badge bg-secondary">${this.humanize(String(value))}</span>`;
        }
        if (config.copyable) {
            return `<code class="user-select-all small">${value}</code>`;
        }
        return this.escapeHtml(String(value));
    }

    escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    // =========================================================================
    // Format a single object as a clean detail card
    // =========================================================================

    formatObjectResponse(data) {
        const keys = Object.keys(data);
        let html = '<div class="detail-card mt-3">';

        keys.forEach(key => {
            const config = this.getFieldConfig(key);
            const formattedVal = this.formatValue(data[key], config);
            html += `
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="fas ${config.icon} fa-fw text-muted me-2"></i>${config.label}
                    </div>
                    <div class="detail-value">${formattedVal}</div>
                </div>`;
        });

        html += '</div>';
        return html;
    }

    // =========================================================================
    // Format an array as a clean table with human-readable headers
    // =========================================================================

    formatArrayResponse(data, resource, paginationType) {
        if (data.length === 0) {
            return `<p class="text-muted text-center py-3"><i class="fas fa-inbox fa-2x d-block mb-2"></i>No results found</p>`;
        }

        const columns = Object.keys(data[0]);
        // Hide passwordHash in tables
        const visibleCols = columns.filter(c => c !== 'passwordHash');

        let html = `<div class="table-responsive mt-3">
            <table class="table table-sm table-hover align-middle mb-0">
            <thead><tr>`;

        visibleCols.forEach(col => {
            const config = this.getFieldConfig(col);
            html += `<th class="text-nowrap"><i class="fas ${config.icon} fa-fw me-1 text-muted"></i>${config.label}</th>`;
        });
        html += '</tr></thead><tbody>';

        data.forEach(item => {
            html += '<tr>';
            visibleCols.forEach(col => {
                const config = this.getFieldConfig(col);
                let value = item[col];
                // Truncate long strings in table cells
                const truncated = (typeof value === 'string' && value.length > 36);
                if (truncated) value = value.substring(0, 36) + '…';
                const formatted = this.formatValue(value, { ...config, copyable: false });
                html += `<td class="small">${formatted}</td>`;
            });
            html += '</tr>';
        });

        html += '</tbody></table></div>';
        html += `<div class="text-muted small mt-2 text-end">${data.length} record${data.length !== 1 ? 's' : ''} shown</div>`;
        return html;
    }

    // =========================================================================
    // Boolean & primitive formatters
    // =========================================================================

    formatBooleanResponse(data) {
        const icon = data ? 'fa-check-circle' : 'fa-times-circle';
        const color = data ? 'success' : 'danger';
        const text = data ? 'Yes — Exists' : 'No — Not Found';
        return `<div class="text-center py-4">
            <i class="fas ${icon} fa-3x text-${color} mb-2"></i>
            <p class="mb-0 fw-semibold text-${color}">${text}</p>
        </div>`;
    }

    formatPrimitiveResponse(data) {
        if (typeof data === 'string' && data.length > 20) {
            // Likely a UUID / ID
            return `<div class="text-center py-3">
                <span class="text-muted small d-block mb-1">Result</span>
                <code class="fs-6 user-select-all">${this.escapeHtml(String(data))}</code>
            </div>`;
        }
        return `<div class="text-center py-3">
            <span class="text-muted small d-block mb-1">Result</span>
            <span class="fs-5 fw-semibold">${this.escapeHtml(String(data))}</span>
        </div>`;
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
const apiClient = new ApiClient();
const uiManager = new UIManager();

// =====================================================================
// CREATE APP COMPANY SELECT (avoid duplicate companies)
// =====================================================================
function applyCreateAppCompanySelection() {
    const select = document.getElementById('createCompanySelect');
    if (!select) return;

    const disabled = select.value && select.value.trim() !== '';
    const fields = [
        'createCompanyName',
        'createCompanyIndustry',
        'createCompanyCity',
        'createCompanyState',
        'createCompanyURL'
    ];

    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.disabled = disabled;
    });
}

async function refreshCreateAppCompanySelect() {
    const select = document.getElementById('createCompanySelect');
    if (!select) return;

    const previous = select.value;
    select.innerHTML = '<option value="">Loading companies...</option>';

    const result = await apiClient.fetchCompanies(1000, 0);
    if (!result.success || !Array.isArray(result.data)) {
        select.innerHTML = '<option value="">-- Unable to load companies --</option>';
        const msg = result.data?.error || result.error || 'Failed to load companies';
        uiManager.displayError('createApp', msg);
        return;
    }

    const companies = [...result.data].sort((a, b) => (a.name || '').localeCompare(b.name || ''));

    select.innerHTML =
        '<option value="">-- Create new / type company name below --</option>' +
        companies.map(c => `<option value="${c.cuid}">${c.name} (${c.cuid})</option>`).join('');

    if (previous) {
        select.value = previous;
    }

    applyCreateAppCompanySelection();
}

// Initialize the company select if the Create Application tab exists
const createCompanySelectEl = document.getElementById('createCompanySelect');
if (createCompanySelectEl) {
    createCompanySelectEl.addEventListener('change', applyCreateAppCompanySelection);
    refreshCreateAppCompanySelect();
}

// =====================================================================
// CREATE APPLICATION WORKFLOW
// =====================================================================
async function createApplicationWorkflow() {
    uiManager.displayLoading('createApp');

    // Get form values
    const selectedCompanyID = document.getElementById('createCompanySelect')?.value || '';

    const companyName = document.getElementById('createCompanyName').value;
    const companyIndustry = document.getElementById('createCompanyIndustry').value;
    const companyCity = document.getElementById('createCompanyCity').value;
    const companyState = document.getElementById('createCompanyState').value;
    const companyURL = document.getElementById('createCompanyURL').value;

    const jobTitle = document.getElementById('createJobTitle').value;
    const jobEmploymentType = document.getElementById('createJobEmploymentType').value;
    const jobWorkType = document.getElementById('createJobWorkType').value;
    const jobSalaryMin = document.getElementById('createJobSalaryMin').value;
    const jobSalaryMax = document.getElementById('createJobSalaryMax').value;
    const jobURL = document.getElementById('createJobURL').value;

    const appUserID = document.getElementById('createAppUserID').value;
    const appSource = document.getElementById('createAppSource').value;
    const appNotes = document.getElementById('createAppNotes').value;

    const companyNameNeeded = !selectedCompanyID;

    // Validate required fields
    if ((companyNameNeeded && !companyName) || !jobTitle || !jobEmploymentType || !jobWorkType || !appUserID) {
        uiManager.displayError('createApp', 'Please fill in all required fields (Job + User UUID, and Company Name if not selecting an existing company)');
        return;
    }

    try {
        // Step 1: Resolve Company ID (reuse existing if possible)
        let companyID = selectedCompanyID;

        if (!companyID) {
            console.log('Step 1: Checking for existing company by name...');
            const existingCompany = await apiClient.findCompanyIdByName(companyName);

            if (!existingCompany.success) {
                const msg = existingCompany.data?.error || existingCompany.error || 'Unknown error';
                uiManager.displayError('createApp', `Failed to lookup company: ${msg}`);
                return;
            }

            if (existingCompany.data) {
                companyID = existingCompany.data;
                console.log('Reusing existing company:', companyID);
            } else {
                console.log('Step 1: Creating company...');
                const companyResult = await apiClient.createCompany(
                    companyName, companyIndustry, companyCity, companyState, companyURL
                );

                if (!companyResult || !companyResult.success) {
                    const msg = companyResult?.data?.error || companyResult?.error || 'Unknown error';
                    uiManager.displayError('createApp', `Failed to create company: ${msg}`);
                    return;
                }

                companyID = companyResult.data;
                console.log('Company created:', companyID);
            }
        } else {
            console.log('Step 1: Using selected company:', companyID);
        }

        // Step 2: Create Job
        console.log('Step 2: Creating job...');
        const jobResult = await apiClient.createJob(
            companyID, jobTitle, jobEmploymentType, jobWorkType, jobURL, jobSalaryMin, jobSalaryMax
        );

        if (!jobResult || !jobResult.success) {
            const msg = jobResult?.data?.error || jobResult?.error || 'Unknown error';
            uiManager.displayError('createApp', `Failed to create job: ${msg}`);
            return;
        }

        const jobID = jobResult.data;
        console.log('Job created:', jobID);

        // Step 3: Create Application
        console.log('Step 3: Creating application...');
        const appResult = await apiClient.createApplication(
            appUserID, jobID, 'applied', appSource, appNotes
        );

        if (!appResult || !appResult.success) {
            const msg = appResult?.data?.error || appResult?.error || 'Unknown error';
            uiManager.displayError('createApp', `Failed to create application: ${msg}`);
            return;
        }

        const appID = appResult.data;
        console.log('Application created:', appID);

        // Success! Display confirmation with IDs
        const successHTML = `
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <h5><i class="fas fa-check-circle"></i> Success!</h5>
                <p class="mb-2">Application created and automatically tracked in Activity log.</p>

                <div class="mt-3">
                    <strong>Created IDs:</strong>
                    <div class="mt-2">
                        <div class="mb-2">
                            <strong>Company ID:</strong><br>
                            <code class="bg-light p-2 d-block">${companyID}</code>
                        </div>
                        <div class="mb-2">
                            <strong>Job ID:</strong><br>
                            <code class="bg-light p-2 d-block">${jobID}</code>
                        </div>
                        <div class="mb-2">
                            <strong>Application ID:</strong><br>
                            <code class="bg-light p-2 d-block">${appID}</code>
                        </div>
                    </div>
                </div>

                <hr>
                <p class="mb-0"><strong>Next:</strong> Go to the "Applications" tab to update status or view activity timeline.</p>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        const responseDiv = document.getElementById('createAppResponse');
        responseDiv.innerHTML = successHTML;

        // Clear form
        const companySelect = document.getElementById('createCompanySelect');
        if (companySelect) {
            companySelect.value = '';
        }

        document.getElementById('createCompanyName').value = '';
        document.getElementById('createCompanyIndustry').value = '';
        document.getElementById('createCompanyCity').value = '';
        document.getElementById('createCompanyState').value = '';
        document.getElementById('createCompanyURL').value = '';
        document.getElementById('createJobTitle').value = '';
        document.getElementById('createJobEmploymentType').value = '';
        document.getElementById('createJobWorkType').value = '';
        document.getElementById('createJobSalaryMin').value = '';
        document.getElementById('createJobSalaryMax').value = '';
        document.getElementById('createJobURL').value = '';
        document.getElementById('createAppUserID').value = '';
        document.getElementById('createAppSource').value = '';
        document.getElementById('createAppNotes').value = '';

        applyCreateAppCompanySelection();

    } catch (error) {
        console.error('Error in workflow:', error);
        uiManager.displayError('createApp', `Unexpected error: ${error.message}`);
    }
}

/**
 * Update application status with activity tracking
 */
async function updateAppStatus() {
    const auid = document.getElementById('statusAppIdInput').value.trim();
    const newStatus = document.getElementById('statusSelect').value;

    if (!auid) {
        alert('Please enter an Application AUID');
        return;
    }

    if (!newStatus) {
        alert('Please select a status');
        return;
    }

    try {
        const response = await apiClient.updateApplicationStatus(auid, newStatus);
        if (response.success || response.data) {
            alert(`Status updated to: ${newStatus}\nActivity logged automatically.`);
            document.getElementById('statusAppIdInput').value = '';
            document.getElementById('statusSelect').value = '';
            // Load timeline to show the new status change
            loadActivityTimeline(auid);
        } else {
            alert(`Error: ${response.error || 'Failed to update status'}`);
        }
    } catch (error) {
        alert(`Error updating status: ${error.message}`);
    }
}

/**
 * Load and display activity timeline for an application
 */
async function loadActivityTimeline(auid) {
    const timelineContainer = document.getElementById('activityTimeline');

    if (!auid || auid.trim() === '') {
        timelineContainer.innerHTML = '<div class="timeline-empty">Enter an Application AUID to view its timeline</div>';
        return;
    }

    try {
        // Fetch activities for this application
        const response = await apiClient.getActivitiesByApplicationId(auid);

        if (!response || !response.data || response.data.length === 0) {
            timelineContainer.innerHTML = '<div class="timeline-empty">No activities found for this application</div>';
            return;
        }

        // Build timeline HTML
        let timelineHTML = '';

        // Sort by date descending (newest first)
        const activities = response.data.sort((a, b) => {
            const dateA = new Date(a.eventTime || 0);
            const dateB = new Date(b.eventTime || 0);
            return dateB - dateA;
        });

        activities.forEach(activity => {
            const eventType = activity.eventType || 'unknown';
            const eventTime = activity.eventTime ? new Date(activity.eventTime).toLocaleString() : 'Unknown time';
            const isCreated = eventType === 'created';
            const icon = isCreated ? '📋' : '✓';
            const itemClass = isCreated ? 'created' : 'status-change';

            let eventDescription = '';
            if (isCreated) {
                eventDescription = 'Application Created';
            } else if (activity.oldStatus && activity.newStatus) {
                eventDescription = `Status Changed: ${formatStatus(activity.oldStatus)} → ${formatStatus(activity.newStatus)}`;
            } else {
                eventDescription = eventType.charAt(0).toUpperCase() + eventType.slice(1);
            }

            const detailText = activity.detail ? `<div class="timeline-detail">${sanitizeHTML(activity.detail)}</div>` : '';

            const statusBadges = activity.oldStatus && activity.newStatus
                ? `<span class="status-badge status-${activity.oldStatus}">${formatStatus(activity.oldStatus)}</span>
                   <i class="fas fa-arrow-right"></i>
                   <span class="status-badge status-${activity.newStatus}">${formatStatus(activity.newStatus)}</span>`
                : '';

            timelineHTML += `
                <div class="timeline-item ${itemClass}">
                    <div class="timeline-dot">${icon}</div>
                    <div class="timeline-content">
                        <div class="timeline-time">${eventTime}</div>
                        <div class="timeline-event">${eventDescription}</div>
                        ${statusBadges}
                        ${detailText}
                    </div>
                </div>
            `;
        });

        timelineContainer.innerHTML = timelineHTML;
    } catch (error) {
        console.error('Error loading timeline:', error);
        timelineContainer.innerHTML = `<div class="timeline-empty">Error loading activities: ${error.message}</div>`;
    }
}

/**
 * Format status for display
 */
function formatStatus(status) {
    if (!status) return 'Unknown';
    return status
        .replace(/_/g, ' ')
        .split(' ')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

/**
 * Sanitize HTML to prevent XSS
 */
function sanitizeHTML(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

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
