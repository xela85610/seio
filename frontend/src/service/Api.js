const baseURL =
    process.env.REACT_APP_API_BASE_URL ??
    'http://localhost:8080/api';

export function authHeader() {
    const token = sessionStorage.getItem('auth_basic'); // "Basic xxx"
    return token ? { Authorization: token } : {};
}

async function toHttpError(res) {
    let body;
    try {
        const ct = res.headers.get('content-type') || '';
        body = ct.includes('application/json') ? await res.json() : await res.text();
    } catch (e) {
        body = `__UNREADABLE_BODY__ (${e.message})`;
    }
    const err = new Error(typeof body === 'string' ? body : body?.error || res.statusText);
    err.status = res.status;
    err.body = typeof body === 'string' ? { error: body } : body;
    return err;
}

function joinUrl(base, path) {
    return `${base.replace(/\/$/, '')}/${path.replace(/^\//, '')}`;
}

export async function apiGet(path, extraHeaders = {}) {
    const url = joinUrl(baseURL, path);

    const res = await fetch(url, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json', ...authHeader(), ...extraHeaders },
        credentials: 'include',
    });


    if (!res.ok) throw await toHttpError(res);

    const contentType = res.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        try {
            return await res.json();
        } catch (e) {
            throw new Error('INVALID_JSON_RESPONSE');
        }
    } else {
        // pour debug : retournons le texte (HTML) si ce n'est pas du JSON
        const text = await res.text();
        console.warn('[apiGet] response not json (first 500 chars):', text.slice(0, 500));
        throw new Error('UNEXPECTED_RESPONSE_NOT_JSON');
    }
}

export async function apiHead(path, extraHeaders = {}) {
    const url = joinUrl(baseURL, path);
    const res = await fetch(url, {
        method: 'HEAD',
        headers: { ...authHeader(), ...extraHeaders },
        credentials: 'include',
    });
    if (!res.ok) throw await toHttpError(res);
    return true;
}
