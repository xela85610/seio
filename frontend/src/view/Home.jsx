import React from "react";

export default function Home() {
    return (
        <>
            <section id="accueil" className="hero container">
                <h1>S’entraîner à la trésorerie avec les bg</h1>
                <p className="subtitle">
                    Perfectionnez vos compétences et maîtrisez les processus clés grâce à notre plateforme d’entraînement interactive.
                </p>
            </section>

            <section className="grid">
                <article id="questions" className="card">
                    <div className="header">
                        <div className="badge" aria-hidden="true">❓</div>
                        <h3>Questions</h3>
                    </div>
                    <p>Testez vos connaissances sur le légal, les normes et la trésorerie.</p>
                    <div className="actions">
                        <a className="btn primary" href="/questions">Commencer</a>
                        <a className="btn ghost" href="/questions/advanced">Paramètres avancés</a>
                    </div>
                </article>

                <article id="processus" className="card">
                    <div className="header">
                        <div className="badge green" aria-hidden="true">📘</div>
                        <h3>Processus</h3>
                    </div>
                    <p>Maîtrisez les processus en reconstituant l’ordre des étapes.</p>
                    <div className="actions">
                        <a className="btn primary" href="/processus">Choisir un processus</a>
                    </div>
                </article>
            </section>
        </>
    );
}
