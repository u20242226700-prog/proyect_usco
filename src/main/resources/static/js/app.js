// HabiliUSCO - app.js

// JWT helpers
const TOKEN_KEY = 'habiliusco_token';

function saveToken(token) { localStorage.setItem(TOKEN_KEY, token); }
function getToken() { return localStorage.getItem(TOKEN_KEY); }
function removeToken() { localStorage.removeItem(TOKEN_KEY); }

function authHeaders() {
    const token = getToken();
    return token ? { 'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

// Auto-set auth header in all fetch calls
async function apiPost(url, data) {
    const res = await fetch(url, {
        method: 'POST',
        headers: authHeaders(),
        body: JSON.stringify(data)
    });
    return res;
}

// Login form handler
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = loginForm.querySelector('button[type=submit]');
        btn.disabled = true;
        btn.textContent = 'Ingresando...';
        const errorDiv = document.getElementById('loginError');
        if (errorDiv) errorDiv.style.display = 'none';

        const data = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        };

        try {
            const res = await apiPost('/api/auth/login', data);
            if (res.ok) {
                const json = await res.json();
                saveToken(json.token);
                window.location.href = '/portal';
            } else {
                const text = await res.text();
                if (errorDiv) { errorDiv.textContent = 'Usuario o contraseña incorrectos'; errorDiv.style.display = 'flex'; }
            }
        } catch (err) {
            if (errorDiv) { errorDiv.textContent = 'Error de conexión'; errorDiv.style.display = 'flex'; }
        } finally {
            btn.disabled = false;
            btn.textContent = 'Iniciar Sesión';
        }
    });
}

// Registro form handler
const registroForm = document.getElementById('registroForm');
if (registroForm) {
    registroForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = registroForm.querySelector('button[type=submit]');
        btn.disabled = true;
        const errorDiv = document.getElementById('registroError');
        const successDiv = document.getElementById('registroSuccess');
        if (errorDiv) errorDiv.style.display = 'none';

        const data = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            nombreCompleto: document.getElementById('nombreCompleto').value,
            telefono: document.getElementById('telefono').value,
            facultad: document.getElementById('facultad').value,
            programa: document.getElementById('programa').value
        };

        try {
            const res = await apiPost('/api/auth/registro', data);
            const text = await res.text();
            if (res.ok) {
                if (successDiv) { successDiv.style.display = 'flex'; }
                setTimeout(() => window.location.href = '/login', 2000);
            } else {
                if (errorDiv) { errorDiv.textContent = text; errorDiv.style.display = 'flex'; }
                btn.disabled = false;
            }
        } catch (err) {
            if (errorDiv) { errorDiv.textContent = 'Error de conexión'; errorDiv.style.display = 'flex'; }
            btn.disabled = false;
        }
    });
}

// Inject JWT token into all Thymeleaf form actions via hidden field
// For protected pages: add token to all form submissions and links
function injectToken() {
    const token = getToken();
    if (!token) return;

    // Add Authorization header to all form submissions
    document.querySelectorAll('form[data-auth]').forEach(form => {
        form.addEventListener('submit', (e) => {
            // Forms go through normal submission; token handled by cookie alternative
        });
    });
}

// Check if token exists for protected pages
function checkAuth() {
    const token = getToken();
    const publicPaths = ['/login', '/registro'];
    const path = window.location.pathname;
    if (!publicPaths.some(p => path.startsWith(p)) && !token && path !== '/') {
        window.location.href = '/login';
        return false;
    }
    return true;
}

// Logout
function logout() {
    removeToken();
    window.location.href = '/login';
}

// Auto-expand textarea
document.querySelectorAll('textarea.auto-resize').forEach(ta => {
    ta.addEventListener('input', () => {
        ta.style.height = 'auto';
        ta.style.height = ta.scrollHeight + 'px';
    });
});

// Blocked dates for datepicker
window.fechasOcupadas = window.fechasOcupadas || [];

const dateInput = document.getElementById('fechaReserva');
if (dateInput) {
    const today = new Date().toISOString().split('T')[0];
    dateInput.min = today;

    dateInput.addEventListener('input', () => {
        const selected = dateInput.value;
        const isOcupada = window.fechasOcupadas.includes(selected);
        const hint = document.getElementById('dateHint');
        if (isOcupada) {
            dateInput.style.borderColor = '#E53E3E';
            dateInput.style.background = '#FFF5F5';
            if (hint) { hint.textContent = '⚠ Esta fecha ya está ocupada. Escoge otra.'; hint.style.color = '#E53E3E'; }
            document.getElementById('btnSolicitar').disabled = true;
        } else {
            dateInput.style.borderColor = '';
            dateInput.style.background = '';
            if (hint) { hint.textContent = '✅ Fecha disponible'; hint.style.color = '#065F46'; }
            document.getElementById('btnSolicitar').disabled = false;
        }
    });
}

// Scroll chat to bottom
const chatMessages = document.querySelector('.chat-messages');
if (chatMessages) {
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Token injection for Thymeleaf secured routes
// We set the token as cookie for server-side JWT filter to work with form submissions
function setTokenCookie() {
    const token = getToken();
    if (token) {
        document.cookie = `jwt_token=${token}; path=/; SameSite=Lax`;
    }
}
setTokenCookie();
