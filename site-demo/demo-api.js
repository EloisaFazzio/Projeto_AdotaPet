"use strict";
// AdotaPet GitHub Pages: demonstração visual, sem autenticação, gravação ou conexão ao banco.
window.ADOTAPET_DEMO = true;
(() => {
 const abrigos = [
 {id:1,nome:"DEMO — Lar Amigo Jaraguá",cidade:"Jaraguá do Sul",uf:"SC"},
 {id:2,nome:"DEMO — Patas de Schroeder",cidade:"Schroeder",uf:"SC"},
 {id:3,nome:"DEMO — Rede Animal Guaramirim",cidade:"Guaramirim",uf:"SC"},
 {id:4,nome:"DEMO — Cuidado em Corupá",cidade:"Corupá",uf:"SC"}
 ];
 const lista = [
 ["Butterfly Paraguaia","CAO","FEMEA","PEQUENO",0,"Cheia de carisma, adora atenção."],
 ["Zé do Caixão","CAO","MACHO","GRANDE",1,"Brincalhão e cheio de energia."],
 ["Nazaré","GATO","FEMEA","PEQUENO",2,"Curiosa, observadora e carinhosa."],
 ["Botox","GATO","MACHO","MEDIO",3,"Sério nas fotos, tranquilo na vida."],
 ["Credosvaldo","GATO","MACHO","PEQUENO",0,"Adora explorar todos os cantinhos."],
 ["Zeca Padoguinho","CAO","MACHO","PEQUENO",1,"Fiscal do café da manhã."],
 ["Fiapo","GATO","FEMEA","PEQUENO",2,"Rápida quando ouve a ração."],
 ["Pinga","OUTRA","FEMEA","PEQUENO",3,"Coelhinha curiosa."],
 ["Vendida","CAO","FEMEA","MEDIO",0,"Companheira e sociável."],
 ["Batmiau","GATO","MACHO","MEDIO",1,"Defende o sofá nas horas vagas."],
 ["Ximico e Ximiquinho","CAO","MACHO","MEDIO",2,"Uma dupla cheia de histórias."],
 ["Sol","OUTRA","MACHO","PEQUENO",3,"Porquinho-da-índia cheio de personalidade."],
 ["Chiquinha","CAO","FEMEA","PEQUENO",0,"Gosta de colo e de carinho."],
 ["Viagra","GATO","MACHO","PEQUENO",1,"Sempre pronto para brincar."],
 ["Banguela","CAO","FEMEA","MEDIO",2,"Um sorriso inesquecível."],
 ["Piolho Negão","GATO","MACHO","PEQUENO",3,"Tranquilo e companheiro."]
 ].map((p,i)=>({id:i+1,nome:p[0],especie:p[1],sexo:p[2],porte:p[3],abrigo:abrigos[p[4]].nome,cidade:abrigos[p[4]].cidade,status:"DISPONIVEL",raca:"SRD",dataNascimento:"2024-03-01",descricao:p[5]}));
 const fetchOriginal=window.fetch.bind(window);
 window.fetch=(input,options)=>{
  const url=typeof input==="string"?input:input?.url||"";
  if(!url.startsWith("/api/"))return fetchOriginal(input,options);
  const resposta=(data,status=200)=>Promise.resolve(new Response(JSON.stringify(data),{status,headers:{"Content-Type":"application/json"}}));
  if(options?.method&&options.method.toUpperCase()!=="GET")return resposta({erro:"Demonstração visual: cadastros e solicitações reais estão desativados."},503);
  if(url.startsWith("/api/abrigos"))return resposta(abrigos);
  if(url.startsWith("/api/animais?")){
   const p=new URLSearchParams(url.split("?")[1]||"");
   const dados=lista.filter(x=>(!p.get("especie")||p.get("especie")===x.especie)&&(!p.get("porte")||p.get("porte")===x.porte)&&(!p.get("sexo")||p.get("sexo")===x.sexo)&&(!p.get("cidade")||x.cidade.toLocaleLowerCase("pt-BR").includes(p.get("cidade").toLocaleLowerCase("pt-BR"))));
   const size=Math.max(1,Math.min(100,Number(p.get("size"))||8));const page=Math.max(0,Number(p.get("page"))||0);
   return resposta({content:dados.slice(page*size,(page+1)*size),totalPages:Math.ceil(dados.length/size),totalElements:dados.length,number:page,size});
  }
  if(url.startsWith("/api/conteudo"))return resposta({},404);
  return resposta({erro:"Funcionalidade indisponível nesta demonstração estática."},503);
 };
 function informar(){
  let el=document.getElementById("aviso-demo-acao");
  if(!el){el=document.createElement("div");el.id="aviso-demo-acao";el.setAttribute("role","status");el.style.cssText="position:fixed;bottom:24px;left:50%;transform:translateX(-50%);max-width:90vw;z-index:9999;padding:16px 21px;background:#083c34;color:white;border-radius:14px;box-shadow:0 12px 35px #0004;font:600 14px system-ui;text-align:center";document.body.append(el)}
  el.textContent="Esta é uma demonstração visual gratuita. Cadastros, login e solicitações exigem o sistema completo.";
  clearTimeout(window.__demoTimeout);window.__demoTimeout=setTimeout(()=>el.remove(),6500);
 }
 document.addEventListener("click",event=>{
   const target=event.target.closest("#btn-account,#btn-staff,#btn-requests,[data-adopt],a[href='/painel.html']");
   if(target){event.preventDefault();event.stopImmediatePropagation();informar();}
 },true);
 document.addEventListener("DOMContentLoaded",()=>{
  try{sessionStorage.removeItem("adotapet-sessao")}catch{}
  const n=document.createElement("div");
  n.setAttribute("role","note");n.style.cssText="background:#f2e8bd;color:#3b3926;font:600 13px system-ui;text-align:center;padding:9px 18px";
  n.textContent="DEMONSTRAÇÃO ACADÊMICA GRATUITA · Dados fictícios · Login e adoção reais indisponíveis";
  document.body.prepend(n);
 });
})();
