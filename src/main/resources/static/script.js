// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/users`,
    // ⬇️ BinaryContent 컨트롤러 경로에 맞춰 변경 (너 컨트롤러가 "/api/binaryContents"면 그걸로!)
    BINARY_CONTENT: `${API_BASE_URL}/binaryContents`
};

// ApiResult 언랩 헬퍼
function unwrapApiResult(payload) {
    return (payload && typeof payload === 'object' && 'data' in payload) ? payload.data : payload;
}

// Init
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Users
async function fetchAndRenderUsers() {
    try {
        const resp = await fetch(ENDPOINTS.USERS);
        if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            throw new Error(`Users API ${resp.status}: ${txt}`);
        }
        const payload = await resp.json();
        const users = unwrapApiResult(payload); // ✅ ApiResult 언랩
        if (!Array.isArray(users)) throw new Error('Users is not an array');
        renderUserList(users);
    } catch (err) {
        console.error('Error fetching users:', err);
    }
}

// Profiles (JSON 메타 또는 파일 스트림 모두 지원)
async function fetchUserProfile(profileId) {
    try {
        // ⬇️ REST 경로: /api/binaryContents/{id} (너 컨트롤러에 맞춰서)
        const resp = await fetch(`${ENDPOINTS.BINARY_CONTENT}/${profileId}`);
        if (!resp.ok) {
            const txt = await resp.text().catch(() => '');
            throw new Error(`Profile API ${resp.status}: ${txt}`);
        }

        const ct = resp.headers.get('content-type') || '';

        if (ct.includes('application/json')) {
            // 서버가 ApiResult<BinaryContentDto> JSON을 주는 경우
            const payload = await resp.json();
            const profile = unwrapApiResult(payload); // { contentType, bytes }
            if (!profile?.contentType || !profile?.bytes) {
                throw new Error('Invalid profile JSON shape');
            }
            return `data:${profile.contentType};base64,${profile.bytes}`;
        } else {
            // 서버가 바이너리 스트림(이미지 파일)을 주는 경우 (예: /{id}/file)
            const blob = await resp.blob();
            return URL.createObjectURL(blob);
        }
    } catch (err) {
        console.error('Error fetching profile:', err);
        return '/default-avatar.png';
    }
}

// Render
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        // ⚠️ user.profileId 키 이름이 정확한지 확인 (profileId/attachmentId/profile 등)
        const pid = user.profileId ?? user.profile ?? user.profileContentId;

        const profileUrl = pid
            ? await fetchUserProfile(pid)
            : '/default-avatar.png';

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
