"use strict";
const CAMPOS = [
  ["titulo", "Título da página principal", "text"],
  ["apresentacao", "Texto de apresentação", "textarea"],
  ["missao", "Texto da missão", "textarea"],
  ["imagemHero", "URL HTTPS da imagem de destaque", "url"],
  ["imagemMissao", "URL HTTPS da imagem da missão", "url"]
];
const formulario = document.getElementById("editor-conteudo");
const mensagem = document.getElementById("editor-mensagem");
let sessao;
try {sessao = JSON.parse(sessionStorage.getItem("adotapet-sessao") || "null");} catch {sessao = null;}
const token = sessao && sessao.token;
async function requisicao(url, options = {}) {
  const resposta = await fetch(url, { ...options, headers: {...options.headers, ...(token ? {Authorization:"Bearer " + token} : {})}});
  const dados = await resposta.json().catch(() => ({}));
  if(!resposta.ok) throw Error(dados.erro || "Falha na requisição (HTTP " + resposta.status + ")");
  return dados;
}
async function iniciar() {
  try {
    if(!token) throw Error("Entre com uma conta de administrador no site antes de editar o conteúdo.");
    const usuario = await requisicao("/api/auth/me");
    if(usuario.perfil !== "ADMIN") throw Error("Somente administradores podem editar o conteúdo público.");
    const valores = await requisicao("/api/conteudo");
    CAMPOS.forEach(([chave, rotulo, tipo]) => {
      const grupo = document.createElement("label"); grupo.className = "editor-campo";
      const titulo = document.createElement("span"); titulo.textContent = rotulo;
      const controle = tipo === "textarea" ? document.createElement("textarea") : document.createElement("input");
      if(tipo !== "textarea") controle.type = tipo;
      controle.name = chave; controle.maxLength = 3000; controle.required = true;
      controle.value = valores[chave] || "";
      grupo.append(titulo,controle);formulario.append(grupo);
    });
    const botao = document.createElement("button"); botao.type="submit";botao.className="btn btn-deep";
    botao.textContent="Salvar conteúdo";formulario.append(botao);
    formulario.hidden = false;mensagem.textContent = "Edite textos e URLs das fotos ilustrativas. Alterações são gravadas no banco.";
  } catch(e) { mensagem.textContent = e.message; }
}
formulario.addEventListener("submit", async evento => {
  evento.preventDefault();
  const botao = formulario.querySelector('button[type="submit"]'); botao.disabled=true;
  mensagem.textContent="Salvando alterações...";
  try {
    for(const [chave] of CAMPOS) {
      const valor = formulario.elements[chave].value.trim();
      await requisicao("/api/conteudo/" + chave, {method:"PUT",headers:{"Content-Type":"application/json"},body:JSON.stringify({valor})});
    }
    mensagem.textContent="Conteúdo atualizado. Abra a página principal para conferir.";
  } catch(e) { mensagem.textContent = "Não foi possível concluir: " + e.message; }
  finally {botao.disabled=false;}
});
iniciar();
