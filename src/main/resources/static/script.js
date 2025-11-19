// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// ✅ 프로필 이미지를 굳이 fetch로 받지 말고, src에 엔드포인트를 바로 사용
function getProfileUrl(profileId) {
    return `${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`;
}

async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId
            ? getProfileUrl(user.profileId)
            : '/default-avatar.png';

        userElement.innerHTML = `
      <img src="${profileUrl}" alt="${user.username}" class="user-avatar"
           onerror="this.onerror=null; this.src='/default-avatar.png';">
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