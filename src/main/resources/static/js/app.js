// ═══════════════════════════════════════════════════
//  app.js — BookShare API functions
// ═══════════════════════════════════════════════════


// ===================================================
// HELPERS
// ===================================================

function showAlert(id, msg, type = 'success') {

    const el = document.getElementById(id);

    if (!el) return;

    el.textContent = msg;

    el.className = `alert alert-${type} show`;

    setTimeout(() => {

        el.className = 'alert';

    }, 5000);
}


function setSpinner(id, on) {

    const el = document.getElementById(id);

    if (el) {

        el.className = on
            ? 'spinner show'
            : 'spinner';

    }
}


function statusBadge(s) {

    if (s === 'SHARED') {

        return '<span class="badge badge-shared">SHARED</span>';

    }

    if (s === 'RETURNED') {

        return '<span class="badge badge-returned">RETURNED</span>';

    }

    return `<span class="badge">${s}</span>`;
}



// ===================================================
// 1. REGISTER USER
// ===================================================

async function registerUser() {

    const name =
        document.getElementById('name')?.value?.trim();

    const email =
        document.getElementById('email')?.value?.trim();

    const password =
        document.getElementById('password')?.value?.trim();


    if (!name || !email || !password) {

        showAlert(
            'alert',
            'All fields are required.',
            'error'
        );

        return;
    }


    setSpinner('spinner', true);


    try {

        const res = await fetch(
            '/users/register',
            {
                method: 'POST',

                headers: {
                    'Content-Type': 'application/json'
                },

                body: JSON.stringify({
                    name,
                    email,
                    password
                })
            }
        );


        const data = await res.json();


        if (!res.ok) {

            showAlert(
                'alert',
                data.message || 'Registration failed.',
                'error'
            );

            return;
        }


        showAlert(
            'alert',
            `User "${data.name}" registered! ID: ${data.id}`,
            'success'
        );


        document.getElementById(
            'result-card'
        ).style.display = 'block';


        document.getElementById(
            'res-id'
        ).textContent = data.id;


        document.getElementById(
            'res-name'
        ).textContent = data.name;


        document.getElementById(
            'res-email'
        ).textContent = data.email;


        document.getElementById('name').value = '';

        document.getElementById('email').value = '';

        document.getElementById('password').value = '';


    } catch (e) {

        console.error(e);

        showAlert(
            'alert',
            'Could not connect to server.',
            'error'
        );

    } finally {

        setSpinner('spinner', false);

    }
}



// ===================================================
// 2. ADD BOOK
// ===================================================

async function addBook() {

    const title =
        document.getElementById('title')?.value?.trim();

    const author =
        document.getElementById('author')?.value?.trim();

    const userId =
        document.getElementById('userId')?.value?.trim();

    const fileInput =
        document.getElementById('bookFile');


    if (!title || !author || !userId) {

        showAlert(
            'alert',
            'Title, author and user ID are required.',
            'error'
        );

        return;
    }


    setSpinner('spinner', true);


    try {

        // Create multipart form data

        const formData = new FormData();


        formData.append(
            'title',
            title
        );


        formData.append(
            'author',
            author
        );


        // Add image if selected

        if (
            fileInput &&
            fileInput.files.length > 0
        ) {

            formData.append(
                'file',
                fileInput.files[0]
            );

        }


        // Send multipart request

        const res = await fetch(
            `/books/add-with-image/${userId}`,
            {
                method: 'POST',
                body: formData
            }
        );


        const data = await res.json();


        if (!res.ok) {

            showAlert(
                'alert',
                data.message || 'Failed to add book.',
                'error'
            );

            return;
        }


        showAlert(
            'alert',
            `Book "${data.title}" added successfully! ID: ${data.id}`,
            'success'
        );


        // Show result

        document.getElementById(
            'result-card'
        ).style.display = 'block';


        document.getElementById(
            'res-id'
        ).textContent = data.id;


        document.getElementById(
            'res-title'
        ).textContent = data.title;


        document.getElementById(
            'res-author'
        ).textContent = data.author;


        document.getElementById(
            'res-owner'
        ).textContent =
            data.owner
                ? `${data.owner.name} (#${data.owner.id})`
                : '—';


        // Clear form

        document.getElementById('title').value = '';

        document.getElementById('author').value = '';

        document.getElementById('userId').value = '';


        if (fileInput) {

            fileInput.value = '';

        }


        // Reload books

        loadAllBooks();


    } catch (e) {

        console.error(e);


        showAlert(
            'alert',
            'Could not connect to server.',
            'error'
        );

    } finally {

        setSpinner(
            'spinner',
            false
        );

    }
}



