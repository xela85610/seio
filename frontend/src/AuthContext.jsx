import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Simule récupération dans sessionStorage
        const saved = sessionStorage.getItem('me');
        if (saved) setUser(JSON.parse(saved));
        setLoading(false);
    }, []);

    const login = (me) => {
        setUser(me);
        sessionStorage.setItem('me', JSON.stringify(me));
    };

    const logout = () => {
        setUser(null);
        sessionStorage.removeItem('me');
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout, setUser }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
