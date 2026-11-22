"use strict";
/* Exibe fotos de referencia em perfis de apresentacao sem alterar registros de outras entidades. */
(function catalogoComFotos() {
  const root = document.getElementById("pet-grid");
  if (!root) return;
  const abrigosDeApresentacao = new Set(["Lar Amigo Jaraguá", "Patas de Schroeder", "Rede Animal Guaramirim", "Cuidado em Corupá"]);
  const pets = [
    ["Luna", "Safadinha", "safadinha.webp", "Cheia de carisma, adora atenção e está sempre pronta para fazer novas amizades."],
    ["Thor", "Zé do Caixão", "ze-do-caixao.webp", "O nome é assustador, mas quem manda nele é o barulho do aspirador."],
    ["Mimi", "Nazaré", "nazare.webp", "Analisa a situação com os olhos arregalados e exige um sachê antes de responder."],
    ["Bento", "Botox", "botox.webp", "Dono de uma expressão séria e de um talento impressionante para tirar sonecas."],
    ["Nina", "Dona Lambida", "dona-lambida.webp", "Recepciona visitas com alegria e uma quantidade indefinida de beijinhos."],
    ["Tobias", "Zeca Padoguinho", "zeca-padoguinho.webp", "Fiscaliza o café da manhã e considera qualquer petisco assunto de primeira importância."],
    ["Amora", "Fiapo", "fiapo.webp", "Silenciosa até abrir o pacote de ração: aí ninguém segura a velocidade."],
    ["Pipoca", "Pinga", "pinga.webp", "Uma coelhinha curiosa, especialista em saltos surpresa e verduras fresquinhas."],
    ["Mel", "Pipoca Turbo", "pipoca-turbo.webp", "Ouviu a palavra passeio e já começou a corrida pela casa."],
    ["Simba", "Batmiau", "batmiau.webp", "Defende o sofá nas horas vagas e desaparece quando alguém abre o pote de remédio."],
    ["Fred", "Ximico e Ximiquinho", "ximico-e-ximquinho.webp", "Dupla inseparável: um inventa a bagunça e o outro finge que não viu."],
    ["Sol", "Sol", "sol.webp", "Um porquinho-da-índia com personalidade de astro e preferência por verduras crocantes."],
    ["Bela", "Dona Bagunça", "dona-bagunca.webp", "Especialista em reorganizar almofadas e colocar a culpa em todo mundo."],
    ["Chico", "Viagra", "viagra.webp", "Tem tanta energia que considera três da manhã o horário ideal para brincar."],
    ["Dora", "Banguela", "banguela.webp", "Um sorriso inesquecível, alegria de sobra e zero preocupação com a opinião alheia."],
    ["Oliver", "Pilho", "pilho.webp", "Desaparece por horas, reaparece dentro da caixa e age como se nada tivesse acontecido."]
  ];
  const porNome = new Map();
  for (const item of pets) {porNome.set(item[0], item); porNome.set(item[1], item);}
  // Alias de uma revisao intermediaria: nao mostra o nome anterior do gato mascarado.
  porNome.set("Credosvaldo", pets[9]);
  function atualizar() {
    for (const card of root.querySelectorAll(".pet-card")) {
      const nomeEl = card.querySelector("h3");
      const abrigo = (card.dataset.abrigo || "").replace(/^DEMO\s*[—-]\s*/u, "").trim();
      if (!nomeEl || !abrigosDeApresentacao.has(abrigo)) continue;
      const info = porNome.get(nomeEl.textContent.trim());
      if (!info) continue;
      const [antigo, novo, arquivo, resumo] = info;
      if (nomeEl.textContent !== novo) nomeEl.textContent = novo;
      const descricao = card.querySelector(".desc");
      if (descricao && descricao.textContent !== resumo) descricao.textContent = resumo;
      const retrato = card.querySelector(".pet-portrait");
      if (!retrato) continue;
      const src = arquivo.startsWith("https://") ? arquivo : "imagens-pets/" + arquivo;
      let imagem = retrato.querySelector("img.foto-catalogo");
      if (!imagem) {
        retrato.querySelectorAll(".pet-photo,.pet-photo-credit").forEach(el=>el.remove());
        imagem = document.createElement("img");
        imagem.className = "foto-catalogo";
        imagem.loading="lazy";
        imagem.decoding="async";
        imagem.alt="Fotografia de referência para o perfil de " + novo;
        const simbolo = retrato.querySelector(".pet-emoji");
        imagem.addEventListener("load",()=>{if(simbolo) simbolo.hidden=true;});
        imagem.addEventListener("error",()=>{imagem.hidden=true; if(simbolo) simbolo.hidden=false;});
        retrato.appendChild(imagem);
      }
      if (imagem.getAttribute("src")!==src) imagem.src=src;
    }
  }
  let pendente=false;
  new MutationObserver(()=>{
    if(pendente) return; pendente=true;
    queueMicrotask(()=>{pendente=false;atualizar();});
  }).observe(root,{childList:true});
  atualizar();
})();
