// Autenticação local do GeTech — sem Firebase.
window.BASE_URL = "file:///android_asset";

function cadastrar() {
    const email = document.getElementById('cadEmail').value.trim().toLowerCase();
    const senha = document.getElementById('cadSenha').value;
    const msg = document.getElementById('mensagem-cad');
    msg.innerText = '';

    if (!email || !senha) { mostrarMensagem(msg, 'Preencha todos os campos do cadastro!', 'erro'); return; }
    if (senha.length < 6) { mostrarMensagem(msg, 'A senha deve ter pelo menos 6 caracteres.', 'erro'); return; }

    let usuarios = JSON.parse(localStorage.getItem('usuarios') || '[]');
    if (usuarios.find(u => u.email.toLowerCase() === email)) {
        mostrarMensagem(msg, 'Este email já está cadastrado!', 'erro'); return;
    }

    usuarios.push({ email, senha, nome: email.split("@")[0], perfil: "cliente" });
    localStorage.setItem('usuarios', JSON.stringify(usuarios));
    mostrarMensagem(msg, 'Cadastro realizado com sucesso! Faça o login.', 'sucesso');

    document.getElementById('cadEmail').value = '';
    document.getElementById('cadSenha').value = '';

    setTimeout(() => {
        const btn = document.querySelectorAll('.tab-btn')[0];
        if (typeof switchTab === 'function') switchTab('login', btn);
    }, 1500);
}

function garantirUsuariosDemo() {
    let usuarios = JSON.parse(localStorage.getItem('usuarios') || '[]');
    const demos = [
        {email:'gestor@getech.local', senha:'123456', nome:'Gestor GeTech', perfil:'gestor'},
        {email:'cliente@getech.local', senha:'123456', nome:'Cliente GeTech', perfil:'cliente'}
    ];
    let alterado = false;
    demos.forEach(d => {
        if (!usuarios.some(u => u.email.toLowerCase() === d.email)) {
            usuarios.push(d);
            alterado = true;
        }
    });
    if (alterado || !localStorage.getItem('usuarios')) localStorage.setItem('usuarios', JSON.stringify(usuarios));
    return usuarios;
}

function fazerLogin() {
    const email = document.getElementById('loginEmail').value.trim().toLowerCase();
    const senha = document.getElementById('loginSenha').value;
    const msg = document.getElementById('mensagem');
    msg.innerText = '';

    if (!email || !senha) { mostrarMensagem(msg, 'Preencha todos os campos do login!', 'erro'); return; }

    const usuarios = garantirUsuariosDemo();
    const usuario = usuarios.find(u => u.email.toLowerCase() === email && u.senha === senha);

    if (!usuario) { mostrarMensagem(msg, 'Email ou senha incorretos!', 'erro'); return; }

    localStorage.setItem('logado', 'true');
    localStorage.setItem('usuarioAtual', usuario.email);
    localStorage.setItem('sessaoGeTech', JSON.stringify({
        loginAtivo: true,
        email: usuario.email,
        perfil: usuario.perfil
    }));

    mostrarMensagem(msg, 'Login realizado com sucesso!', 'sucesso');

    setTimeout(() => {
        if (usuario.perfil === 'gestor') {
            window.location.href = `${window.BASE_URL}/site/app/app.html`;
        } else {
            window.location.href = `${window.BASE_URL}/site/public/pages/cliente.html`;
        }
    }, 500);
}

function mostrarMensagem(elemento, texto, tipo) {
    elemento.innerText = texto;
    elemento.style.color = tipo === 'sucesso' ? 'var(--accent-green)' : '#f87171';
}

function togglePasswordVisibility(inputId, buttonElement) {
    const inputField = document.getElementById(inputId);
    if (!inputField) return;
    if (inputField.type === 'password') {
        inputField.type = 'text';
        buttonElement.textContent = '⊘';
        buttonElement.setAttribute('aria-label', 'Esconder senha');
    } else {
        inputField.type = 'password';
        buttonElement.textContent = '◉';
        buttonElement.setAttribute('aria-label', 'Mostrar senha');
    }
}
