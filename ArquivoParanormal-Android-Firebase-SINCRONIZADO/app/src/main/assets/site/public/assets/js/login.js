document.addEventListener("DOMContentLoaded", () => {
     const sessao = JSON.parse(localStorage.getItem('sessaoGeTech'));
    const BASE_URL = "file:///android_asset";

    // Se não houver sessão, se não estiver ativo, ou se o perfil NÃO for gestor
    if (!sessao || !sessao.loginAtivo || sessao.perfil !== 'gestor') {
        alert("Acesso restrito. Apenas gestores podem acessar este painel.");
        // Redireciona para a página de login
        window.location.href = `${BASE_URL}/site/public/pages/index.html`;
        return; // Para a execução do resto do script
    }
});
// =========================
//  CADASTRO
// =========================
function cadastrar() {
    const email   = document.getElementById('cadEmail').value.trim();
    const senha   = document.getElementById('cadSenha').value;
    const msg     = document.getElementById('mensagem-cad');

    msg.innerText = '';

    if (!email || !senha) {
        mostrarMensagem(msg, 'Preencha todos os campos do cadastro!', 'erro');
        return;
    }

    if (senha.length < 6) {
        mostrarMensagem(msg, 'A senha deve ter pelo menos 6 caracteres.', 'erro');
        return;
    }

    let usuarios = JSON.parse(localStorage.getItem('usuarios') || '[]');

    if (usuarios.find(u => u.email === email)) {
        mostrarMensagem(msg, 'Este email já está cadastrado!', 'erro');
        return;
    }

    usuarios.push({ email, senha, nome: email.split("@")[0], perfil: "cliente" });
    localStorage.setItem('usuarios', JSON.stringify(usuarios));

    mostrarMensagem(msg, 'Cadastro realizado com sucesso! Faça o login.', 'sucesso');

    document.getElementById('cadEmail').value = '';
    document.getElementById('cadSenha').value = '';

    // Redireciona para a aba de login após 1.5s
    setTimeout(() => {
        switchTab('login', document.querySelectorAll('.tab-btn')[0]);
    }, 1500);
}


// =========================
//  LOGIN
// =========================

// Modificado para evitar o erro "Identifier 'BASE_URL' has already been declared"
window.BASE_URL = "file:///android_asset";

function fazerLogin() {
    const email = document.getElementById('loginEmail').value.trim().toLowerCase();
    const senha = document.getElementById('loginSenha').value;
    const msg = document.getElementById('mensagem');
    msg.innerText = '';
    if (!email || !senha) { mostrarMensagem(msg, 'Preencha todos os campos do login!', 'erro'); return; }

    let usuarios = JSON.parse(localStorage.getItem('usuarios') || '[]');
    if (!usuarios.length) {
        usuarios = [
            {email:'gestor@getech.local', senha:'123456', nome:'Gestor GeTech', perfil:'gestor'},
            {email:'cliente@getech.local', senha:'123456', nome:'Cliente GeTech', perfil:'cliente'}
        ];
        localStorage.setItem('usuarios', JSON.stringify(usuarios));
    }
    const usuario = usuarios.find(u => u.email.toLowerCase() === email && u.senha === senha);
    if (!usuario) { mostrarMensagem(msg, 'Email ou senha incorretos!', 'erro'); return; }

    localStorage.setItem('logado','true');
    localStorage.setItem('usuarioAtual',usuario.email);
    localStorage.setItem('sessaoGeTech',JSON.stringify({loginAtivo:true,email:usuario.email,perfil:usuario.perfil}));
    mostrarMensagem(msg,'Login realizado com sucesso!','sucesso');

    setTimeout(() => {
        if (usuario.perfil === 'gestor')
            window.location.href = 'file:///android_asset/site/app/app.html';
        else
            window.location.href = 'file:///android_asset/site/public/pages/cliente.html';
    }, 500);
}

// =========================
//  HELPER — exibe mensagem
// =========================
function mostrarMensagem(elemento, texto, tipo) {
    elemento.innerText = texto;
    elemento.style.color = tipo === 'sucesso'
        ? 'var(--accent-green)'
        : '#f87171';
}

// ==========================================
//  ALTERNAR VISIBILIDADE DA SENHA
// ==========================================
function togglePasswordVisibility(inputId, buttonElement) {
    const inputField = document.getElementById(inputId);
    if (!inputField) return;

    if (inputField.type === 'password') {
        inputField.type = 'text';
        buttonElement.textContent = '⊘'; // Ícone/Emoji de ocultar
        buttonElement.setAttribute('aria-label', 'Esconder senha');
    } else {
        inputField.type = 'password';
        buttonElement.textContent = '◉'; // Ícone/Emoji de mostrar
        buttonElement.setAttribute('aria-label', 'Mostrar senha');
    }
}