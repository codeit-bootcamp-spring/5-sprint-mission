// Initialize the application
document.addEventListener('DOMContentLoaded', fetchAndRenderUsers);

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch('/user/findAll');
        if (!response.ok) throw new Error('Failed to fetch users');
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const profileUrl = user.imageUrl != null ? user.imageUrl : '/default-avatar.png';
            // ? await fetchUserProfile(user.imageId)
            // : '/default-avatar.png';

        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.name}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.name}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}
