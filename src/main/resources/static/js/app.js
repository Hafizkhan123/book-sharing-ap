// ═══════════════════════════════════════════════════
//  app.js — BookShare API functions (fetch API)
// ═══════════════════════════════════════════════════

// ── Helpers ─────────────────────────────────────────
function showAlert(id, msg, type = 'success') {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = msg;
    el.className = `alert alert-${type} show`;
    setTimeout(() => { el.className = 'alert'; }, 5000);
}

function setSpinner(id, on) {
    const el = document.getElementById(id);
    if (el) el.className = on ? 'spinner show' : 'spinner';
}

function statusBadge(s) {
    if (s === 'SHARED')   return '<span class="badge badge-shared">SHARED</span>';
    if (s === 'RETURNED') return '<span class="badge badge-returned">RETURNED</span>';
    return `<span class="badge">${s}</span>`;
}

// ── 1. Register User ─────────────────────────────────
async function registerUser() {
    const name     = document.getElementById('name')?.value?.trim();
    const email    = document.getElementById('email')?.value?.trim();
    const password = document.getElementById('password')?.value?.trim();

    if (!name || !email || !password) {
        showAlert('alert', 'All fields are required.', 'error');
        return;
    }

    setSpinner('spinner', true);
    try {
        const res  = await fetch('/users/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        const data = await res.json();

        if (!res.ok) {
            showAlert('alert', data.message || 'Registration failed.', 'error');
            return;
        }

        showAlert('alert', `User "${data.name}" registered! ID: ${data.id}`, 'success');
        document.getElementById('result-card').style.display = 'block';
        document.getElementById('res-id').textContent    = data.id;
        document.getElementById('res-name').textContent  = data.name;
        document.getElementById('res-email').textContent = data.email;
        document.getElementById('name').value     = '';
        document.getElementById('email').value    = '';
        document.getElementById('password').value = '';

    } catch (e) {
        showAlert('alert', 'Could not connect to server.', 'error');
    } finally {
        setSpinner('spinner', false);
    }
}

// ── 2. Add Book ──────────────────────────────────────
async function addBook() {
    const title  = document.getElementById('title')?.value?.trim();
    const author = document.getElementById('author')?.value?.trim();
    const userId = document.getElementById('userId')?.value?.trim();

    if (!title || !author || !userId) {
        showAlert('alert', 'All fields are required.', 'error');
        return;
    }

    setSpinner('spinner', true);
    try {
        const res  = await fetch(`/books/add/${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, author })
        });
        const data = await res.json();

        if (!res.ok) {
            showAlert('alert', data.message || 'Failed to add book.', 'error');
            return;
        }

        showAlert('alert', `Book "${data.title}" added! ID: ${data.id}`, 'success');
        document.getElementById('result-card').style.display = 'block';
        document.getElementById('res-id').textContent     = data.id;
        document.getElementById('res-title').textContent  = data.title;
        document.getElementById('res-author').textContent = data.author;
        document.getElementById('res-owner').textContent  = `${data.owner.name} (#${data.owner.id})`;
        document.getElementById('title').value  = '';
        document.getElementById('author').value = '';
        loadAllBooks();

    } catch (e) {
        showAlert('alert', 'Could not connect to server.', 'error');
    } finally {
        setSpinner('spinner', false);
    }
}

// ── 3. Load All Books ────────────────────────────────
async function loadAllBooks() {
    const tbody = document.getElementById('books-tbody');
    if (!tbody) return;
    tbody.innerHTML = '<tr class="empty-row"><td colspan="4">Loading...</td></tr>';
    try {
        const res  = await fetch('/books');
        const data = await res.json();
        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="4">No books found</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(b => `
            <tr>
                <td>${b.id}</td>
                <td><strong>${b.title}</strong></td>
                <td>${b.author}</td>
                <td>${b.owner ? b.owner.name + ' <span style="color:#4a5568">(#' + b.owner.id + ')</span>' : '—'}</td>
            </tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="4">Error loading books</td></tr>';
    }
}

// ── 4. Share Book ────────────────────────────────────
async function shareBook() {
    const bookId   = document.getElementById('bookId')?.value?.trim();
    const toUserId = document.getElementById('toUserId')?.value?.trim();

    if (!bookId || !toUserId) {
        showAlert('alert', 'Both fields are required.', 'error');
        return;
    }

    setSpinner('spinner', true);
    try {
        const res  = await fetch(`/share/${bookId}/${toUserId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();

        if (!res.ok) {
            showAlert('alert', data.message || 'Failed to share.', 'error');
            return;
        }

        showAlert('alert', `Book shared! Share ID: ${data.id}`, 'success');
        document.getElementById('result-card').style.display = 'block';
        document.getElementById('res-id').textContent   = data.id;
        document.getElementById('res-book').textContent = `${data.book.title} (#${data.book.id})`;
        document.getElementById('res-from').textContent = `${data.fromUser.name} (#${data.fromUser.id})`;
        document.getElementById('res-to').textContent   = `${data.toUser.name} (#${data.toUser.id})`;
        document.getElementById('res-date').textContent = data.shareDate;
        document.getElementById('res-status').innerHTML = statusBadge(data.status);
        document.getElementById('bookId').value   = '';
        document.getElementById('toUserId').value = '';

    } catch (e) {
        showAlert('alert', 'Could not connect to server.', 'error');
    } finally {
        setSpinner('spinner', false);
    }
}

// ── 5. Return Book ───────────────────────────────────
async function returnBook() {
    const shareId = document.getElementById('shareId')?.value?.trim();

    if (!shareId) {
        showAlert('alert', 'Share ID is required.', 'error');
        return;
    }

    setSpinner('spinner', true);
    try {
        const res  = await fetch(`/share/return/${shareId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();

        if (!res.ok) {
            showAlert('alert', data.message || 'Failed to return.', 'error');
            return;
        }

        showAlert('alert', `Book returned! Status: ${data.status}`, 'success');
        document.getElementById('result-card').style.display = 'block';
        document.getElementById('res-id').textContent          = data.id;
        document.getElementById('res-book').textContent        = `${data.book.title} (#${data.book.id})`;
        document.getElementById('res-from').textContent        = `${data.fromUser.name} (#${data.fromUser.id})`;
        document.getElementById('res-to').textContent          = `${data.toUser.name} (#${data.toUser.id})`;
        document.getElementById('res-share-date').textContent  = data.shareDate;
        document.getElementById('res-return-date').textContent = data.returnDate;
        document.getElementById('res-status').innerHTML        = statusBadge(data.status);
        document.getElementById('shareId').value = '';
        loadActiveShares();

    } catch (e) {
        showAlert('alert', 'Could not connect to server.', 'error');
    } finally {
        setSpinner('spinner', false);
    }
}

// ── 6. Load History ──────────────────────────────────
async function loadHistory() {
    const userId    = document.getElementById('filterUserId')?.value?.trim();
    const direction = document.getElementById('filterDirection')?.value;
    const tbody     = document.getElementById('history-tbody');
    const countEl   = document.getElementById('record-count');
    if (!tbody) return;

    setSpinner('spinner', true);
    tbody.innerHTML = '<tr class="empty-row"><td colspan="7">Loading...</td></tr>';

    let url = '/share';
    if (userId) {
        if (direction === 'to')   url = `/share/to/${userId}`;
        else if (direction === 'from') url = `/share/from/${userId}`;
    }

    try {
        const res  = await fetch(url);
        const data = await res.json();

        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="7">No records found</td></tr>';
            if (countEl) countEl.textContent = '0 records';
            return;
        }

        if (countEl) countEl.textContent = `${data.length} record${data.length !== 1 ? 's' : ''}`;
        tbody.innerHTML = data.map(s => `
            <tr>
                <td>${s.id}</td>
                <td><strong>${s.book.title}</strong></td>
                <td>${s.fromUser.name}</td>
                <td>${s.toUser.name}</td>
                <td>${s.shareDate}</td>
                <td>${s.returnDate || '—'}</td>
                <td>${statusBadge(s.status)}</td>
            </tr>`).join('');

    } catch (e) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="7">Error loading</td></tr>';
    } finally {
        setSpinner('spinner', false);
    }
}

// ── 7. Load Borrowed ─────────────────────────────────
async function loadBorrowed() {
    const userId  = document.getElementById('userId')?.value?.trim();
    const tbody   = document.getElementById('borrowed-tbody');
    const countEl = document.getElementById('borrowed-count');
    const titleEl = document.getElementById('borrowed-title');

    if (!userId) { showAlert('alert', 'Please enter a User ID.', 'error'); return; }

    setSpinner('spinner', true);
    if (tbody) tbody.innerHTML = '<tr class="empty-row"><td colspan="7">Loading...</td></tr>';

    try {
        const res  = await fetch(`/share/borrowed/${userId}`);
        const data = await res.json();

        if (!res.ok) { showAlert('alert', data.message || 'Failed to load.', 'error'); return; }
        if (titleEl) titleEl.textContent = `Borrowed by User #${userId}`;

        if (!Array.isArray(data) || !data.length) {
            if (tbody)   tbody.innerHTML = '<tr class="empty-row"><td colspan="7">No borrowed books</td></tr>';
            if (countEl) countEl.textContent = '0 books';
            return;
        }

        if (countEl) countEl.textContent = `${data.length} book${data.length !== 1 ? 's' : ''}`;
        if (tbody) tbody.innerHTML = data.map(s => `
            <tr>
                <td>${s.id}</td>
                <td><strong>${s.book.title}</strong></td>
                <td>${s.book.author}</td>
                <td>${s.fromUser.name}</td>
                <td>${s.shareDate}</td>
                <td>${statusBadge(s.status)}</td>
                <td><button class="btn btn-danger btn-sm" onclick="quickReturn(${s.id})">Return</button></td>
            </tr>`).join('');

    } catch (e) {
        if (tbody) tbody.innerHTML = '<tr class="empty-row"><td colspan="7">Error</td></tr>';
    } finally {
        setSpinner('spinner', false);
    }
}

// ── 8. Load Active Shares (return.html) ──────────────
async function loadActiveShares() {
    const userId = document.getElementById('borrowUserId')?.value?.trim();
    const tbody  = document.getElementById('active-tbody');
    if (!tbody) return;

    tbody.innerHTML = '<tr class="empty-row"><td colspan="7">Loading...</td></tr>';
    const url = userId ? `/share/borrowed/${userId}` : '/share';

    try {
        const res     = await fetch(url);
        const data    = await res.json();
        const records = userId
            ? data
            : (Array.isArray(data) ? data.filter(s => s.status === 'SHARED') : []);

        if (!records.length) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="7">No active borrows</td></tr>';
            return;
        }

        tbody.innerHTML = records.map(s => `
            <tr>
                <td>${s.id}</td>
                <td><strong>${s.book.title}</strong></td>
                <td>${s.fromUser.name}</td>
                <td>${s.toUser.name}</td>
                <td>${s.shareDate}</td>
                <td>${statusBadge(s.status)}</td>
                <td><button class="btn btn-danger btn-sm" onclick="quickReturn(${s.id})">Return</button></td>
            </tr>`).join('');

    } catch (e) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="7">Error</td></tr>';
    }
}

// ── 9. Quick Return (inline button) ──────────────────
async function quickReturn(shareId) {
    if (!confirm(`Return Share ID ${shareId}?`)) return;
    try {
        const res  = await fetch(`/share/return/${shareId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await res.json();
        if (!res.ok) { alert(data.message || 'Failed.'); return; }
        alert(`✓ "${data.book.title}" returned!`);
        if (document.getElementById('active-tbody'))   loadActiveShares();
        if (document.getElementById('borrowed-tbody')) loadBorrowed();
    } catch (e) { alert('Could not connect.'); }
}

// ── 10. Borrowed check (sharebook.html) ──────────────
async function loadBorrowedByUser() {
    const userId = document.getElementById('checkUserId')?.value?.trim();
    const tbody  = document.getElementById('borrowed-tbody');
    if (!tbody) return;
    if (!userId) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="5">Enter a user ID first</td></tr>';
        return;
    }
    tbody.innerHTML = '<tr class="empty-row"><td colspan="5">Loading...</td></tr>';
    try {
        const res  = await fetch(`/share/borrowed/${userId}`);
        const data = await res.json();
        if (!Array.isArray(data) || !data.length) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="5">No borrowed books</td></tr>';
            return;
        }
        tbody.innerHTML = data.map(s => `
            <tr>
                <td>${s.id}</td>
                <td><strong>${s.book.title}</strong></td>
                <td>${s.fromUser.name}</td>
                <td>${s.shareDate}</td>
                <td>${statusBadge(s.status)}</td>
            </tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="5">Error</td></tr>';
    }
}
