// 정적 사용자 목록 (프로필 이미지 포함)
const users = [
    {
        username: "jessie",
        email: "jessie@codeit.com",
        imageUrl: "https://i.pravatar.cc/150?img=3",
        online: true
    },
    {
        username: "rex",
        email: "rex@codeit.com",
        imageUrl: "https://i.pravatar.cc/150?img=4",
        online: true
    },
    {
        username: "buzz",
        email: "buzz@codeit.com",
        imageUrl: "https://i.pravatar.cc/150?img=5",
        online: false
    },
    {
        username: "woody",
        email: "woody@codeit.com",
        imageUrl: "https://i.pravatar.cc/150?img=6",
        online: true
    }
];

// 페이지 로드 후 사용자 목록 렌더링
document.addEventListener('DOMContentLoaded', () => {
    renderUserList(users);
});

// 사용자 목록 렌더링 함수
function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    users.forEach(user => {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        userElement.innerHTML = `
      <img src="${user.imageUrl}" alt="${user.username}" class="user-avatar">
      <div class="user-info">
        <div class="user-name">${user.username}</div>
        <div class="user-email">${user.email}</div>
      </div>
      <div class="status-badge ${user.online ? 'online' : 'offline'}">
        ${user.online ? '온라인' : '오프라인'}
      </div>
    `;

        userListElement.appendChild(userElement);
    });
}