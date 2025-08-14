import Home from "./view/Home";
import "./style/App.css";

export default function App() {
    return (
        <>
            <div className="edge edge-top" aria-hidden="true" />
            <div className="edge edge-bottom" aria-hidden="true" />

            <header className="topbar">
                <div className="container topbar-inner">
                    <a className="brand" href="/">
            <span className="brand-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <rect x="3" y="3" width="7" height="7" rx="2" />
                <rect x="14" y="3" width="7" height="7" rx="2" />
                <rect x="3" y="14" width="7" height="7" rx="2" />
                <rect x="14" y="14" width="7" height="7" rx="2" />
              </svg>
            </span>
                        <span className="brand-text">
              <strong>Pôle Trésorerie</strong>
              <small>Plateforme d’entraînement</small>
            </span>
                    </a>

                    <nav className="nav">
                        <a className="pill active" href="#accueil">Accueil</a>
                        <a className="pill" href="#questions">Questions</a>
                        <a className="pill" href="#processus">Processus</a>
                        <a className="ghost" href="https://github.com/xela85610/seio" target="_blank" rel="noreferrer">GitHub</a>
                    </nav>
                </div>
            </header>

            <main>
                <Home />
            </main>

            <footer className="footer">
                <div className="container">
                    <span>© {new Date().getFullYear()} SEIO</span>
                </div>
            </footer>
        </>
    );
}
