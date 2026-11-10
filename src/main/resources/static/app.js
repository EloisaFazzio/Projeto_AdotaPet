"use strict";
/* AdotaPet — frontend sem dados fictícios: todos os perfis vêm da API REST. */
const $ = id => document.getElementById(id);
const labels = {
  CAO:"Cachorro",GATO:"Gato",OUTRA:"Outro",
  MACHO:"Macho",FEMEA:"Fêmea",
  PEQUENO:"Pequeno",MEDIO:"Médio",GRANDE:"Grande",
  PENDENTE:"Pendente",EM_ANALISE:"Em análise",APROVADA:"Aprovada",
  REJEITADA:"Rejeitada",CANCELADA:"Cancelada",DISPONIVEL:"Disponível"
};
const emoji = {CAO:"🐶",GATO:"🐱",OUTRA:"🐾"};
const escapeHtml = value => String(value ?? "").replace(/[&<>"']/g, c =>
  ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
const capitalize = value => labels[value] || value || "—";

let sessao = null;
try { sessao = JSON.parse(sessionStorage.getItem("adotapet-sessao") || "null"); }
catch { sessionStorage.removeItem("adotapet-sessao"); }
let paginaAtual = 0;
let totalPaginas = 0;
let animaisVisiveis = [];
let abrigosCache = [];
let petPendente = null;
let avisoTimer = null;

async function api(url, options = {}) {
  const headers = new Headers(options.headers || {});
  if (options.body) headers.set("Content-Type","application/json");
  if (sessao && sessao.token) headers.set("Authorization","Bearer " + sessao.token);
  let resposta;
  try { resposta = await fetch(url, Object.assign({}, options, {headers})); }
  catch { throw new Error("Sem conexão com o servidor. Verifique se o backend está em execução."); }
  const dados = await resposta.json().catch(() => ({}));
  if (!resposta.ok) {
    if (resposta.status === 401 && sessao) {
      sessao = null;
      sessionStorage.removeItem("adotapet-sessao");
      atualizarSessao();
    }
    throw new Error(dados.erro || (resposta.status === 403
      ? "Você não tem permissão para realizar esta ação."
      : "Não foi possível concluir a operação (HTTP " + resposta.status + ")."));
  }
  return dados;
}

function avisar(texto) {
  const toast = $("toast");
  toast.textContent = texto;
  toast.classList.add("visible");
  clearTimeout(avisoTimer);
  avisoTimer = setTimeout(() => toast.classList.remove("visible"), 5500);
}

function atualizarSessao() {
  const usuario = sessao && sessao.usuario;
  const equipe = usuario && ["ADMIN","VOLUNTARIO"].includes(usuario.perfil);
  $("btn-account").hidden = !!usuario;
  $("btn-logout").hidden = !usuario;
  $("account-name").hidden = !usuario;
  $("account-name").textContent = usuario ? "Olá, " + usuario.nome.split(" ")[0] : "";
  $("btn-requests").hidden = !usuario || usuario.perfil !== "ADOTANTE";
  $("btn-staff").hidden = !equipe;
}

function fecharModal() {
  if ($("app-dialog").open) $("app-dialog").close();
  $("dialog-content").innerHTML = "";
}
function abrirModal(html) {
  $("dialog-content").innerHTML = html;
  if (!$("app-dialog").open) $("app-dialog").showModal();
  const primeiro = $("dialog-content").querySelector("input,textarea,select,button");
  if (primeiro) primeiro.focus();
}
function mensagemForm(texto) {
  const alvo = $("dialog-content").querySelector(".form-response");
  if (alvo) { alvo.hidden = false; alvo.textContent = texto; }
  else avisar(texto);
}
function cabecalhoModal(titulo, descricao = "") {
  return '<div class="dialog-header"><span class="eyebrow">ADOTAPET</span><h2 id="dialog-title">' +
    escapeHtml(titulo) + '</h2>' + (descricao ? '<p>' + escapeHtml(descricao) + '</p>' : "") + '</div>';
}

async function carregarAbrigos() {
  try {
    abrigosCache = await api("/api/abrigos");
    $("count-shelters").textContent = abrigosCache.length;
  } catch {
    $("count-shelters").textContent = "—";
  }
}

function idadeAnimal(dataNascimento) {
  if (!dataNascimento) return "Idade não informada";
  const nascimento = new Date(dataNascimento + "T12:00:00");
  const hoje = new Date();
  if (!Number.isFinite(nascimento.getTime()) || nascimento > hoje) return "Idade não informada";
  let anos = hoje.getFullYear() - nascimento.getFullYear();
  const mes = hoje.getMonth() - nascimento.getMonth();
  if (mes < 0 || (mes === 0 && hoje.getDate() < nascimento.getDate())) anos--;
  if (anos < 1) return "Menos de 1 ano";
  return anos + (anos === 1 ? " ano" : " anos");
}

function cartaoPet(p) {
  return '<article class="pet-card">' +
    '<div class="pet-portrait"><span class="pet-badge">♡ Disponível</span><span class="pet-emoji" aria-hidden="true">' +
    (emoji[p.especie] || "🐾") + '</span></div>' +
    '<div class="pet-content"><h3>' + escapeHtml(p.nome) + '</h3>' +
    '<div class="small-line">' + escapeHtml(idadeAnimal(p.dataNascimento)) + ' · ' + escapeHtml(capitalize(p.sexo)) + '</div>' +
    '<div class="row"><span class="chip">' + escapeHtml(capitalize(p.especie)) +
    '</span><span class="chip">' + escapeHtml(capitalize(p.porte)) + '</span></div>' +
    '<p class="desc">' + escapeHtml(p.descricao || "Conheça a história deste animal e converse com a equipe da ONG.") + '</p>' +
    '<div class="pet-footer"><span class="location">⌖ ' + escapeHtml(p.cidade || "Cidade não informada") +
    '</span><button class="btn btn-deep" type="button" data-adopt="' + p.id + '">Conhecer ↗</button></div></div></article>';
}

async function carregarCatalogo() {
  const feedback = $("catalog-feedback");
  feedback.hidden = false;
  feedback.textContent = "Buscando animais disponíveis...";
  $("pet-grid").innerHTML = "";
  $("pagination").hidden = true;
  const params = new URLSearchParams({page:String(paginaAtual),size:"8"});
  const campos = [
    ["especie",$("filter-species").value],
    ["porte",$("filter-size").value],
    ["sexo",$("filter-sex").value],
    ["cidade",$("filter-city").value.trim()]
  ];
  campos.forEach(([chave,valor]) => { if (valor) params.set(chave,valor); });
  try {
    const dados = await api("/api/animais?" + params.toString());
    animaisVisiveis = dados.content || [];
    totalPaginas = dados.totalPages || 0;
    $("count-animals").textContent = dados.totalElements ?? 0;
    if (animaisVisiveis.length === 0) {
      feedback.innerHTML = '<strong>🐾 Nenhum animal disponível com estes filtros.</strong><p>Experimente mudar a busca ou volte mais tarde.</p>';
      return;
    }
    feedback.hidden = true;
    $("pet-grid").innerHTML = animaisVisiveis.map(cartaoPet).join("");
    if (totalPaginas > 1) {
      $("pagination").hidden = false;
      $("page-label").textContent = "Página " + (paginaAtual + 1) + " de " + totalPaginas;
      $("prev-page").disabled = paginaAtual <= 0;
      $("next-page").disabled = paginaAtual >= totalPaginas - 1;
    }
  } catch (erro) {
    feedback.textContent = erro.message;
    $("count-animals").textContent = "—";
  }
}

function modalAutenticacao(modo = "login") {
  const cadastro = modo === "cadastro";
  abrirModal(cabecalhoModal(cadastro ? "Crie sua conta" : "Bem-vindo de volta!",
    cadastro ? "Cadastre-se como adotante para solicitar um novo amigo." : "Entre para acompanhar suas solicitações.") +
    '<form id="auth-form">' +
    (cadastro ? '<div class="form-field"><label for="auth-name">Nome completo</label><input id="auth-name" autocomplete="name" maxlength="120" required></div>' : "") +
    '<div class="form-field"><label for="auth-email">E-mail</label><input id="auth-email" type="email" autocomplete="email" required maxlength="160"></div>' +
    '<div class="form-field"><label for="auth-password">Senha</label><input id="auth-password" type="password" autocomplete="' +
    (cadastro ? "new-password" : "current-password") + '" required minlength="' + (cadastro ? 8 : 1) + '"></div>' +
    (cadastro ? '<div class="form-grid"><div class="form-field"><label for="auth-city">Cidade (opcional)</label><input id="auth-city" maxlength="80"></div><div class="form-field"><label for="auth-phone">Telefone (opcional)</label><input id="auth-phone" maxlength="20"></div></div>' : "") +
    '<p class="dialog-msg">Nunca informe sua senha a outras pessoas. Somente contas criadas pela equipe podem administrar o sistema.</p>' +
    '<div class="form-actions"><button class="btn btn-deep" type="submit">' +
    (cadastro ? "Criar conta" : "Entrar") + ' ↗</button><button class="text-button" type="button" id="switch-auth">' +
    (cadastro ? "Já tenho uma conta" : "Ainda não tenho conta") + '</button></div>' +
    '<p class="form-response dialog-msg" role="alert" hidden></p></form>');
  $("switch-auth").addEventListener("click", () => modalAutenticacao(cadastro ? "login" : "cadastro"));
  $("auth-form").addEventListener("submit", async event => {
    event.preventDefault();
    const botao = event.target.querySelector('button[type="submit"]');
    botao.disabled = true;
    const dados = {email:$("auth-email").value.trim(),senha:$("auth-password").value};
    if (cadastro) {
      dados.nome = $("auth-name").value.trim();
      dados.cidade = $("auth-city").value.trim();
      dados.telefone = $("auth-phone").value.trim();
    }
    try {
      const resultado = await api("/api/auth/" + (cadastro ? "registrar" : "login"),
        {method:"POST",body:JSON.stringify(dados)});
      sessao = resultado;
      sessionStorage.setItem("adotapet-sessao",JSON.stringify(sessao));
      atualizarSessao();
      fecharModal();
      avisar("Olá, " + resultado.usuario.nome.split(" ")[0] + "! Sua conta está conectada.");
      if (petPendente && resultado.usuario.perfil === "ADOTANTE") {
        const pet = petPendente;
        petPendente = null;
        modalAdocao(pet);
      } else petPendente = null;
    } catch (erro) {
      mensagemForm(erro.message);
    } finally { botao.disabled = false; }
  });
}

function modalAdocao(pet) {
  if (!sessao) {
    petPendente = pet;
    avisar("Entre ou crie uma conta para enviar uma solicitação.");
    modalAutenticacao("login");
    return;
  }
  if (sessao.usuario.perfil !== "ADOTANTE") {
    avisar("Apenas contas do perfil adotante podem solicitar adoção.");
    return;
  }
  abrirModal(cabecalhoModal("Quero adotar " + pet.nome + " ♡",
    "A equipe da ONG analisará sua justificativa antes da adoção ser formalizada.") +
    '<div class="request-item"><h3>' + escapeHtml(pet.nome) + '</h3><p>' +
    escapeHtml(capitalize(pet.especie)) + ' · ' + escapeHtml(capitalize(pet.porte)) + ' · ' +
    escapeHtml(pet.cidade) + '</p></div>' +
    '<form id="adoption-form"><div class="form-field"><label for="adoption-reason">Por que deseja adotar este animal?</label>' +
    '<textarea id="adoption-reason" maxlength="1000" minlength="15" required placeholder="Conte sobre seu lar, rotina e como pretende cuidar dele."></textarea></div>' +
    '<p class="dialog-msg">A solicitação fica registrada com o status PENDENTE. O envio não significa aprovação automática.</p>' +
    '<button type="submit" class="btn btn-deep">Enviar solicitação ↗</button>' +
    '<p class="form-response dialog-msg" role="alert" hidden></p></form>');
  $("adoption-form").addEventListener("submit", async event => {
    event.preventDefault();
    const botao = event.target.querySelector("button[type=submit]");
    botao.disabled = true;
    try {
      await api("/api/solicitacoes",{
        method:"POST",
        body:JSON.stringify({animalId:pet.id,justificativa:$("adoption-reason").value.trim()})
      });
      fecharModal();
      avisar("Sua solicitação foi registrada! Acompanhe em Meus pedidos.");
    } catch (erro) { mensagemForm(erro.message); }
    finally { botao.disabled = false; }
  });
}

async function modalMeusPedidos() {
  if (!sessao || sessao.usuario.perfil !== "ADOTANTE") return;
  abrirModal(cabecalhoModal("Meus pedidos", "Acompanhe o andamento das suas solicitações.") +
    '<div class="dialog-msg">Carregando...</div>');
  try {
    const pedidos = await api("/api/usuarios/me/solicitacoes");
    $("dialog-content").innerHTML = cabecalhoModal("Meus pedidos", "Acompanhe o andamento das suas solicitações.") +
      (pedidos.length ? pedidos.map(p =>
        '<article class="request-item"><h3>' + escapeHtml(p.animal) + '</h3><span class="status">' +
        escapeHtml(capitalize(p.status)) + '</span><p>Solicitado em: ' +
        escapeHtml(new Date(p.solicitadaEm).toLocaleDateString("pt-BR")) + '</p>' +
        '<p><b>Justificativa:</b> ' + escapeHtml(p.justificativa) + '</p>' +
        (p.parecer ? '<p><b>Parecer:</b> ' + escapeHtml(p.parecer) + '</p>' : "") +
        (p.status === "PENDENTE" ? '<button type="button" class="btn btn-outline btn-small" data-cancel="' +
          p.id + '">Cancelar pedido</button>' : "") + '</article>').join("") :
        '<p class="dialog-msg">Você ainda não possui solicitações. Conheça os animais disponíveis!</p>');
    $("dialog-content").querySelectorAll("[data-cancel]").forEach(botao => botao.addEventListener("click", async () => {
      if (!confirm("Deseja cancelar este pedido pendente?")) return;
      try {
        await api("/api/solicitacoes/" + botao.dataset.cancel + "/cancelar",{method:"PATCH"});
        avisar("Pedido cancelado.");
        await modalMeusPedidos();
        await carregarCatalogo();
      } catch (erro) { avisar(erro.message); }
    }));
  } catch (erro) { mensagemForm(erro.message); }
}

function selectAbrigos() {
  return abrigosCache.map(a => '<option value="' + a.id + '">' + escapeHtml(a.nome) +
    ' — ' + escapeHtml(a.cidade) + '</option>').join("");
}
async function modalEquipe() {
  if (!sessao || !["ADMIN","VOLUNTARIO"].includes(sessao.usuario.perfil)) return;
  await carregarAbrigos();
  abrirModal(cabecalhoModal("Área da ONG", "Cadastre animais e gerencie solicitações de adoção.") +
    (sessao.usuario.perfil === "ADMIN" ?
      '<details class="request-item"><summary><b>+ Cadastrar novo abrigo</b></summary><form id="shelter-form">' +
      '<div class="form-grid"><div class="form-field"><label for="s-name">Nome</label><input id="s-name" required maxlength="120"></div>' +
      '<div class="form-field"><label for="s-city">Cidade</label><input id="s-city" required maxlength="80"></div></div>' +
      '<div class="form-field"><label for="s-address">Endereço</label><input id="s-address" required maxlength="200"></div>' +
      '<div class="form-grid"><div class="form-field"><label for="s-uf">UF</label><input id="s-uf" required minlength="2" maxlength="2" placeholder="SC"></div>' +
      '<div class="form-field"><label for="s-capacity">Capacidade</label><input id="s-capacity" type="number" min="1" required></div></div>' +
      '<button class="btn btn-deep" type="submit">Cadastrar abrigo</button></form></details>' : "") +
    '<details class="request-item"><summary><b>+ Cadastrar animal</b></summary><form id="pet-form">' +
    '<div class="form-grid"><div class="form-field"><label for="p-name">Nome</label><input id="p-name" required maxlength="80"></div>' +
    '<div class="form-field"><label for="p-shelter">Abrigo</label><select id="p-shelter" required><option value="">Selecione...</option>' +
    selectAbrigos() + '</select></div></div>' +
    '<div class="form-grid"><div class="form-field"><label for="p-species">Espécie</label><select id="p-species"><option value="CAO">Cachorro</option><option value="GATO">Gato</option><option value="OUTRA">Outro</option></select></div>' +
    '<div class="form-field"><label for="p-size">Porte</label><select id="p-size"><option value="PEQUENO">Pequeno</option><option value="MEDIO">Médio</option><option value="GRANDE">Grande</option></select></div></div>' +
    '<div class="form-grid"><div class="form-field"><label for="p-sex">Sexo</label><select id="p-sex"><option value="FEMEA">Fêmea</option><option value="MACHO">Macho</option></select></div>' +
    '<div class="form-field"><label for="p-birth">Nascimento (opcional)</label><input id="p-birth" type="date"></div></div>' +
    '<div class="form-field"><label for="p-description">Descrição (opcional)</label><textarea id="p-description" maxlength="1000"></textarea></div>' +
    '<label><input type="checkbox" id="p-neutered"> Animal castrado</label><div class="form-actions"><button class="btn btn-deep" type="submit">Cadastrar animal</button></div></form></details>' +
    '<h3 style="margin:25px 0 12px">Solicitações recebidas</h3><div id="staff-requests"><p class="dialog-msg">Carregando pedidos...</p></div>' +
    '<p class="form-response dialog-msg" role="alert" hidden></p>');
  if ($("shelter-form")) $("shelter-form").addEventListener("submit", async event => {
    event.preventDefault();
    try {
      await api("/api/abrigos",{method:"POST",body:JSON.stringify({
        nome:$("s-name").value.trim(),endereco:$("s-address").value.trim(),
        cidade:$("s-city").value.trim(),uf:$("s-uf").value.trim().toUpperCase(),
        capacidade:Number($("s-capacity").value)
      })});
      avisar("Abrigo cadastrado.");
      await modalEquipe();
    } catch (erro) { mensagemForm(erro.message); }
  });
  $("pet-form").addEventListener("submit", async event => {
    event.preventDefault();
    try {
      await api("/api/animais",{method:"POST",body:JSON.stringify({
        nome:$("p-name").value.trim(),abrigoId:Number($("p-shelter").value),
        especie:$("p-species").value,porte:$("p-size").value,sexo:$("p-sex").value,
        dataNascimento:$("p-birth").value || null,descricao:$("p-description").value.trim(),
        castrado:$("p-neutered").checked
      })});
      avisar("Animal cadastrado!");
      await modalEquipe();
      await carregarCatalogo();
    } catch (erro) { mensagemForm(erro.message); }
  });
  try {
    const pedidos = await api("/api/solicitacoes");
    $("staff-requests").innerHTML = pedidos.length ? pedidos.map(p =>
      '<article class="request-item"><h3>' + escapeHtml(p.animal) + ' · ' + escapeHtml(p.adotante) +
      '</h3><span class="status">' + escapeHtml(capitalize(p.status)) + '</span>' +
      '<p>' + escapeHtml(p.justificativa) + '</p><div class="actions">' +
      (p.status === "PENDENTE" ? '<button class="btn btn-outline" data-action="analisar" data-id="' + p.id + '">Iniciar análise</button>' : "") +
      (p.status === "EM_ANALISE" ? '<button class="btn btn-deep" data-action="aprovar" data-id="' + p.id + '">Aprovar</button>' : "") +
      (["PENDENTE","EM_ANALISE"].includes(p.status) ?
        '<button class="btn btn-outline" data-action="rejeitar" data-id="' + p.id + '">Rejeitar</button>' : "") +
      '</div></article>').join("") : '<p class="dialog-msg">Nenhuma solicitação recebida por enquanto.</p>';
    $("staff-requests").querySelectorAll("[data-action]").forEach(botao => botao.addEventListener("click", async () => {
      const acao = botao.dataset.action;
      const parecer = acao === "analisar" ? null : prompt("Informe o parecer da análise:");
      if (acao !== "analisar" && (!parecer || !parecer.trim())) return;
      botao.disabled = true;
      try {
        const options = {method:"PATCH"};
        if (parecer !== null) options.body = JSON.stringify({parecer:parecer.trim()});
        await api("/api/solicitacoes/" + botao.dataset.id + "/" + acao, options);
        avisar("Solicitação atualizada.");
        await modalEquipe();
        await carregarCatalogo();
      } catch (erro) { avisar(erro.message); botao.disabled = false; }
    }));
  } catch (erro) { $("staff-requests").textContent = erro.message; }
}

// Navegação e interações.
$("pet-grid").addEventListener("click", event => {
  const botao = event.target.closest("[data-adopt]");
  if (!botao) return;
  const animal = animaisVisiveis.find(a => a.id === Number(botao.dataset.adopt));
  if (animal) modalAdocao(animal);
});
$("filter-form").addEventListener("change", () => {paginaAtual = 0; carregarCatalogo();});
let filtroTimer;
$("filter-city").addEventListener("input", () => {
  clearTimeout(filtroTimer);
  filtroTimer = setTimeout(() => {paginaAtual = 0; carregarCatalogo();}, 300);
});
$("btn-clear").addEventListener("click", () => {
  $("filter-form").reset();
  paginaAtual = 0;
  carregarCatalogo();
});
$("prev-page").addEventListener("click", () => {if (paginaAtual > 0) {paginaAtual--;carregarCatalogo();}});
$("next-page").addEventListener("click", () => {if (paginaAtual < totalPaginas - 1) {paginaAtual++;carregarCatalogo();}});
$("btn-account").addEventListener("click", () => modalAutenticacao("login"));
$("btn-requests").addEventListener("click", modalMeusPedidos);
$("btn-staff").addEventListener("click", () => { window.location.href = "/painel.html"; });
$("btn-logout").addEventListener("click", () => {
  sessao = null;
  sessionStorage.removeItem("adotapet-sessao");
  atualizarSessao(); carregarCatalogo(); avisar("Você saiu da sua conta.");
});
$("dialog-close").addEventListener("click", fecharModal);
$("app-dialog").addEventListener("click", event => { if (event.target === $("app-dialog")) fecharModal(); });
$("menu-toggle").addEventListener("click", () => {
  const aberto = $("main-menu").classList.toggle("open");
  $("menu-toggle").setAttribute("aria-expanded", String(aberto));
  $("menu-toggle").textContent = aberto ? "×" : "☰";
});
$("main-menu").addEventListener("click", event => {
  if (event.target.closest("a")) {
    $("main-menu").classList.remove("open");
    $("menu-toggle").setAttribute("aria-expanded","false");
    $("menu-toggle").textContent = "☰";
  }
});
$("year").textContent = new Date().getFullYear();
atualizarSessao();
carregarAbrigos();
carregarCatalogo();
if (sessao) api("/api/auth/me").then(usuario => {
  sessao.usuario = usuario;
  sessionStorage.setItem("adotapet-sessao", JSON.stringify(sessao));
  atualizarSessao();
}).catch(() => {});
