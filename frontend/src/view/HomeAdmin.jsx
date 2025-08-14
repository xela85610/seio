import { logout, getCurrentUser } from '../service/Auth';
import { useNavigate } from 'react-router-dom';

export default function HomeAdmin() {
    const navigate = useNavigate();
    const me = getCurrentUser();
    return (
        <div style={{ padding: 24 }}>
            <h2>Tableau de bord Admin</h2>
            <p>Connecté en tant que {me?.name} ({me?.role}).</p>
            <div style={{ display: 'flex', gap: 8 }}>
                <button onClick={() => { logout(); navigate('/login', { replace: true }); }}>
                    Se déconnecter
                </button>
            </div>
            <hr />
            <p>Ici tu mettras la gestion des questions/processus (endpoints réservés ADMIN).</p>
        </div>
    );
}
