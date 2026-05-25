// HabiliUSCO - app.js (Session-based, no JWT)

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

// ===== STAR RATING =====
function setStars(val) {
    const input = document.getElementById('calificacionInput');
    if (input) input.value = val;
    document.querySelectorAll('.star-btn').forEach((btn, i) => {
        btn.style.color = i < val ? 'var(--gold)' : '#D1D5DB';
    });
}
if (document.getElementById('calificacionInput')) setStars(5);

// ===== BLOQUEO FECHA =====
const fechaBloqueoInput = document.getElementById('fechaBloqueo');
if (fechaBloqueoInput) {
    fechaBloqueoInput.min = new Date().toISOString().split('T')[0];
}

// ===== AUTO ALERTS DISMISS =====
document.querySelectorAll('.alert').forEach(alert => {
    setTimeout(() => {
        alert.style.transition = 'opacity 0.5s';
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 500);
    }, 4000);
});
