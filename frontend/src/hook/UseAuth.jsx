import { createContext, useContext, useMemo, useState } from 'react';

const AuthCtx = createContext(null);
export const useAuth = () => useContext(AuthCtx);

// Exemple: stocke le user {role: 'ADMIN' | 'USER'} après login
export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const login = (u) => setUser(u);
    const logout = () => setUser(null);

    const value = useMemo(() => ({ user, login, logout }), [user]);
    return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>;
}