// Componentes públicos do Site C.
// O APK usa fallback embutido porque fetch() de componentes via file:// pode ser bloqueado pelo WebView.
window.BASE_URL = "file:///android_asset";

const HEADER_LOCAL = `<header class="header">
    <div class="logo">
        <h2>GeTech - ERP Industrial</h2>
    </div>

    <nav class="menu">
        <a href="index.html">Início</a>
        <a href="sobre.html">Sobre</a>
        <a href="funcionalidades.html">Funcionalidades</a>
        <a href="planos.html">Planos</a>
        <a href="depoimentos.html">Depoimentos</a>
        <a href="integração.html">Integrações</a>
        <a href="FAQ.html">FAQ</a>
        <a href="blog.html">Blog</a>
        <a href="contato.html">Contato</a>
        <a href="politica-privacidade.html">Política de Privacidade</a>
        <a href="teste_aumentada.html">Realidade Aumentada</a>
        <a href="../../Site%20C/pages/chat.html">Chatbot</a>
        <a href="configuracoes.html" id="menu-configuracoes">Configurações</a>
        <a href="../pages/redimencionamento_site.html"><span>Ge</span>Tech</a>
        <div class="theme-toggle-wrap">
    <span class="theme-icon" id="themeIcon">🌙</span>
    <button class="theme-toggle" id="themeToggle" aria-label="Ativar tema claro"></button>
</div>
    </nav>

    <div class="auth" id="auth-section">
        <a href="login.html" class="btn-login">Entrar</a>
        <a href="login.html" class="btn-cadastro">Cadastrar</a>
    </div>
</header>`;
const FOOTER_LOCAL = `<footer class="footer">
    <div class="footer-container">
        <!-- TUDO que deve ficar lado a lado entra aqui -->
        <div class="footer-content">
            <div class="footer-column">
                <h3>Ge<span>Tech</span></h3>
            </div>  

            <nav class="footer-nav">
                <a href="../pages/index.html">Início</a>
                <a href="../pages/sobre.html">Sobre</a>
                <a href="../pages/contato.html">Contato</a>
                <a href="../pages/politica-privacidade.html">Privacidade</a>
            </nav>

            <!-- Movido para dentro do footer-content -->
            <div class="suporte">
                <h4>Su<span>porte</span></h4>
                <a href="../pages/ajuda.html">Ajuda</a>
                <a href="../pages/FAQ.html">FAQ</a>
            </div>
        </div>

        <div class="footer-bottom"> 
            <p class="copy">Ge<span>Tech</span> &copy; 2026. Todos os direitos reservados.</p>
        </div>
    </div>
</footer>`;

function carregarComponente(id, arquivo, fallback) {
    const el = document.getElementById(id);
    if (!el) return Promise.resolve();
    return fetch(arquivo)
        .then(response => {
            if (!response.ok) throw new Error("HTTP " + response.status);
            return response.text();
        })
        .then(data => { el.innerHTML = data; })
        .catch(() => { el.innerHTML = fallback; });
}

(function () {
    const saved = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', saved);
})();

function inicializarToggleTema() {
    const btn = document.getElementById('themeToggle');
    const icon = document.getElementById('themeIcon');
    if (!btn) return;

    const aplicarTema = (tema) => {
        document.documentElement.setAttribute('data-theme', tema);
        localStorage.setItem('theme', tema);
        if (icon) icon.textContent = tema === 'dark' ? '🌙' : '☀️';
        btn.setAttribute('aria-label', tema === 'dark' ? 'Ativar tema claro' : 'Ativar tema escuro');
    };
    aplicarTema(localStorage.getItem('theme') || 'dark');
    btn.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme');
        aplicarTema(current === 'dark' ? 'light' : 'dark');
    });
}

document.addEventListener("DOMContentLoaded", () => {
    carregarComponente('header', '../components/header.html', HEADER_LOCAL).then(() => {
        inicializarToggleTema();
        if (typeof verificarStatusLogin === 'function') verificarStatusLogin();
    });
    carregarComponente('footer', '../components/footer.html', FOOTER_LOCAL);
});
