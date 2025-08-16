import { apiGet } from './Api';

function encodeBasic(username, password) {
    const creds = `${username}:${password}`;
    // safe UTF-8 encode pour btoa
    try {
        return 'Basic ' + window.btoa(unescape(encodeURIComponent(creds)));
    } catch (e) {
        // fallback plus moderne si disponible
        try {
            const encoder = new TextEncoder();
            const bytes = Array.from(encoder.encode(creds));
            const bin = String.fromCharCode(...bytes);
            return 'Basic ' + window.btoa(bin);
        } catch (e2) {
            console.error('Failed to encode credentials', e, e2);
            throw e;
        }
    }
}

export async function login(username, password) {
    console.log('[Auth.login] username=', username);
    const basic = encodeBasic(username, password);
    const me = await apiGet('/auth/me', { Authorization: basic });
    sessionStorage.setItem('auth_basic', basic);
    sessionStorage.setItem('me', JSON.stringify(me));
    return me;
}

export function logout() {
    sessionStorage.removeItem('auth_basic');
    sessionStorage.removeItem('me');
}

export function getCurrentUser() {
    const raw = sessionStorage.getItem('me');
    return raw ? JSON.parse(raw) : null;
}

export function isAuthenticated() {
    return !!sessionStorage.getItem('auth_basic');
}
