"use strict";
(async function(){
 const secao=document.getElementById('galeria-memes'),grade=secao?.querySelector('.meme-grid');
 if(!secao||!grade||!window.AdotaPetRetratos)return;
 try{
  let pagina=0,total=1;const animais=[];
  while(pagina<total&&pagina<15){
    const resp=await fetch('/api/animais?size=36&page='+pagina,{cache:'no-store'});
    if(!resp.ok)throw Error('Catalogo indisponivel');
    const dados=await resp.json();animais.push(...dados.content||[]);total=Number(dados.totalPages)||0;pagina++;
  }
  const preferidos=['Sol','Banguela','Butterfly Paraguaia','Chiquinha','Zé do Caixão','Credosvaldo','Vendida','Piolho Negão','Viagra','Zeca Padoguinho','Ximico e Ximiquinho'];
  animais.sort((a,b)=>{
   const aPos=preferidos.indexOf(a.nome),bPos=preferidos.indexOf(b.nome);
   return (aPos<0?999:aPos)-(bPos<0?999:bPos);
  });
  const disponiveis=animais.filter(a=>a.status==='DISPONIVEL');
  grade.replaceChildren();
  for(const animal of disponiveis){
   const card=document.createElement('figure'),img=document.createElement('img'),legenda=document.createElement('figcaption'),sub=document.createElement('small');
   img.loading='lazy';img.alt='Foto de '+animal.nome;
   window.AdotaPetRetratos.atribuirFoto(img,animal.nome,animal.id);
   legenda.textContent=animal.nome;sub.textContent=animal.descricao||'Conheça o animal e seu perfil.';
   legenda.append(sub);card.append(img,legenda);grade.append(card);
  }
  const aviso=secao.querySelector('.meme-note');if(aviso)aviso.remove();
  secao.hidden=disponiveis.length===0;
 }catch(e){secao.hidden=true;console.warn('Galeria indisponivel',e);}
})();
