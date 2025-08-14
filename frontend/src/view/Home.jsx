export default function Home() {
    return (
        <div className="container">
            <section className="hero">
                <h1>Bienvenue sur SEIO</h1>
            </section>

            <section id="status" className="card">
                <h2>Statut</h2>
            </section>

            <section id="features" className="grid">
                <div className="card">
                    <h3>Rapide</h3>
                </div>
                <div className="card">
                    <h3>Claire</h3>
                </div>
                <div className="card">
                    <h3>Évolutive</h3>
                </div>
            </section>

            <section id="contact" className="card">
                <h2>Contact</h2>
                <p>Ajoute ici un e‑mail, un lien GitHub, etc.</p>
            </section>
        </div>
    );
}
