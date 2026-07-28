const express = require('express');
const axios = require('axios');
const app = express();

app.use(express.json());
app.use(express.static('public', {
    setHeaders: (res, path) => {
        if (path.endsWith('.js')) {
            res.set('Cache-Control', 'no-store, no-cache, must-revalidate, proxy-revalidate');
            res.set('Pragma', 'no-cache');
            res.set('Expires', '0');
        }
    }
}));
app.set('view engine', 'pug');

const API_URL = process.env.API_URL || 'http://app:8080/api';

app.get('/', (req, res) => {
    res.render('index', { title: 'TradeGuard Console' });
});

app.post('/api/login', async (req, res) => {
    try {
        const response = await axios.post(`${API_URL}/auth/login`, req.body);
        res.json(response.data);
    } catch (err) {
        res.status(err.response?.status || 500).json(err.response?.data || { error: 'Login failed' });
    }
});

app.post('/api/orders', async (req, res) => {
    const { token, ...order } = req.body;
    try {
        const response = await axios.post(`${API_URL}/trades`, order, {
            headers: { Authorization: `Bearer ${token}` }
        });
        res.json(response.data);
    } catch (err) {
        res.status(err.response?.status || 500).json(err.response?.data || { error: 'Order failed' });
    }
});

app.get('/api/orders/history', async (req, res) => {
    const token = req.headers.authorization?.split(' ')[1];
    try {
        const response = await axios.get(`${API_URL}/trades/history`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        res.json(response.data);
    } catch (err) {
        res.status(err.response?.status || 500).json(err.response?.data || { error: 'History failed' });
    }
});

app.listen(3000, () => console.log('Frontend proxy running on port 3000'));
