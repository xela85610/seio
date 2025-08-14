// src/components/RequireAuth.jsx
import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../AuthContext'; // adapte le chemin

export default function RequireAuth({ children }) {
    const { user, loading } = useAuth(); // loading = true tant que l'auth est en cours d'initialisation
    const location = useLocation();

    // 1) Si on est en train de charger l'auth, afficher quelque chose (ou null)
    if (loading) {
        return <div>Chargement…</div>;
    }

    // 2) Si pas d'utilisateur après chargement, renvoyer vers /login
    if (!user) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // 3) Sinon on rend les enfants (la route protégée)
    return children;
}
