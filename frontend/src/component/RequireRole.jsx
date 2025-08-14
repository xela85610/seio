// src/components/RequireRole.jsx
import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../AuthContext';

export default function RequireRole({ children, allowed = [] }) {
    const { user, loading } = useAuth();
    const location = useLocation();

    if (loading) return <div>Chargement…</div>;
    if (!user) return <Navigate to="/login" state={{ from: location }} replace />;

    // Si rôle non autorisé, envoyer sur une page forbidden ou /login
    if (!allowed.includes(user.role)) {
        return <Navigate to="/forbidden" replace />;
    }

    return children;
}
