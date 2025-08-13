export default function Home() {
    return (
        <div className="container">
            <section className="hero">
                <h1>Bienvenue sur SEIO</h1>
                <p className="subtitle">Application Spring Boot + React (frontend seul pour l’instant)</p>
            </section>

            <section id="status" className="card">
                <h2>Statut</h2>
                <p>Backend: non démarré (placeholder).</p>
            </section>

            <section id="features" className="grid">
                <div className="card">
                    <h3>Rapide</h3>
                    <p>Stack légère pour démarrer vite.</p>
                </div>
                <div className="card">
                    <h3>Claire</h3>
                    <p>Structure simple: asset / service / view.</p>
                </div>
                <div className="card">
                    <h3>Évolutive</h3>
                    <p>Prête pour connecter des API quand le backend sera dispo.</p>
                </div>
            </section>

            <section id="contact" className="card">
                <h2>Contact</h2>
                <p>Ajoute ici un e‑mail, un lien GitHub, etc.</p>
            </section>
        </div>
    );
}
