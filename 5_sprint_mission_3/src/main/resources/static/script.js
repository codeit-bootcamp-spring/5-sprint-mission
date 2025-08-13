document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// AI가 생성한 가짜 사용자 데이터 + 이미지 URL
const fakeUsers = [
    {
        id: "uuid-1",
        createdAt: "2025-08-13T10:00:00Z",
        updatedAt: "2025-08-13T12:00:00Z",
        username: "Alice",
        email: "alice@example.com",
        profileUrl: "https://i.pravatar.cc/60?img=1",
        online: true
    },
    {
        id: "uuid-2",
        createdAt: "2025-08-12T09:30:00Z",
        updatedAt: "2025-08-12T11:00:00Z",
        username: "Bob",
        email: "bob@example.com",
        profileUrl: "https://i.pravatar.cc/60?img=2",
        online: false
    },
    {
        id: "uuid-3",
        createdAt: "2025-08-10T15:00:00Z",
        updatedAt: "2025-08-12T14:00:00Z",
        username: "Charlie",
        email: "charlie@example.com",
        profileUrl: "https://i.pravatar.cc/60?img=3",
        online: true
    }
];

async function fetchAndRenderUsers() {
    // API 호출 대신 AI 데이터 사용
    renderUserList(fakeUsers);
}

async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        // 프로필 이미지 URL 사용, 없으면 기본 아바타
        const profileUrl = user.profileUrl || '/default-avatar.png';

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
