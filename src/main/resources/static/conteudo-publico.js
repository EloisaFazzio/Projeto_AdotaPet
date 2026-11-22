"use strict";
(async function aplicarConteudo() {
  try {
    const resposta = await fetch("/api/conteudo");
    if (!resposta.ok) return;
    const conteudo = await resposta.json();
    const texto = [
      [".hero-copy h1", conteudo.titulo],
      [".hero-copy > p", conteudo.apresentacao],
      [".mission-copy > p", conteudo.missao]
    ];
    for(const [seletor,valor] of texto) {
      const elemento = document.querySelector(seletor);
      if(elemento && valor) elemento.textContent = valor;
    }
    for(const [seletor,valor] of [[".hero-photo",conteudo.imagemHero],[".mission-visual img",conteudo.imagemMissao]]) {
      const imagem = document.querySelector(seletor);
      if(imagem && typeof valor==="string" && /^https:\/\//.test(valor)) imagem.src=valor;
    }
  } catch { /* Mantém a página com os textos padrão caso a API esteja indisponível. */ }
})();
