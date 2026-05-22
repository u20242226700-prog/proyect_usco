// HabiliUSCO - app.js

const TOKEN_KEY = 'habiliusco_token';

function saveToken(token) { localStorage.setItem(TOKEN_KEY, token); }
function getToken() { return localStorage.getItem(TOKEN_KEY); }
function removeToken() { localStorage.removeItem(TOKEN_KEY); }

function setTokenCookie() {
    const token = getToken();
    if (token) {
        document.cookie = `jwt_token=${token}; path=/; SameSite=Lax`;
    }
}

function logout() {
    removeToken();
    document.cookie = 'jwt_token=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT';
    window.location.href = '/login';
}

// Set cookie on every page load
setTokenCookie();

// ===== LOGIN =====
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = loginForm.querySelector('button[type=submit]');
        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = 'Ingresando...';
        const errorDiv = document.getElementById('loginError');
        if (errorDiv) errorDiv.style.display = 'none';

        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: document.getElementById('username').value,
                    password: document.getElementById('password').value
                })
            });
            if (res.ok) {
                const json = await res.json();
                saveToken(json.token);
                setTokenCookie();
                window.location.href = '/portal';
            } else {
                if (errorDiv) { errorDiv.textContent = '❌ Usuario o contraseña incorrectos'; errorDiv.style.display = 'flex'; }
                btn.disabled = false;
                btn.textContent = originalText;
            }
        } catch (err) {
            if (errorDiv) { errorDiv.textContent = '❌ Error de conexión'; errorDiv.style.display = 'flex'; }
            btn.disabled = false;
            btn.textContent = originalText;
        }
    });
}

// ===== REGISTRO =====
const registroForm = document.getElementById('registroForm');
if (registroForm) {
    registroForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = registroForm.querySelector('button[type=submit]');
        btn.disabled = true;
        btn.textContent = 'Creando cuenta...';
        const errorDiv = document.getElementById('registroError');
        const successDiv = document.getElementById('registroSuccess');
        if (errorDiv) errorDiv.style.display = 'none';

        try {
            const res = await fetch('/api/auth/registro', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: document.getElementById('username').value,
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value,
                    nombreCompleto: document.getElementById('nombreCompleto').value,
                    telefono: document.getElementById('telefono')?.value || '',
                    facultad: document.getElementById('facultad')?.value || '',
                    programa: document.getElementById('programa')?.value || ''
                })
            });
            const text = await res.text();
            if (res.ok) {
                if (successDiv) successDiv.style.display = 'flex';
                setTimeout(() => window.location.href = '/login', 2000);
            } else {
                if (errorDiv) { errorDiv.textContent = '❌ ' + text; errorDiv.style.display = 'flex'; }
                btn.disabled = false;
                btn.textContent = 'Crear cuenta';
            }
        } catch (err) {
            if (errorDiv) { errorDiv.textContent = '❌ Error de conexión'; errorDiv.style.display = 'flex'; }
            btn.disabled = false;
            btn.textContent = 'Crear cuenta';
        }
    });
}

// ===== DATEPICKER OCCUPIED DATES =====
const dateInput = document.getElementById('fechaReserva');
if (dateInput) {
    const today = new Date().toISOString().split('T')[0];
    dateInput.min = today;
    dateInput.addEventListener('input', () => {
        const selected = dateInput.value;
        const isOcupada = (window.fechasOcupadas || []).includes(selected);
        const hint = document.getElementById('dateHint');
        const btn = document.getElementById('btnSolicitar');
        if (isOcupada) {
            dateInput.style.borderColor = '#DC2626';
            dateInput.style.background = '#FFF5F5';
            if (hint) { hint.textContent = '⚠ Esta fecha ya está ocupada. Escoge otra.'; hint.style.color = '#DC2626'; }
            if (btn) btn.disabled = true;
        } else if (selected) {
            dateInput.style.borderColor = '#16A34A';
            dateInput.style.background = '#F0FFF4';
            if (hint) { hint.textContent = '✅ Fecha disponible'; hint.style.color = '#16A34A'; }
            if (btn) btn.disabled = false;
        }
    });
}

// ===== AUTO SCROLL CHAT =====
const chatMessages = document.querySelector('.chat-messages');
if (chatMessages) chatMessages.scrollTop = chatMessages.scrollHeight;

// ===== AUTO RESIZE TEXTAREA =====
document.querySelectorAll('textarea.auto-resize').forEach(ta => {
    ta.addEventListener('input', () => {
        ta.style.height = 'auto';
        ta.style.height = Math.min(ta.scrollHeight, 150) + 'px';
    });
    ta.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            ta.closest('form')?.requestSubmit();
        }
    });
});

// ===== CONFIRM DIALOGS =====
document.querySelectorAll('[data-confirm]').forEach(el => {
    el.addEventListener('click', (e) => {
        if (!confirm(el.dataset.confirm)) e.preventDefault();
    });
});
