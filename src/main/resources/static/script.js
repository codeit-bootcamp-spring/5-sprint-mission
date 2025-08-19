// API endpoints
const API_USERS = '/api/user/findAll';
const API_BINARY = '/api/binaryContent/find';

// theme toggle
const root = document.documentElement;
const toggle = document.getElementById('themeToggle');
toggle?.addEventListener('change', () => {
    if(toggle.checked){ root.classList.add('light'); }
    else { root.classList.remove('light'); }
});

// search & refresh
const q = document.getElementById('q');
const refreshBtn = document.getElementById('refresh');
const listEl = document.getElementById('list');

refreshBtn?.addEventListener('click', render);
q?.addEventListener('input', () => { clearTimeout(window.__t); window.__t = setTimeout(render, 250); });

async function fetchUsers(){
    const res = await fetch(API_USERS);
    if(!res.ok) throw new Error('사용자 조회 실패');
    return await res.json(); // List<UserDto>
}

async function fetchBinary(binaryContentId){
    const url = `${API_BINARY}?binaryContentId=${encodeURIComponent(binaryContentId)}`;
    const res = await fetch(url);
    if(!res.ok) return null;
    return await res.json(); // BinaryContent
}

function toDataUrl(file){
    if(!file || !file.bytes || !file.contentType) return '';
    return `data:${file.contentType};base64,${file.bytes}`;
}

function filterUsers(users, q){
    const s = (q||'').trim().toLowerCase();
    if(!s) return users;
    return users.filter(u =>
        String(u.username||'').toLowerCase().includes(s) ||
        String(u.email||'').toLowerCase().includes(s)
    );
}

function dateText(iso){ try{ return new Date(iso).toLocaleString(); }catch{ return ''; } }

async function render(){
    listEl.innerHTML = `
    <div class="card skeleton"></div>
    <div class="card skeleton"></div>
    <div class="card skeleton"></div>
    <div class="card skeleton"></div>
  `;

    try{
        const users = filterUsers(await fetchUsers(), q?.value);
        const items = await Promise.all(users.map(async u=>{
            let avatar = '';
            if(u.profileId){
                const file = await fetchBinary(u.profileId);
                avatar = toDataUrl(file);
            }
            const badge = u.online ? '<span class="badge">온라인</span>' : '<span class="badge off">오프라인</span>';
            return `
        <article class="card">
          <img class="avatar" src="${avatar}" alt="" onerror="this.style.display='none'"/>
          <div class="info">
            <div class="name">${u.username||''}</div>
            <div class="email">${u.email||''}</div>
            <div class="meta">
              <span>생성: ${dateText(u.createdAt)}</span>
              <span>수정: ${dateText(u.updatedAt)}</span>
            </div>
          </div>
          ${badge}
        </article>
      `;
        }));
        listEl.innerHTML = items.join('') || '<div class="card">데이터가 없습니다.</div>';
    }catch(e){
        listEl.innerHTML = `<div class="card">오류: ${e.message}</div>`;
    }
}

// first render
render();
