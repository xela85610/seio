import Home from './view/Home';
import './style/App.css';

export default function App() {
  return (
      <>
        <header className="topbar">
          <div className="brand">SEIO</div>
          <nav className="nav">
            <a href="https://github.com/xela85610/seio" target="_blank" rel="noreferrer">GitHub</a>
          </nav>
        </header>

        <main>
          <Home />
        </main>

        <footer className="footer">
          <span>© {new Date().getFullYear()} SEIO</span>
        </footer>
      </>
  );
}
