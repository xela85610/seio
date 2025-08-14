// src/pages/Login.jsx
import { useState } from 'react';
import { login } from '../service/Auth';
import { useNavigate, useLocation } from 'react-router-dom';
import '../style/Login.css';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPwd, setShowPwd] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const location = useLocation();
    // si l'utilisateur était redirigé vers /login, location.state.from contient la cible
    const from = location.state?.from?.pathname ?? null;

    const onSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const me = await login(username.trim(), password);

            if (from) {
                console.log('login success me=', me);
                navigate(from || (me.role === 'ADMIN' ? '/admin' : '/user'), { replace: true });
                console.log('called navigate');
                return;
            }

            const target = me.role === 'ADMIN' ? '/admin' : '/user';
            navigate(target, { replace: true });
        } catch (err) {
            setError(err?.body?.error || err?.message || 'Identifiants invalides');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <form className="login-card" onSubmit={onSubmit}>
                <h1>Se connecter</h1>
                <label>
                    Nom d’utilisateur
                    <input
                        type="text"
                        autoFocus
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="admin ou membre"
                        required
                    />
                </label>

                <label>
                    Mot de passe
                    <div className="pwd-row">
                        <input
                            type={showPwd ? 'text' : 'password'}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="*******"
                            required
                        />
                        <button
                            type="button"
                            className="toggle"
                            onClick={() => setShowPwd((v) => !v)}
                            aria-label="Afficher/Masquer le mot de passe"
                        >
                            {showPwd ? '🙈' : '👁️'}
                        </button>
                    </div>
                </label>

                {error && <div className="error">{error}</div>}

                <button type="submit" disabled={loading}>
                    {loading ? 'Connexion…' : 'Se connecter'}
                </button>
            </form>
        </div>
    );
}
