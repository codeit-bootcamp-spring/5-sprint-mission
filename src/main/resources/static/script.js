// API endpoints
const API_BASE_URL = 'http://localhost:8080/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok)
            throw new Error('Failed to fetch users');

        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}


// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = ''; // Clear existing content

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        // Get profile image URL
        const profileUrl = user.profileId ? await fetchUserProfile(user.profileId) : '/img/avatar/default-avatar.png';

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}/${profileId}`);
        if (!response.ok) 
            throw new Error('Failed to fetch profile');
        const profile = await response.json();

        // Convert base64 encoded bytes to data URL
        return toDataUrl(profile);
    } catch (error) {
        console.error('Error fetching profile:', error);
        return '/default-avatar.png'; // Fallback to default avatar
    }
}

function toDataUrl(profile) {
    if (!profile || !profile.contentType || !profile.content) {
        return 'img/avatar/default-avatar.png'
    }

    // contentType은 BinaryContentType의 type 값과 매칭
    const mimeTypeMap = {
        PNG: "image/png",
        JPG: "image/jpg",
        JPEG: "image/jpeg",
        GIF: "image/gif",
        WEBP: "image/webp",
        BMP: "image/bmp",
        SVG: "image/svg+xml", // SVG는 xml 형식
        TIFF: "image/tiff"
    };
    const cleanBase64 = profile.content.replace(/\s/g, '');
    const mimeType = mimeTypeMap[profile.contentType];
    return `data:${mimeType};base64,${cleanBase64}`;
}