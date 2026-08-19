window.BASE_URL = "file:///android_asset";

function verificarStatusLogin() {
    const authSection = document.getElementById('auth-section');
    const menuConfig = document.getElementById('menu-configuracoes');

    let sessao = null;
    try { sessao = JSON.parse(localStorage.getItem('sessaoGeTech') || 'null'); } catch(e) {}
    const estaLogado = !!(sessao && sessao.loginAtivo === true);
    const emailUsuario = localStorage.getItem('usuarioAtual');

    if (menuConfig) menuConfig.style.display = estaLogado ? "inline-block" : "none";
    if (!authSection) return;

    if (estaLogado && emailUsuario) {
        const listaUsuarios = JSON.parse(localStorage.getItem('usuarios') || '[]');
        const usuarioLogado = listaUsuarios.find(u => u.email === emailUsuario);
        const nomeExibicao = usuarioLogado?.nome || emailUsuario.split('@')[0];
        authSection.innerHTML = `
            <div class="user-info">
                <span class="user-logged">Bem-vindo, <strong>${nomeExibicao}</strong>!</span>
                <button onclick="logout()" class="btn-logout">Sair</button>
            </div>`;
    } else {
        authSection.innerHTML = `
            <div class="auth-buttons">
                <a href="${window.BASE_URL}/site/public/pages/login.html" class="btn-login">Entrar</a>
                <a href="${window.BASE_URL}/site/public/pages/login.html" class="btn-cadastro">Cadastrar</a>
            </div>`;
    }
}

function logout() {
    localStorage.removeItem('logado');
    localStorage.removeItem('usuarioAtual');
    localStorage.removeItem('sessaoGeTech');
    window.location.href = `${window.BASE_URL}/site/public/pages/index.html`;
}

function redirecionarUsuario() {
    let sessao = null;
    try { sessao = JSON.parse(localStorage.getItem('sessaoGeTech') || 'null'); } catch(e) {}

    if (sessao?.loginAtivo && sessao.perfil === 'gestor') {
        window.location.href = `${window.BASE_URL}/site/app/app.html`;
    } else if (sessao?.loginAtivo && sessao.perfil === 'cliente') {
        window.location.href = `${window.BASE_URL}/site/public/pages/cliente.html`;
    } else {
        window.location.href = `${window.BASE_URL}/site/public/pages/login.html`;
    }
}
