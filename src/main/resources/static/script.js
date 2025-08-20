// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/users`,
    BINARY_CONTENT: `${API_BASE_URL}/binary-contents`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers().then(() => {
        console.log('Users fetched and rendered successfully');
    })
});

// Fetch users from the API
const fetchAndRenderUsers = async () => {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) console.error('Failed to fetch users');
        const users = await response.json();
        await renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Fetch user profile image
const fetchUserProfile = async (profileId) => {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}/${profileId}`);
        if (!response.ok) console.error('Failed to fetch profile');
        const profile = await response.json();

        // Convert base64 encoded bytes to data URL
        return `data:${profile.contentType};base64,${profile.bytes}`;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return '/default-avatar.png'; // Fallback to default avatar
    }
}

/**
 * @typedef {{ id:string, username:string, email:string, profileId?:string,
 *            userStatusType?:('ONLINE'|'OFFLINE'|'IDLE'|'DO_NOT_DISTURB') }} User
 */
/** @param {User[]} users */
// Render user list
const renderUserList = async (users) => {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = ''; // Clear existing content

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        // Get profile image URL
        const profileUrl = user.profileId ?
            await fetchUserProfile(user.profileId) :
            '/default-avatar.png';

        const status = (user.userStatusType ?? 'OFFLINE');
        const statusClass = status.toLowerCase();
        const statusText = ({ONLINE: '온라인', DO_NOT_DISTURB: '방해 금지', IDLE: '자리 비움', OFFLINE: '오프라인'})[status] ?? '오프라인';

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${statusClass}">
                ${statusText}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}
