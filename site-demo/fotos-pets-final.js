"use strict";
/* Uma única associação de fotos para catálogo, galeria e administração. */
(function(){
  const fotos = new Map([
    ["Butterfly Paraguaia","butterfly-paraguaia.webp"],
    ["Zé do Caixão","ze-do-caixao.webp"],
    ["Nazaré","nazare.webp"],
    ["Botox","botox.webp"],
    ["Credosvaldo","credosvaldo.webp"],
    ["Zeca Padoguinho","zeca-padoguinho.webp"],
    ["Fiapo","fiapo.webp"],
    ["Pinga","pinga.webp"],
    ["Vendida","vendida.webp"],
    ["Batmiau","batmiau.webp"],
    ["Ximico e Ximiquinho","ximico-e-ximquinho.webp"],
    ["Sol","sol.webp"],
    ["Chiquinha","chiquinha.webp"],
    ["Viagra","viagra.webp"],
    ["Banguela","banguela.webp"],
    ["Piolho Negão","piolho-negao.webp"]
  ].map(([nome,arquivo])=>[nome.toLocaleLowerCase('pt-BR'),'imagens-pets/'+arquivo]));
  const arquivo = nome=>fotos.get(String(nome||'').trim().toLocaleLowerCase('pt-BR'))||null;
  function atribuirFoto(img,nome,id){
    const reserva=arquivo(nome);
    img.onerror=()=>{
      if(reserva && img.getAttribute('src')!==reserva){img.onerror=()=>{img.hidden=true};img.src=reserva;}
      else img.hidden=true;
    };
    if(id && !window.ADOTAPET_DEMO)img.src='/api/animais/'+encodeURIComponent(id)+'/imagem';
    else if(reserva)img.src=reserva;
    else img.hidden=true;
  }
  window.AdotaPetRetratos={arquivo,nomeExibido:n=>n,atribuirFoto};
  const grade=document.getElementById('pet-grid');if(!grade)return;
  function sincronizar(){
    for(const card of grade.querySelectorAll('.pet-card')){
      if(card.dataset.fotoFinal==='1')continue;
      const titulo=card.querySelector('h3'),retrato=card.querySelector('.pet-portrait'),botao=card.querySelector('[data-adopt]');
      if(!titulo||!retrato||!botao)continue;
      retrato.querySelectorAll('img.foto-catalogo,img.pet-photo,img.foto-animal-v2,img.foto-animal-v3,.pet-photo-credit').forEach(e=>e.remove());
      const imagem=document.createElement('img');imagem.className='foto-animal-final';imagem.alt='Foto de '+titulo.textContent.trim();imagem.loading='lazy';
      imagem.addEventListener('load',()=>retrato.querySelectorAll('.pet-emoji,.pet-symbol').forEach(e=>e.hidden=true));
      retrato.append(imagem);card.dataset.fotoFinal='1';atribuirFoto(imagem,titulo.textContent.trim(),botao.dataset.adopt);
    }
  }
  new MutationObserver(sincronizar).observe(grade,{childList:true});sincronizar();
})();
