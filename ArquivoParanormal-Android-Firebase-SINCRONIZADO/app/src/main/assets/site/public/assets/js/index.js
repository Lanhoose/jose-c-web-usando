document.addEventListener("DOMContentLoaded", () => {
    // Página pública: visitantes podem acessar o Site C.
    // A proteção de acesso acontece somente ao abrir o painel interno.
    const heroCard = document.querySelector('.hero-card');
    const modulesGrid = document.querySelector('.modules-grid');
    const statsGrid = document.querySelector('.stats-grid');
    const sectionTitles = document.querySelectorAll('.section-title');

    setTimeout(() => {
        if (heroCard) heroCard.classList.remove('hidden-track');
        if (modulesGrid) modulesGrid.classList.remove('hidden-track');
        if (statsGrid) statsGrid.classList.remove('hidden-track');
        sectionTitles.forEach(title => title.classList.remove('hidden-track'));
    }, 200);
});
