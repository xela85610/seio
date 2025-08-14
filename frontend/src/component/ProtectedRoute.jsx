import { Navigate } from 'react-router-dom';
import { isAuthenticated, getCurrentUser } from '../service/Auth';

export default function ProtectedRoute({ children, role }) {
    if (!isAuthenticated()) return <Navigate to="/login" replace />;
    const me = getCurrentUser();
    if (role && me?.role !== role) {
        // Redirige vers la page correspondant à son rôle
        return <Navigate to={me?.role === 'ADMIN' ? '/admin' : '/membre'} replace />;
    }
    return children;
}
