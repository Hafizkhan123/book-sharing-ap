// ── auth-guard.js ──────────────────────────────────────────────
// Include on every page EXCEPT login.html and register.html
// Checks login status → redirects to login if not authenticated
// Injects top navbar with email + logout button
// ───────────────────────────────────────────────────────────────

(async function authGuard() {
    try {
        const res = await fetch('/auth/status', { credentials: 'include' });

        const data = await res.json();
        if (!data.loggedIn) {
            window.location.href = '/login.html';
            return;
        }
        injectNavbar(data.email);
    } catch (e) {
        window.location.href = '/login.html';
    }
})();

function injectNavbar(email) {
    const bar = document.createElement('div');
    bar.id = 'auth-bar';
    bar.style.cssText = [
        'position:fixed', 'top:0', 'left:0', 'right:0',
        'background:#161b27',
        'border-bottom:1px solid #2a3045',
        'padding:10px 28px',
        'display:flex',
        'align-items:center',
        'justify-content:space-between',
        "font-family:'DM Sans',sans-serif",
        'font-size:0.84rem',
        'z-index:9999',
        'box-shadow:0 2px 16px rgba(0,0,0,0.3)'
    ].join(';');

    bar.innerHTML = `
        <span style="font-family:'Syne',sans-serif;font-weight:700;color:#e8ecf4;font-size:0.93rem;">
            📚 BookShare
        </span>
        <div style="display:flex;align-items:center;gap:14px;">
            <span style="color:#8892a4;font-size:0.81rem;">
                Logged in as <strong style="color:#e8ecf4;">${email}</strong>
            </span>
            <button onclick="logout()" style="
                padding:5px 14px;
                background:rgba(239,68,68,0.12);
                color:#f87171;
                border:1px solid rgba(239,68,68,0.25);
                border-radius:8px;
                cursor:pointer;
                font-family:'DM Sans',sans-serif;
                font-size:0.8rem;
                font-weight:600;
                transition:all 0.2s;
            ">Logout</button>
        </div>
    `;

    document.body.insertBefore(bar, document.body.firstChild);
    document.body.style.paddingTop = '46px';
}

async function logout() {
    try { await fetch('/auth/logout', { method: 'POST' }); } catch (e) {}
    window.location.href = '/login.html';
}
