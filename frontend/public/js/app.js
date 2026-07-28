const API = {
    login: '/api/login',
    orders: '/api/orders',
    history: '/api/orders/history'  // sin parámetro
};

let token = localStorage.getItem('token');
let username = localStorage.getItem('username');

function showLogin() {
    document.getElementById('loginSection').style.display = 'block';
    document.getElementById('orderSection').style.display = 'none';
}
function showDashboard() {
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('orderSection').style.display = 'block';
    loadHistory();
}

// Login
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;
    try {
        const res = await fetch(API.login, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: user, password: pass })
        });
        const data = await res.json();
        if (data.token) {
            token = data.token;
            username = user;
            localStorage.setItem('token', token);
            localStorage.setItem('username', username);
            showDashboard();
        } else {
            document.getElementById('loginError').textContent = 'Invalid credentials';
        }
    } catch (e) {
        document.getElementById('loginError').textContent = 'Connection error';
    }
});

// Logout
document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    token = null;
    username = null;
    showLogin();
    document.getElementById('loginError').textContent = '';
});

// Submit order
document.getElementById('orderForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const symbol = document.getElementById('symbol').value;
    const quantity = document.getElementById('quantity').value;
    const price = document.getElementById('price').value;
    const traderName = document.getElementById('traderName').value;
    try {
        const res = await fetch(API.orders, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ symbol, quantity, price, traderName, token })
        });
        const data = await res.json();
        const resultDiv = document.getElementById('orderResult');
        const isApproved = data.status === 'APPROVED';
        resultDiv.innerHTML = `
            <div class="order-status ${isApproved ? 'status-approved' : 'status-rejected'} fade-in">
                <i class="bi bi-${isApproved ? 'check-circle' : 'x-circle'} me-2"></i>
                <strong>${data.status || 'UNKNOWN'}</strong> ${data.rejectReason ? ': ' + data.rejectReason : ''}
            </div>`;
        loadHistory();
    } catch (e) {
        document.getElementById('orderResult').innerHTML = '<div class="alert alert-danger">Error submitting order</div>';
    }
});

// Load history (sin parámetro, el backend usa el token)
async function loadHistory() {
    if (!token) return;
    try {
        const res = await fetch(API.history, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const orders = await res.json();
        const tbody = document.getElementById('historyBody');
        if (!Array.isArray(orders) || orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">No orders yet</td></tr>';
            return;
        }
        tbody.innerHTML = orders.reverse().map(o => `
            <tr>
                <td>${o.id}</td>
                <td><strong>${o.symbol}</strong></td>
                <td>${o.quantity}</td>
                <td>$${parseFloat(o.price).toFixed(2)}</td>
                <td>${o.traderName}</td>
                <td><span class="badge ${o.status === 'APPROVED' ? 'badge-approved' : 'badge-rejected'}">${o.status || 'N/A'}</span></td>
                <td class="text-muted">${new Date().toLocaleTimeString()}</td>
            </tr>`).join('');
    } catch (e) {
        console.error('History error', e);
        document.getElementById('historyBody').innerHTML = '<tr><td colspan="7" class="text-center text-danger">Failed to load history</td></tr>';
    }
}

// Init
if (token && username) {
    showDashboard();
} else {
    showLogin();
}
