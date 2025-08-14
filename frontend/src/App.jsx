// src/App.jsx
import { Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';
import Login from './view/Login.jsx';
import HomeAdmin from './view/HomeAdmin.jsx';
import HomeUser from './view/HomeUser.jsx';
import NotFound from "./view/NotFound";
import Forbidden from "./view/Forbidden";

// Protège l'accès si non connecté (gère loading)
function RequireAuth() {
    const { user, loading } = useAuth();
    const loc = useLocation();

    if (loading) return <div>Chargement…</div>; // NE PAS rediriger pendant le chargement

    if (!user) return <Navigate to="/login" replace state={{ from: loc }} />;
    return <Outlet />;
}

// Vérifie le rôle (gère loading)
function RequireRole({ allow }) {
    const { user, loading } = useAuth();
    if (loading) return <div>Chargement…</div>;
    if (!user) return <Navigate to="/login" replace />;
    if (!allow.includes(user.role)) return <Navigate to="/" replace />;
    return <Outlet />;
}

// Page racine: redirige en fonction du rôle si connecté
function RootRedirect() {
    const { user, loading } = useAuth();
    if (loading) return <div>Chargement…</div>;
    if (!user) return <Navigate to="/login" replace />;
    return user.role === 'ADMIN'
        ? <Navigate to="/admin" replace />
        : <Navigate to="/user" replace />;
}

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />

            <Route element={<RequireAuth />}>
                <Route element={<RequireRole allow={['ADMIN']} />}>
                    <Route path="/admin" element={<HomeAdmin />} />
                </Route>

                <Route element={<RequireRole allow={['USER']} />}>
                    <Route path="/user" element={<HomeUser />} />
                </Route>
            </Route>

            <Route path="/forbidden" element={<Forbidden />} />
            <Route path="/" element={<RootRedirect />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
    );
}