// ===================================================
// 3. LOAD ALL BOOKS
// ===================================================

// Stores all books returned from backend

let allBooks = [];


async function loadAllBooks() {

    const tbody =
        document.getElementById('books-tbody');


    if (!tbody) return;


    tbody.innerHTML =
        '<tr class="empty-row">' +
        '<td colspan="5">Loading...</td>' +
        '</tr>';


    try {

        const res =
            await fetch('/books');


        const data =
            await res.json();


        if (!res.ok) {

            throw new Error(
                data.message ||
                'Failed to load books'
            );

        }


        // Make sure data is an array

        allBooks =
            Array.isArray(data)
                ? data
                : [];


        // Display books

        displayBooks(allBooks);


    } catch (e) {

        console.error(
            'Load books error:',
            e
        );


        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="5">Error loading books</td>' +
            '</tr>';

    }
}



// ===================================================
// 4. SEARCH / FILTER BOOKS
// ===================================================

function filterBooks() {

    const input =
        document.getElementById('bookSearch');


    if (!input) return;


    const searchInput =
        input.value
            .toLowerCase()
            .trim();


    // Filter by title OR author

    const filteredBooks =
        allBooks.filter(book => {

            const title =
                (book.title || '')
                    .toLowerCase();


            const author =
                (book.author || '')
                    .toLowerCase();


            return (
                title.includes(searchInput) ||
                author.includes(searchInput)
            );

        });


    // Display filtered books

    displayBooks(filteredBooks);

}



// ===================================================
// 5. DISPLAY BOOKS
// ===================================================

