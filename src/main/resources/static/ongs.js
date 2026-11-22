"use strict";
// Fotos de ambientação: NÃO são fotos autenticadas das instalações.
(async function mostrarRedeComFotos(){
  const raiz=document.getElementById("rede-ongs");
  if(!raiz) return;
  const arquivoPorAbrigo={
    "Lar Amigo Jaraguá":"lar-amigo-jaragua.webp",
    "Patas de Schroeder":"patas-de-schroeder.webp",
    "Rede Animal Guaramirim":"rede-animal-guaramirim.webp",
    "Cuidado em Corupá":"cuidado-em-corupa.webp"
  };
  const limpar=nome=>String(nome||"").replace(/^DEMO\s*[—-]\s*/u,"").trim();
  try{
    const resposta=await fetch("/api/abrigos");
    if(!resposta.ok) throw Error("HTTP "+resposta.status);
    const abrigos=await resposta.json();
    raiz.replaceChildren();
    if(!abrigos.length){raiz.textContent="Ainda não há abrigos cadastrados.";return;}
    for(const abrigo of abrigos){
      const nome=limpar(abrigo.nome);
      const item=document.createElement("article");item.className="ong-card ong-card-com-foto";
      const foto=document.createElement("img");foto.className="ong-imagem";
      foto.loading="lazy";foto.decoding="async";
      foto.src="/fotos-ongs/"+(arquivoPorAbrigo[nome]||"lar-amigo-jaragua.webp");
      foto.alt="Fotografia de ambientação de um espaço de acolhimento animal";
      const conteudo=document.createElement("div");conteudo.className="ong-informacoes";
      const titulo=document.createElement("h3");titulo.textContent=nome;
      const cidade=document.createElement("p");cidade.textContent=abrigo.cidade+" / "+abrigo.uf;
      const descricao=document.createElement("small");descricao.textContent="Acolhimento e proteção animal";
      conteudo.append(titulo,cidade,descricao);item.append(foto,conteudo);raiz.append(item);
    }
    let nota=document.getElementById("ong-fotos-nota");
    if(!nota){nota=document.createElement("p");nota.id="ong-fotos-nota";nota.className="ong-fotos-nota";raiz.after(nota);}
    nota.textContent="";
  }catch(err){raiz.textContent="Não foi possível carregar os abrigos neste momento.";}
})();
