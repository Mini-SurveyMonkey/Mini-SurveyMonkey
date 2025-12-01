// ===============================
// Theme toggle + avatar dropdown
// ===============================
document.addEventListener('DOMContentLoaded', () => {
    const root = document.documentElement;
    const themeBtn = document.getElementById('themeToggle');

    const applyIcon = () => {
        const isDark = root.getAttribute('data-theme') === 'dark';
        if (themeBtn) themeBtn.textContent = isDark ? '☀️' : '🌙';
    };
    applyIcon();

    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            const isDark = root.getAttribute('data-theme') === 'dark';

            if (isDark) {
                root.removeAttribute('data-theme');
                localStorage.setItem('msm-theme', 'light');
            } else {
                root.setAttribute('data-theme', 'dark');
                localStorage.setItem('msm-theme', 'dark');
            }
            applyIcon();
        });
    }

    const userMenu = document.querySelector('.user-menu');
    if (userMenu) {
        const avatarBtn = userMenu.querySelector('.avatar-btn');
        avatarBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            userMenu.classList.toggle('open');
        });

        document.addEventListener('click', () => {
            userMenu.classList.remove('open');
        });
    }
});

// ===============================
// Inline message helper
// ===============================
function showInlineMessage(message, type = 'success') {
    const box = document.getElementById('surveysMessage');
    if (!box) return;

    box.textContent = message || '';
    box.className = 'inline-message show ' + type;

    clearTimeout(box._timeout);
    box._timeout = setTimeout(() => {
        box.className = 'inline-message';
        box.textContent = '';
    }, 3500);
}

// ===============================
// Custom confirm modal helper
// ===============================
function showConfirmModal({ title, body, confirmText = 'Confirm', destructive = false }) {
    return new Promise((resolve) => {
        const backdrop = document.getElementById('app-modal-backdrop');
        const titleEl = document.getElementById('app-modal-title');
        const bodyEl = document.getElementById('app-modal-body');
        const confirmBtn = document.getElementById('app-modal-confirm');
        const cancelBtn = document.getElementById('app-modal-cancel');

        titleEl.textContent = title || 'Are you sure?';
        bodyEl.textContent = body || '';
        confirmBtn.textContent = confirmText;
        confirmBtn.classList.toggle('destructive', !!destructive);

        backdrop.classList.add('show');

        const close = (result) => {
            backdrop.classList.remove('show');
            confirmBtn.removeEventListener('click', onConfirm);
            cancelBtn.removeEventListener('click', onCancel);
            backdrop.removeEventListener('click', onBackdrop);
            resolve(result);
        };

        const onConfirm = (e) => {
            e.stopPropagation();
            close(true);
        };
        const onCancel = (e) => {
            e.stopPropagation();
            close(false);
        };
        const onBackdrop = (e) => {
            if (e.target === backdrop) close(false);
        };

        confirmBtn.addEventListener('click', onConfirm);
        cancelBtn.addEventListener('click', onCancel);
        backdrop.addEventListener('click', onBackdrop);
    });
}