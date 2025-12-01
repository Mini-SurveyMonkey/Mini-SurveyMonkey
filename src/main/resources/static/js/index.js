function renderSurveyList(ul, data, isMine, reloadAll) {
    if (!ul) return;

    ul.innerHTML = '';

    if (!Array.isArray(data) || data.length === 0) {
        ul.innerHTML = '<li class="no-surveys muted">No surveys yet.</li>';
        return;
    }

    data.forEach(s => {
        const li = document.createElement('li');
        li.className = 'survey-item';

        const title = document.createElement('div');
        title.className = 'survey-title';
        title.textContent = `#${s.id} — ${s.title || '(untitled survey)'}`;

        const actions = document.createElement('div');
        actions.className = 'survey-actions';

        // Preview
        const preview = document.createElement('a');
        preview.href = `/surveys/${s.id}/preview`;
        preview.textContent = 'Preview';
        preview.className = 'action-link';
        actions.appendChild(preview);

        // Take
        const take = document.createElement('a');
        take.href = `/surveys/${s.id}/response`;
        take.textContent = 'Take';
        take.className = 'action-link primary';
        actions.appendChild(take);

        // Only for your surveys
        if (isMine) {
            // Results
            const results = document.createElement('a');
            results.href = `/surveys/${s.id}/results`;
            results.textContent = 'Results';
            results.className = 'action-link';
            actions.appendChild(results);

            // Close / Reopen
            const toggle = document.createElement('a');
            toggle.textContent = s.closed ? 'Reopen' : 'Close';
            toggle.className = 'action-link';
            toggle.href = '#';
            toggle.onclick = async (e) => {
                e.preventDefault();
                const action = s.closed ? 'reopen' : 'close';
                if (!confirm(`Are you sure you want to ${action} survey #${s.id}?`)) return;

                const r = await fetch(`/surveys/${s.id}/close`, { method: 'POST' });
                if (r.ok) {
                    reloadAll && reloadAll();
                } else {
                    alert('Failed to update survey status');
                }
            };
            actions.appendChild(toggle);

            // Delete
            const del = document.createElement('a');
            del.textContent = 'Delete';
            del.className = 'action-link';
            del.href = '#';
            del.onclick = async (e) => {
                e.preventDefault();
                if (!confirm(`Delete survey #${s.id}?`)) return;

                const r = await fetch(`/surveys/${s.id}`, { method: 'DELETE' });
                if (r.ok) {
                    reloadAll && reloadAll();
                } else {
                    alert('Failed to delete survey');
                }
            };
            actions.appendChild(del);
        }

        // Share
        const share = document.createElement('a');
        share.textContent = 'Share';
        share.className = 'action-link';
        share.href = '#';
        share.onclick = async (e) => {
            e.preventDefault();
            try {
                const r = await fetch(`/surveys/${s.id}/share`);
                if (!r.ok) {
                    alert('Failed to fetch share link');
                    return;
                }
                const link = await r.text();

                if (navigator.clipboard && navigator.clipboard.writeText) {
                    await navigator.clipboard.writeText(link);
                    alert('Share link copied to clipboard:\n' + link);
                } else {
                    prompt('Copy this link:', link);
                }
            } catch (err) {
                alert('Error while generating share link');
            }
        };
        actions.appendChild(share);

        li.appendChild(title);
        li.appendChild(actions);
        ul.appendChild(li);
    });
}

async function fetchAndRender(url, ulId, isMine, reloadAll) {
    const ul = document.getElementById(ulId);
    if (!ul) return;

    ul.innerHTML = '<li class="muted">Loading…</li>';

    try {
        const resp = await fetch(url);
        if (!resp.ok) {
            throw new Error('Failed to load surveys');
        }
        const data = await resp.json();
        renderSurveyList(ul, data, isMine, reloadAll);
    } catch (e) {
        ul.innerHTML = `<li class="error">${e.message}</li>`;
    }
}

async function loadMySurveys() {
    // Your Surveys
    return fetchAndRender('/surveys/mine', 'mySurveysUl', true, loadAllSurveysAndMine);
}

async function loadAllSurveys() {
    // All Surveys: hit /surveys and hide owner-only actions
    return fetchAndRender('/surveys', 'allSurveysUl', false, loadAllSurveysAndMine);
}

async function loadAllSurveysAndMine() {
    await Promise.all([loadMySurveys(), loadAllSurveys()]);
}

window.addEventListener('DOMContentLoaded', () => {
    const myBtn = document.getElementById('refreshMyBtn');
    const allBtn = document.getElementById('refreshAllBtn');

    if (myBtn) {
        myBtn.addEventListener('click', loadMySurveys);
    }
    if (allBtn) {
        allBtn.addEventListener('click', loadAllSurveys);
    }

    // Load both sections
    loadAllSurveysAndMine();
});