function displayBooks(data) {

    const tbody =
        document.getElementById('books-tbody');


    if (!tbody) return;


    // No results

    if (!data.length) {

        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="5">No books found</td>' +
            '</tr>';

        return;

    }


    // Generate table rows

    tbody.innerHTML =
        data.map(book => `

            <tr>

                <!-- Cover -->

                <td>

                    ${
                        book.imageUrl

                        ?

                        `<img
                            src="${book.imageUrl}"
                            alt="${book.title}"
                            style="
                                width:60px;
                                height:80px;
                                object-fit:cover;
                                border-radius:8px;
                            "
                        >`

                        :

                        'No Image'
                    }

                </td>


                <!-- ID -->

                <td>
                    ${book.id}
                </td>


                <!-- Title -->

                <td>

                    <strong>
                        ${book.title}
                    </strong>

                </td>


                <!-- Author -->

                <td>
                    ${book.author}
                </td>


                <!-- Owner -->

                <td>

                    ${
                        book.owner

                        ?

                        `${book.owner.name}

                        <span
                            style="color:#4a5568"
                        >
                            (#${book.owner.id})
                        </span>`

                        :

                        '—'
                    }

                </td>

            </tr>

        `).join('');

}



// ===================================================
// 6. SHARE BOOK
// ===================================================

async function shareBook() {

    const bookId =
        document.getElementById('bookId')
            ?.value?.trim();


    const toUserId =
        document.getElementById('toUserId')
            ?.value?.trim();


    if (!bookId || !toUserId) {

        showAlert(
            'alert',
            'Both fields are required.',
            'error'
        );

        return;
    }


    setSpinner(
        'spinner',
        true
    );


    try {

        const res =
            await fetch(
                `/share/${bookId}/${toUserId}`,
                {
                    method: 'POST',

                    headers: {
                        'Content-Type':
                            'application/json'
                    }
                }
            );


        const data =
            await res.json();


        if (!res.ok) {

            showAlert(
                'alert',
                data.message ||
                'Failed to share.',
                'error'
            );

            return;
        }


        showAlert(
            'alert',
            `Book shared! Share ID: ${data.id}`,
            'success'
        );


        document.getElementById(
            'result-card'
        ).style.display = 'block';


        document.getElementById(
            'res-id'
        ).textContent = data.id;


        document.getElementById(
            'res-book'
        ).textContent =
            `${data.book.title} (#${data.book.id})`;


        document.getElementById(
            'res-from'
        ).textContent =
            `${data.fromUser.name} (#${data.fromUser.id})`;


        document.getElementById(
            'res-to'
        ).textContent =
            `${data.toUser.name} (#${data.toUser.id})`;


        document.getElementById(
            'res-date'
        ).textContent =
            data.shareDate;


        document.getElementById(
            'res-status'
        ).innerHTML =
            statusBadge(data.status);


        document.getElementById(
            'bookId'
        ).value = '';


        document.getElementById(
            'toUserId'
        ).value = '';


    } catch (e) {

        console.error(e);

        showAlert(
            'alert',
            'Could not connect to server.',
            'error'
        );

    } finally {

        setSpinner(
            'spinner',
            false
        );

    }
}



// ===================================================
// 7. RETURN BOOK
// ===================================================

async function returnBook() {

    const shareId =
        document.getElementById('shareId')
            ?.value?.trim();


    if (!shareId) {

        showAlert(
            'alert',
            'Share ID is required.',
            'error'
        );

        return;
    }


    setSpinner(
        'spinner',
        true
    );


    try {

        const res =
            await fetch(
                `/share/return/${shareId}`,
                {
                    method: 'PUT',

                    headers: {
                        'Content-Type':
                            'application/json'
                    }
                }
            );


        const data =
            await res.json();


        if (!res.ok) {

            showAlert(
                'alert',
                data.message ||
                'Failed to return.',
                'error'
            );

            return;
        }


        showAlert(
            'alert',
            `Book returned! Status: ${data.status}`,
            'success'
        );


        document.getElementById(
            'result-card'
        ).style.display = 'block';


        document.getElementById(
            'res-id'
        ).textContent = data.id;


        document.getElementById(
            'res-book'
        ).textContent =
            `${data.book.title} (#${data.book.id})`;


        document.getElementById(
            'res-from'
        ).textContent =
            `${data.fromUser.name} (#${data.fromUser.id})`;


        document.getElementById(
            'res-to'
        ).textContent =
            `${data.toUser.name} (#${data.toUser.id})`;


        document.getElementById(
            'res-share-date'
        ).textContent =
            data.shareDate;


        document.getElementById(
            'res-return-date'
        ).textContent =
            data.returnDate;


        document.getElementById(
            'res-status'
        ).innerHTML =
            statusBadge(data.status);


        document.getElementById(
            'shareId'
        ).value = '';


        loadActiveShares();


    } catch (e) {

        console.error(e);

        showAlert(
            'alert',
            'Could not connect to server.',
            'error'
        );

    } finally {

        setSpinner(
            'spinner',
            false
        );

    }
}



// ===================================================
// 8. LOAD HISTORY
// ===================================================

async function loadHistory() {

    const userId =
        document.getElementById(
            'filterUserId'
        )?.value?.trim();


    const direction =
        document.getElementById(
            'filterDirection'
        )?.value;


    const tbody =
        document.getElementById(
            'history-tbody'
        );


    const countEl =
        document.getElementById(
            'record-count'
        );


    if (!tbody) return;


    setSpinner(
        'spinner',
        true
    );


    tbody.innerHTML =
        '<tr class="empty-row">' +
        '<td colspan="7">Loading...</td>' +
        '</tr>';


    let url = '/share';


    if (userId) {

        if (direction === 'to') {

            url = `/share/to/${userId}`;

        }

        else if (direction === 'from') {

            url = `/share/from/${userId}`;

        }

    }


    try {

        const res =
            await fetch(url);


        const data =
            await res.json();


        if (
            !Array.isArray(data) ||
            !data.length
        ) {

            tbody.innerHTML =
                '<tr class="empty-row">' +
                '<td colspan="7">No records found</td>' +
                '</tr>';


            if (countEl) {

                countEl.textContent =
                    '0 records';

            }

            return;
        }


        if (countEl) {

            countEl.textContent =
                `${data.length} record${
                    data.length !== 1
                        ? 's'
                        : ''
                }`;

        }


        tbody.innerHTML =
            data.map(s => `

                <tr>

                    <td>
                        ${s.id}
                    </td>

                    <td>
                        <strong>
                            ${s.book.title}
                        </strong>
                    </td>

                    <td>
                        ${s.fromUser.name}
                    </td>

                    <td>
                        ${s.toUser.name}
                    </td>

                    <td>
                        ${s.shareDate}
                    </td>

                    <td>
                        ${s.returnDate || '—'}
                    </td>

                    <td>
                        ${statusBadge(s.status)}
                    </td>

                </tr>

            `).join('');


    } catch (e) {

        console.error(e);

        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="7">Error loading</td>' +
            '</tr>';

    } finally {

        setSpinner(
            'spinner',
            false
        );

    }
}



// ===================================================
// 9. LOAD BORROWED BOOKS
// ===================================================

async function loadBorrowed() {

    const userId =
        document.getElementById(
            'userId'
        )?.value?.trim();


    const tbody =
        document.getElementById(
            'borrowed-tbody'
        );


    const countEl =
        document.getElementById(
            'borrowed-count'
        );


    const titleEl =
        document.getElementById(
            'borrowed-title'
        );


    if (!userId) {

        showAlert(
            'alert',
            'Please enter a User ID.',
            'error'
        );

        return;
    }


    setSpinner(
        'spinner',
        true
    );


    if (tbody) {

        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="7">Loading...</td>' +
            '</tr>';

    }


    try {

        const res =
            await fetch(
                `/share/borrowed/${userId}`
            );


        const data =
            await res.json();


        if (!res.ok) {

            showAlert(
                'alert',
                data.message ||
                'Failed to load.',
                'error'
            );

            return;
        }


        if (titleEl) {

            titleEl.textContent =
                `Borrowed by User #${userId}`;

        }


        if (
            !Array.isArray(data) ||
            !data.length
        ) {

            if (tbody) {

                tbody.innerHTML =
                    '<tr class="empty-row">' +
                    '<td colspan="7">No borrowed books</td>' +
                    '</tr>';

            }


            if (countEl) {

                countEl.textContent =
                    '0 books';

            }

            return;
        }


        if (countEl) {

            countEl.textContent =
                `${data.length} book${
                    data.length !== 1
                        ? 's'
                        : ''
                }`;

        }


        if (tbody) {

            tbody.innerHTML =
                data.map(s => `

                    <tr>

                        <td>
                            ${s.id}
                        </td>

                        <td>
                            <strong>
                                ${s.book.title}
                            </strong>
                        </td>

                        <td>
                            ${s.book.author}
                        </td>

                        <td>
                            ${s.fromUser.name}
                        </td>

                        <td>
                            ${s.shareDate}
                        </td>

                        <td>
                            ${statusBadge(s.status)}
                        </td>

                        <td>

                            <button
                                class="btn btn-danger btn-sm"
                                onclick="quickReturn(${s.id})"
                            >
                                Return
                            </button>

                        </td>

                    </tr>

                `).join('');

        }


    } catch (e) {

        console.error(e);

        if (tbody) {

            tbody.innerHTML =
                '<tr class="empty-row">' +
                '<td colspan="7">Error</td>' +
                '</tr>';

        }

    } finally {

        setSpinner(
            'spinner',
            false
        );

    }
}



// ===================================================
// 10. LOAD ACTIVE SHARES
// ===================================================

async function loadActiveShares() {

    const userId =
        document.getElementById(
            'borrowUserId'
        )?.value?.trim();


    const tbody =
        document.getElementById(
            'active-tbody'
        );


    if (!tbody) return;


    tbody.innerHTML =
        '<tr class="empty-row">' +
        '<td colspan="7">Loading...</td>' +
        '</tr>';


    const url =
        userId
            ? `/share/borrowed/${userId}`
            : '/share';


    try {

        const res =
            await fetch(url);


        const data =
            await res.json();


        const records =
            userId
                ? data
                : (
                    Array.isArray(data)
                        ? data.filter(
                            s =>
                                s.status === 'SHARED'
                        )
                        : []
                );


        if (!records.length) {

            tbody.innerHTML =
                '<tr class="empty-row">' +
                '<td colspan="7">No active borrows</td>' +
                '</tr>';

            return;
        }


        tbody.innerHTML =
            records.map(s => `

                <tr>

                    <td>
                        ${s.id}
                    </td>

                    <td>
                        <strong>
                            ${s.book.title}
                        </strong>
                    </td>

                    <td>
                        ${s.fromUser.name}
                    </td>

                    <td>
                        ${s.toUser.name}
                    </td>

                    <td>
                        ${s.shareDate}
                    </td>

                    <td>
                        ${statusBadge(s.status)}
                    </td>

                    <td>

                        <button
                            class="btn btn-danger btn-sm"
                            onclick="quickReturn(${s.id})"
                        >
                            Return
                        </button>

                    </td>

                </tr>

            `).join('');


    } catch (e) {

        console.error(e);

        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="7">Error</td>' +
            '</tr>';

    }
}



// ===================================================
// 11. QUICK RETURN
// ===================================================

async function quickReturn(shareId) {

    if (
        !confirm(
            `Return Share ID ${shareId}?`
        )
    ) {

        return;

    }


    try {

        const res =
            await fetch(
                `/share/return/${shareId}`,
                {
                    method: 'PUT',

                    headers: {
                        'Content-Type':
                            'application/json'
                    }
                }
            );


        const data =
            await res.json();


        if (!res.ok) {

            alert(
                data.message ||
                'Failed.'
            );

            return;
        }


        alert(
            `✓ "${data.book.title}" returned!`
        );


        if (
            document.getElementById(
                'active-tbody'
            )
        ) {

            loadActiveShares();

        }


        if (
            document.getElementById(
                'borrowed-tbody'
            )
        ) {

            loadBorrowed();

        }


    } catch (e) {

        console.error(e);

        alert(
            'Could not connect.'
        );

    }
}



// ===================================================
// 12. BORROWED CHECK
// ===================================================

async function loadBorrowedByUser() {

    const userId =
        document.getElementById(
            'checkUserId'
        )?.value?.trim();


    const tbody =
        document.getElementById(
            'borrowed-tbody'
        );


    if (!tbody) return;


    if (!userId) {

        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="5">Enter a user ID first</td>' +
            '</tr>';

        return;
    }


    tbody.innerHTML =
        '<tr class="empty-row">' +
        '<td colspan="5">Loading...</td>' +
        '</tr>';


    try {

        const res =
            await fetch(
                `/share/borrowed/${userId}`
            );


        const data =
            await res.json();


        if (
            !Array.isArray(data) ||
            !data.length
        ) {

            tbody.innerHTML =
                '<tr class="empty-row">' +
                '<td colspan="5">No borrowed books</td>' +
                '</tr>';

            return;
        }


        tbody.innerHTML =
            data.map(s => `

                <tr>

                    <td>
                        ${s.id}
                    </td>

                    <td>
                        <strong>
                            ${s.book.title}
                        </strong>
                    </td>

                    <td>
                        ${s.fromUser.name}
                    </td>

                    <td>
                        ${s.shareDate}
                    </td>

                    <td>
                        ${statusBadge(s.status)}
                    </td>

                </tr>

            `).join('');


    } catch (e) {

        console.error(e);

        tbody.innerHTML =
            '<tr class="empty-row">' +
            '<td colspan="5">Error</td>' +
            '</tr>';

    }
}