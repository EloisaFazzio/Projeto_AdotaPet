"use strict";
/* Painel AdotaPet. Todos os dados vêm da API; nunca armazene senhas. */
const $ = id => document.getElementById(id);
const esc = value => String(value == null ? "" : value).replace(/[&<>"']/g,
  c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
const nomes = {
  CAO:"Cachorro",GATO:"Gato",OUTRA:"Outro",FEMEA:"Fêmea",MACHO:"Macho",
  PEQUENO:"Pequeno",MEDIO:"Médio",GRANDE:"Grande",
  DISPONIVEL:"Disponível",EM_TRATAMENTO:"Em tratamento",
  EM_PROCESSO_ADOCAO:"Em processo de adoção",ADOTADO:"Adotado",
  PENDENTE:"Pendente",EM_ANALISE:"Em análise",APROVADA:"Aprovada",
  REJEITADA:"Rejeitada",CANCELADA:"Cancelada"
};
const label = x => nomes[x] || String(x ?? "—");
const admin = () => !!sessao && sessao.usuario.perfil === "ADMIN";
const equipe = () => !!sessao && ["ADMIN","VOLUNTARIO"].includes(sessao.usuario.perfil);
const btn = (txt,action,id,extra="") =>
  '<button type="button" class="action-button ' + extra + '" data-action="' + esc(action) +
  '" data-id="' + Number(id) + '">' + esc(txt) + '</button>';
const badge = status => '<span class="pill ' +
  (["REJEITADA","CANCELADA"].includes(status) ? "pill-alert" :
    ["PENDENTE","EM_ANALISE"].includes(status) ? "pill-pending" : "pill-neutral") +
  '">' + esc(label(status)) + '</span>';
const table = (heads,rows) => '<div class="panel-card"><div class="panel-table-wrap"><table class="panel-table"><thead><tr>' +
  heads.map(h=>'<th scope="col">' + esc(h) + '</th>').join("") +
  '</tr></thead><tbody>' + (rows.length ? rows.join("") :
  '<tr><td colspan="' + heads.length + '">Nenhum registro encontrado.</td></tr>') +
  '</tbody></table></div></div>';
const row = cells => '<tr>' + cells.map(c=>'<td>' + c + '</td>').join("") + '</tr>';
const field = (name,title,type="text",opts={}) =>
  '<div class="panel-field"><label for="f-'+esc(name)+'">'+esc(title)+'</label><input id="f-'+esc(name)+
  '" name="'+esc(name)+'" type="'+esc(type)+'" value="'+esc(opts.value || "")+'"'+
  (opts.required?' required':'')+(opts.min?' min="'+esc(opts.min)+'"':'')+
  (opts.maxlength?' maxlength="'+Number(opts.maxlength)+'"':'')+
  (opts.minlength?' minlength="'+Number(opts.minlength)+'"':'')+
  (opts.placeholder?' placeholder="'+esc(opts.placeholder)+'"':'')+'></div>';
const select = (name,title,values,value="",required=true) =>
  '<div class="panel-field"><label for="f-'+esc(name)+'">'+esc(title)+'</label><select id="f-'+esc(name)+
  '" name="'+esc(name)+'"'+(required?' required':'')+'>'+
  values.map(([v,t])=>'<option value="'+esc(v)+'"'+(String(v)===String(value)?' selected':'')+'>'+esc(t)+'</option>').join("")+
  '</select></div>';
const textArea = (name,title,value="") =>
  '<div class="panel-field"><label for="f-'+esc(name)+'">'+esc(title)+'</label><textarea id="f-'+esc(name)+
  '" name="'+esc(name)+'" maxlength="1000" required>'+esc(value)+'</textarea></div>';
const sec = (title,inner) => '<section class="panel-card"><h2>'+esc(title)+'</h2>'+inner+'</section>';
const stat = (number,text,icon="✳") =>
  '<div class="metric"><div class="metric-icon" aria-hidden="true">'+icon+'</div><strong>'+esc(number)+'</strong><span>'+esc(text)+'</span></div>';
const f = path => "/api/" + path;
const hojeISO = () => {
  const h = new Date();
  return h.getFullYear()+"-"+String(h.getMonth()+1).padStart(2,"0")+"-"+String(h.getDate()).padStart(2,"0");
};
let sessao=null;
let visao="resumo";
let avisoTimer=null;
try { sessao=JSON.parse(sessionStorage.getItem("adotapet-sessao")||"null"); }
catch { sessionStorage.removeItem("adotapet-sessao"); }

async function api(url,options={}) {
  const headers = new Headers(options.headers||{});
  if(options.body) headers.set("Content-Type","application/json");
  if(sessao?.token) headers.set("Authorization","Bearer "+sessao.token);
  let response;
  try { response=await fetch(url,{...options,headers}); }
  catch { throw Error("Não foi possível conectar à API. Inicie o servidor Spring Boot."); }
  const data = response.status===204?{}:await response.json().catch(()=>({}));
  if(!response.ok) {
    if(response.status===401) {
      sessionStorage.removeItem("adotapet-sessao");
      sessao=null;
      $("panel-alert").hidden=false;
      $("panel-alert").textContent="Sua sessão expirou. Entre novamente pelo site.";
    }
    throw Error(data.erro||(response.status===403?"Você não tem permissão para esta operação.":"Erro HTTP "+response.status));
  }
  return data;
}
function toast(texto) {
  const el=$("panel-toast");
  el.textContent=texto;
  el.classList.add("visible");
  clearTimeout(avisoTimer);
  avisoTimer=setTimeout(()=>el.classList.remove("visible"),5000);
}
function error(err) {
  $("panel-alert").hidden=false;
  $("panel-alert").textContent=err.message || String(err);
}
function clearError() { $("panel-alert").hidden=true; $("panel-alert").textContent=""; }
const titles={
  resumo:["Visão geral","Acompanhe a rotina da ONG com informações do banco de dados."],
  animais:["Animais","Consulte os animais por situação e registre novas entradas."],
  solicitacoes:["Solicitações","Analise pedidos e formalize adoções com responsabilidade."],
  vacinas:["Vacinas","Gerencie o catálogo de vacinas conforme a espécie."],
  aplicacoes:["Vacinações","Registre aplicações, consulte o histórico e acompanhe doses vencidas."],
  abrigos:["Abrigos","Cadastre abrigos, atualize informações e organize voluntários."],
  usuarios:["Usuários","Administre perfis e desative contas mantendo o histórico."],
  relatorios:["Relatórios","Consulte os resultados reais de adoção e saúde animal."]
};
const views={resumo,animais,solicitacoes,vacinas,aplicacoes,abrigos,usuarios,relatorios};
async function render() {
  if(!equipe()) {
    $("panel-content").innerHTML=sec("Acesso restrito",
      '<p>Entre no site como administrador ou voluntário para usar o painel.</p><a class="btn btn-deep" href="/">Voltar ao site</a>');
    $("view-actions").innerHTML="";
    return;
  }
  if(!admin() && ["usuarios","relatorios"].includes(visao)) visao="resumo";
  $("view-title").textContent=titles[visao][0];
  $("view-description").textContent=titles[visao][1];
  $("panel-nav").querySelectorAll("[data-view]").forEach(b=>{
    b.classList.toggle("active",b.dataset.view===visao);
    b.setAttribute("aria-current",b.dataset.view===visao?"page":"false");
  });
  $("view-actions").innerHTML="";
  $("panel-content").innerHTML='<p class="panel-placeholder">Carregando informações...</p>';
  clearError();
  try { await views[visao](); }
  catch(err) { error(err); $("panel-content").innerHTML='<div class="panel-placeholder">Falha ao carregar esta seção. Tente novamente.</div>'; }
}
function action(titulo,callback) {
  $("view-actions").innerHTML='<button class="btn btn-deep btn-small" type="button" id="view-main-action">'+esc(titulo)+'</button>';
  $("view-main-action").addEventListener("click",callback);
}
function openForm(title,markup,submit) {
  $("form-container").innerHTML='<span class="eyebrow">GESTÃO ADOTAPET</span><h2 id="form-title">'+esc(title)+'</h2>'+
    '<form id="operation-form" class="panel-form">'+markup+
    '<p class="panel-alert" id="form-error" role="alert" hidden></p>'+
    '<button class="btn btn-deep" type="submit">Salvar alterações ↗</button></form>';
  $("form-dialog").showModal();
  $("operation-form").addEventListener("submit",async e=>{
    e.preventDefault();
    const form=e.currentTarget;
    const botao=form.querySelector('button[type="submit"]');
    const dados=Object.fromEntries(new FormData(form).entries());
    botao.disabled=true;
    try {
      await submit(dados,form);
      $("form-dialog").close();
      toast("Operação concluída com sucesso.");
      await render();
    } catch(err) {
      const el=$("form-error");el.textContent=err.message;el.hidden=false;
    } finally {botao.disabled=false;}
  });
}
function closeDialog() { if($("form-dialog").open) $("form-dialog").close(); }
async function resumo() {
  const [abrigos,pedidos,pendentes,vencidas] = await Promise.all([
    api(f("abrigos")),api(f("solicitacoes")),
    api(f("animais?size=1&status=DISPONIVEL")),api(f("vacinacoes/vencidas"))
  ]);
  const aguardando=pedidos.filter(p=>["PENDENTE","EM_ANALISE"].includes(p.status));
  $("panel-content").innerHTML=
    '<div class="metric-grid">'+stat(pendentes.totalElements||0,"Animais disponíveis","🐾")+
    stat(abrigos.length,"Abrigos cadastrados","⌂")+
    stat(aguardando.length,"Solicitações em andamento","♡")+
    stat(vencidas.length,"Doses com data vencida","✚")+'</div>'+
    sec("Prioridades do dia",'<p class="panel-muted">Informações atualizadas diretamente pelo sistema.</p>'+
      (aguardando.length?'<p><strong>'+aguardando.length+'</strong> pedido(s) aguardam análise ou conclusão.</p>':
      '<p>Não há solicitações ativas no momento.</p>')+
      (vencidas.length?'<p class="panel-banner warning">Há '+vencidas.length+' dose(s) vencidas para acompanhamento.</p>':
      '<p class="panel-banner">Nenhuma dose vencida registrada.</p>'))+
    sec("Acesso rápido",'<div class="table-actions">'+
      '<button class="btn btn-outline" data-go="solicitacoes">Ver solicitações</button>'+
      '<button class="btn btn-outline" data-go="aplicacoes">Vacinações vencidas</button>'+
      '<button class="btn btn-outline" data-go="animais">Consultar animais</button></div>');
}
async function todosAnimais() {
  const statuses=["DISPONIVEL","EM_TRATAMENTO","EM_PROCESSO_ADOCAO","ADOTADO"];
  const respostas=await Promise.all(statuses.map(status=>
    api(f("animais?status="+status+"&size=36&page=0"))));
  const animais=[];
  const excedidos=[];
  respostas.forEach((r,i)=>{
    animais.push(...(r.content||[]));
    if(r.totalPages>1) excedidos.push(statuses[i]);
  });
  return {animais,excedidos};
}
async function animais() {
  const dados=await todosAnimais();
  if(dados.excedidos.length) toast("Exibidos até 36 animais por status. A paginação administrativa ainda será ampliada.");
  if(admin()||sessao.usuario.perfil==="VOLUNTARIO") action("+ Cadastrar animal",()=>formAnimal());
  $("panel-content").innerHTML=sec("Animais registrados",
    '<p class="panel-muted">É possível visualizar os principais status. O cadastro completo de animais também fica disponível na página inicial da ONG.</p>')+
    table(["Nome","Espécie/porte","Abrigo/cidade","Status","Ação"],
      dados.animais.map(a=>row([
        "<b>"+esc(a.nome)+"</b>",
        esc(label(a.especie))+" · "+esc(label(a.porte)),
        esc(a.abrigo)+"<span class='sub'>"+esc(a.cidade)+"</span>",
        badge(a.status),
        btn("Vacinar","vacinar",a.id)
      ]))));
}
async function formAnimal() {
  const abrigos=await api(f("abrigos"));
  if(!abrigos.length){toast("Cadastre um abrigo antes de cadastrar um animal.");return;}
  openForm("Cadastrar animal",
    '<div class="panel-form-row">'+field("nome","Nome", "text",{required:true,maxlength:80})+
    select("abrigoId","Abrigo",abrigos.map(a=>[a.id,a.nome+" — "+a.cidade]))+'</div>'+
    '<div class="panel-form-row">'+select("especie","Espécie",[["CAO","Cachorro"],["GATO","Gato"],["OUTRA","Outro"]])+
    select("sexo","Sexo",[["FEMEA","Fêmea"],["MACHO","Macho"]])+'</div>'+
    '<div class="panel-form-row">'+select("porte","Porte",[["PEQUENO","Pequeno"],["MEDIO","Médio"],["GRANDE","Grande"]])+
    field("dataNascimento","Data de nascimento","date")+'</div>'+
    field("raca","Raça (opcional)")+
    '<label><input type="checkbox" name="castrado" value="true"> Castrado</label>'+
    '<div class="panel-field"><label for="f-descricao">Descrição (opcional)</label><textarea name="descricao" id="f-descricao" maxlength="1000"></textarea></div>',
    d=>api(f("animais"),{method:"POST",body:JSON.stringify({
      nome:d.nome,abrigoId:Number(d.abrigoId),especie:d.especie,sexo:d.sexo,
      porte:d.porte,dataNascimento:d.dataNascimento||null,raca:d.raca||null,
      castrado:d.castrado==="true",descricao:d.descricao||null
    })}));
}
async function solicitacoes() {
  const pedidos=await api(f("solicitacoes"));
  $("panel-content").innerHTML=sec("Solicitações de adoção",
    '<p class="panel-muted">A aprovação exige parecer; solicitações concorrentes ativas são rejeitadas automaticamente pelo backend.</p>')+
    table(["Animal / adotante","Status","Justificativa / parecer","Ações"],
      pedidos.map(p=>row([
        "<b>"+esc(p.animal)+"</b><span class='sub'>"+esc(p.adotante)+"</span>",
        badge(p.status),
        '<div>'+esc(p.justificativa)+'</div>'+
        (p.parecer?"<span class='sub'>Parecer: "+esc(p.parecer)+"</span>":""),
        '<div class="table-actions">'+
        (p.status==="PENDENTE"?btn("Iniciar análise","analisar",p.id):"")+
        (p.status==="EM_ANALISE"?btn("Aprovar","aprovar",p.id):"")+
        (["PENDENTE","EM_ANALISE"].includes(p.status)?btn("Rejeitar","rejeitar",p.id,"action-danger"):"")+
        (p.status==="APROVADA"?btn("Formalizar","formalizar",p.id):"")+
        '</div>'
      ]))))+
    sec("Acompanhamento pós-adoção",
      '<p class="panel-muted">A formalização da adoção retorna o número da adoção. Informe esse número para registrar uma visita de acompanhamento.</p>'+
      '<div class="table-actions"><button type="button" class="btn btn-outline" data-open="visita">Registrar visita</button>'+
      '<button type="button" class="btn btn-outline" data-open="historico-visitas">Consultar visitas</button></div>');
}
async function vacinas() {
  const dados=await api(f("vacinas"));
  if(admin()) action("+ Cadastrar vacina",()=>formVacina());
  $("panel-content").innerHTML=sec("Catálogo de vacinas",
    '<p class="panel-muted">O intervalo entre doses é opcional para vacinas de dose única.</p>')+
    table(["Nome","Espécie-alvo","Intervalo de doses","Ações"],dados.map(v=>row([
      "<b>"+esc(v.nome)+"</b>",esc(label(v.especieAlvo)),
      v.intervaloDias!=null?esc(v.intervaloDias)+" dias":"Dose única",
      admin()?'<div class="table-actions">'+btn("Editar","editar-vacina",v.id)+
        btn("Excluir","excluir-vacina",v.id,"action-danger")+'</div>':"—"
    ]))));
}
async function formVacina(id) {
  const v=id?await api(f("vacinas/"+id)):null;
  openForm(id?"Editar vacina":"Cadastrar vacina",
    field("nome","Nome da vacina","text",{required:true,maxlength:100,value:v?.nome})+
    select("especieAlvo","Espécie-alvo",[["CAO","Cachorro"],["GATO","Gato"],["OUTRA","Outro"]],v?.especieAlvo)+
    field("intervaloDias","Intervalo entre doses (dias, opcional)","number",{min:1,value:v?.intervaloDias}),
    d=>api(f("vacinas"+(id?"/"+id:"")),{
      method:id?"PUT":"POST",
      body:JSON.stringify({
        nome:d.nome.trim(),especieAlvo:d.especieAlvo,
        intervaloDias:d.intervaloDias?Number(d.intervaloDias):null
      })
    }));
}
async function aplicacoes() {
  const vencidas=await api(f("vacinacoes/vencidas"));
  action("+ Registrar vacinação",()=>formAplicacao());
  $("panel-content").innerHTML=sec("Doses vencidas",
    '<p class="panel-muted">Considera registros com próxima dose anterior à data atual, conforme a API.</p>')+
    table(["Animal","Vacina","Aplicação","Próxima dose"],
      vencidas.map(v=>row([
        "<b>"+esc(v.animal)+"</b><span class='sub'>ID "+Number(v.animalId)+"</span>",
        esc(v.vacina),esc(v.dataAplicacao),badge("VENCIDA")+" "+esc(v.proximaDose)
      ]))))+
    sec("Consultar histórico de vacinação",'<form class="inline-filters" id="history-form">'+
      field("animalId","ID do animal","number",{required:true,min:1})+
      '<button type="submit" class="btn btn-outline">Consultar</button></form>'+
      '<div id="history-result" aria-live="polite"></div>');
  $("history-form").addEventListener("submit",async e=>{
    e.preventDefault();
    const id=Number(new FormData(e.target).get("animalId"));
    try {
      const registros=await api(f("animais/"+id+"/vacinacoes"));
      $("history-result").innerHTML=table(["Vacina","Aplicação","Próxima dose","Veterinário"],
        registros.map(v=>row([esc(v.vacina),esc(v.dataAplicacao),esc(v.proximaDose||"—"),esc(v.veterinario||"—")])));
    } catch(err){error(err);}
  });
}
async function formAplicacao(animalId) {
  const vacinas=await api(f("vacinas"));
  if(!vacinas.length) {toast("O administrador precisa cadastrar uma vacina primeiro.");return;}
  openForm("Registrar aplicação",
    field("animalId","ID do animal","number",{required:true,min:1,value:animalId||""})+
    select("vacinaId","Vacina",vacinas.map(v=>[v.id,v.nome+" ("+label(v.especieAlvo)+")"]))+
    '<div class="panel-form-row">'+field("dataAplicacao","Data da aplicação","date",{required:true,value:hojeISO()})+
    field("proximaDose","Próxima dose (opcional)","date")+'</div>'+
    field("veterinario","Veterinário responsável (opcional)","text",{maxlength:120}),
    d=>api(f("animais/"+Number(d.animalId)+"/vacinacoes"),{
      method:"POST",body:JSON.stringify({
        vacinaId:Number(d.vacinaId),dataAplicacao:d.dataAplicacao,
        proximaDose:d.proximaDose||null,veterinario:d.veterinario||null
      })
    }));
}
async function abrigos() {
  const dados=await api(f("abrigos"));
  if(admin()) action("+ Cadastrar abrigo",()=>formAbrigo());
  $("panel-content").innerHTML=sec("Abrigos cadastrados",
    '<p class="panel-muted">A consulta pública mostra apenas informações resumidas. Dados de endereço e voluntários são restritos ao administrador.</p>')+
    table(["Abrigo","Cidade","UF","Capacidade","Ações"],dados.map(a=>row([
      "<b>"+esc(a.nome)+"</b>",esc(a.cidade),esc(a.uf),esc(a.capacidade),
      admin()?'<div class="table-actions">'+btn("Editar","editar-abrigo",a.id)+
        btn("Voluntários","voluntarios",a.id)+btn("Excluir","excluir-abrigo",a.id,"action-danger")+'</div>':"—"
    ]))));
}
async function formAbrigo(id) {
  const a=id?await api(f("abrigos/"+id)):null;
  openForm(id?"Editar abrigo":"Cadastrar abrigo",
    field("nome","Nome do abrigo","text",{required:true,maxlength:120,value:a?.nome})+
    field("endereco","Endereço","text",{required:true,maxlength:200,value:a?.endereco})+
    '<div class="panel-form-row">'+field("cidade","Cidade","text",{required:true,maxlength:80,value:a?.cidade})+
    field("uf","UF","text",{required:true,maxlength:2,minlength:2,value:a?.uf,placeholder:"SC"})+'</div>'+
    '<div class="panel-form-row">'+field("telefone","Telefone (opcional)","text",{maxlength:20,value:a?.telefone})+
    field("capacidade","Capacidade","number",{required:true,min:1,value:a?.capacidade})+'</div>',
    d=>api(f("abrigos"+(id?"/"+id:"")),{
      method:id?"PUT":"POST",body:JSON.stringify({
        nome:d.nome,endereco:d.endereco,cidade:d.cidade,uf:d.uf,
        telefone:d.telefone||null,capacidade:Number(d.capacidade)
      })
    }));
}
async function gerenciarVoluntarios(abrigoId) {
  const [vinculados,usuarios]=await Promise.all([
    api(f("abrigos/"+abrigoId+"/voluntarios")),api(f("usuarios?perfil=VOLUNTARIO"))
  ]);
  const disponiveis=usuarios.filter(u=>u.ativo&&!vinculados.some(v=>v.id===u.id));
  openForm("Voluntários do abrigo",
    '<div class="panel-banner">Vinculados: '+(vinculados.length?vinculados.map(v=>esc(v.nome)).join(", "):"nenhum")+'</div>'+
    (disponiveis.length?select("usuarioId","Vincular voluntário ativo",
      disponiveis.map(u=>[u.id,u.nome+" — "+u.email])):
      '<p>Não há voluntários ativos disponíveis para novo vínculo.</p>')+
    '<div class="panel-banner warning">Para desvincular um voluntário, use o botão abaixo.</div>'+
    '<div class="table-actions">'+vinculados.map(v=>btn("Desvincular "+v.nome,"desvincular",v.id,"action-danger")).join("")+'</div>',
    d=>{if(!d.usuarioId)throw Error("Selecione um voluntário disponível.");
      return api(f("abrigos/"+abrigoId+"/voluntarios"),{
        method:"POST",body:JSON.stringify({usuarioId:Number(d.usuarioId)})
      });});
  // Botões internos são processados aqui porque o modal também recebe eventos.
  $("form-container").querySelectorAll("[data-action=desvincular]").forEach(b=>b.addEventListener("click",async()=>{
    if(!confirm("Desvincular este voluntário do abrigo?"))return;
    try {
      await api(f("abrigos/"+abrigoId+"/voluntarios/"+b.dataset.id),{method:"DELETE"});
      closeDialog();toast("Voluntário desvinculado.");await render();
    }catch(err){error(err);}
  }));
}
async function usuarios() {
  const dados=await api(f("usuarios"));
  action("+ Cadastrar usuário",()=>formUsuario());
  $("panel-content").innerHTML=sec("Contas da plataforma",
    '<p class="panel-muted">Somente administradores acessam dados pessoais. Excluir uma conta significa desativá-la sem apagar o histórico.</p>')+
    table(["Nome","E-mail","Perfil","Situação","Ações"],dados.map(u=>row([
      "<b>"+esc(u.nome)+"</b><span class='sub'>"+esc(u.cidade||"")+"</span>",
      esc(u.email),esc(u.perfil),
      badge(u.ativo?"ATIVO":"INATIVO"),
      '<div class="table-actions">'+btn("Editar","editar-usuario",u.id)+
      (u.ativo?btn("Desativar","desativar-usuario",u.id,"action-danger"):
      btn("Reativar","reativar-usuario",u.id))+'</div>'
    ]))));
}
async function formUsuario(id) {
  const u=id?await api(f("usuarios/"+id)):null;
  const p=[["ADOTANTE","Adotante"],["VOLUNTARIO","Voluntário"],["ADMIN","Administrador"]];
  openForm(id?"Editar usuário":"Cadastrar usuário",
    field("nome","Nome completo","text",{required:true,maxlength:120,value:u?.nome})+
    field("email","E-mail","email",{required:true,maxlength:160,value:u?.email})+
    '<div class="panel-form-row">'+select("perfil","Perfil",p,u?.perfil||"ADOTANTE")+
    field("senha","Senha "+(id?"(opcional — alterar)":"(mín. 8 caracteres)"),"password",{required:!id,minlength:8,maxlength:72})+'</div>'+
    '<div class="panel-form-row">'+field("telefone","Telefone (opcional)","text",{maxlength:20,value:u?.telefone})+
    field("cidade","Cidade (opcional)","text",{maxlength:80,value:u?.cidade})+'</div>',
    d=>api(f("usuarios"+(id?"/"+id:"")),{
      method:id?"PUT":"POST",
      body:JSON.stringify(id?{
        nome:d.nome,email:d.email,novaSenha:d.senha||null,
        telefone:d.telefone||null,cidade:d.cidade||null,perfil:d.perfil
      }:{
        nome:d.nome,email:d.email,senha:d.senha,
        telefone:d.telefone||null,cidade:d.cidade||null,perfil:d.perfil
      })
    }));
}
async function relatorios() {
  const [statuses,vencidas] = await Promise.all([
    api(f("relatorios/animais-por-status")),api(f("vacinacoes/vencidas"))
  ]);
  const total=Object.values(statuses).reduce((x,y)=>x+Number(y),0);
  const barras=Object.entries(statuses).map(([status,count])=>{
    const pct=total?Math.round(100*count/total):0;
    return '<div class="fact"><strong>'+esc(count)+'</strong><span>'+esc(label(status))+'</span>'+
      '<div style="height:9px;border-radius:9px;background:#d1e1ca;margin-top:12px">'+
      '<div style="width:'+pct+'%;height:100%;background:var(--green);border-radius:9px"></div></div></div>';
  }).join("");
  const hoje=hojeISO();
  const primeiro=hoje.substring(0,7)+"-01";
  $("panel-content").innerHTML=
    '<div class="metric-grid">'+stat(total,"Animais no sistema","🐾")+
    stat(statuses.ADOTADO||0,"Animais adotados","♡")+
    stat(vencidas.length,"Doses vencidas","✚")+'</div>'+
    sec("Distribuição dos animais por status",'<div class="panel-facts">'+barras+'</div>')+
    sec("Adoções por período",'<form id="period-form" class="inline-filters">'+
      field("de","Data inicial","date",{required:true,value:primeiro})+
      field("ate","Data final","date",{required:true,value:hoje})+
      '<button class="btn btn-outline" type="submit">Consultar</button></form>'+
      '<div id="period-result" aria-live="polite"><p class="panel-muted">Selecione o período e consulte as adoções formalizadas.</p></div>')+
    sec("Vacinações vencidas",'<p>'+vencidas.length+' registro(s) possuem a próxima dose anterior à data atual.</p>');
  $("period-form").addEventListener("submit",async e=>{
    e.preventDefault();
    const dados=new FormData(e.target);
    try{
      const resposta=await api(f("relatorios/adocoes-por-periodo?de="+
        encodeURIComponent(dados.get("de"))+"&ate="+encodeURIComponent(dados.get("ate"))));
      $("period-result").innerHTML='<div class="metric metric-green"><strong>'+esc(resposta.total)+
        '</strong><span>Adoção(ões) formalizada(s) de '+esc(resposta.de)+' a '+esc(resposta.ate)+'</span></div>';
    } catch(err){error(err);}
  });
}
async function formParecer(id,operacao) {
  openForm(operacao==="aprovar"?"Aprovar solicitação":"Rejeitar solicitação",
    '<p>Registre um parecer antes de concluir a decisão.</p>'+
    textArea("parecer","Parecer da equipe"),
    d=>api(f("solicitacoes/"+id+"/"+operacao),{
      method:"PATCH",body:JSON.stringify({parecer:d.parecer.trim()})
    }));
}
function formFormalizar(id) {
  openForm("Formalizar adoção",
    '<p>Adoções só podem ser formalizadas depois da aprovação da solicitação e assinatura do termo.</p>'+
    '<label><input type="checkbox" name="termoAssinado" required value="true"> Confirmo que o termo de responsabilidade foi assinado.</label>'+
    field("observacoes","Observações (opcional)","text",{maxlength:500}),
    async d=>{
      const resultado=await api(f("adocoes"),{
        method:"POST",body:JSON.stringify({solicitacaoId:id,
          termoAssinado:d.termoAssinado==="true",observacoes:d.observacoes||null})
      });
      toast("Adoção #"+resultado.id+" formalizada. Use esse ID para registrar visitas.");
    });
}
function formVisita() {
  openForm("Registrar visita pós-adoção",
    field("adocaoId","ID da adoção","number",{required:true,min:1})+
    field("dataVisita","Data da visita","date",{required:true,value:hojeISO()})+
    textArea("observacoes","Observações da visita")+
    '<label><input type="checkbox" name="animalBemCuidado" checked value="true"> O animal está bem cuidado.</label>',
    d=>api(f("adocoes/"+Number(d.adocaoId)+"/visitas"),{
      method:"POST",body:JSON.stringify({dataVisita:d.dataVisita,
        observacoes:d.observacoes.trim(),animalBemCuidado:d.animalBemCuidado==="true"})
    }));
}
function formHistoricoVisitas() {
  openForm("Consultar visitas",
    field("adocaoId","ID da adoção","number",{required:true,min:1}),
    async d=>{
      const dados=await api(f("adocoes/"+Number(d.adocaoId)+"/visitas"));
      closeDialog();
      $("panel-content").insertAdjacentHTML("afterbegin",sec("Histórico da adoção #"+Number(d.adocaoId),
        table(["Data","Bem-estar","Observações"],dados.map(v=>row([
          esc(v.dataVisita),badge(v.animalBemCuidado?"BEM CUIDADO":"ATENÇÃO"),esc(v.observacoes)
        ])))));
    });
}
async function deletar(recurso,id,mensagem) {
  if(!confirm(mensagem))return;
  try {await api(f(recurso+"/"+id),{method:"DELETE"});toast("Operação realizada.");await render();}
  catch(err){error(err);}
}

$("panel-nav").addEventListener("click",e=>{
  const b=e.target.closest("[data-view]");
  if(!b) return;
  visao=b.dataset.view;
  render();
});
$("panel-content").addEventListener("click",async e=>{
  const go=e.target.closest("[data-go]");
  if(go){visao=go.dataset.go;await render();return;}
  const open=e.target.closest("[data-open]");
  if(open){if(open.dataset.open==="visita")formVisita();if(open.dataset.open==="historico-visitas")formHistoricoVisitas();return;}
  const b=e.target.closest("[data-action]");
  if(!b) return;
  const id=Number(b.dataset.id);
  try {
    switch(b.dataset.action) {
      case "vacinar": return formAplicacao(id);
      case "analisar":
        if(confirm("Iniciar análise desta solicitação?")) {
          await api(f("solicitacoes/"+id+"/analisar"),{method:"PATCH"});
          toast("Análise iniciada.");await render();
        }
        break;
      case "aprovar": return formParecer(id,"aprovar");
      case "rejeitar": return formParecer(id,"rejeitar");
      case "formalizar": return formFormalizar(id);
      case "editar-vacina": return formVacina(id);
      case "excluir-vacina": return deletar("vacinas",id,"Excluir esta vacina? Registros de doses já aplicadas devem ser preservados.");
      case "editar-abrigo": return formAbrigo(id);
      case "voluntarios": return gerenciarVoluntarios(id);
      case "excluir-abrigo": return deletar("abrigos",id,"Excluir este abrigo? A exclusão é bloqueada quando há animais associados.");
      case "editar-usuario": return formUsuario(id);
      case "desativar-usuario": return deletar("usuarios",id,"Desativar este usuário? O histórico será preservado.");
      case "reativar-usuario":
        await api(f("usuarios/"+id+"/reativar"),{method:"PATCH"});toast("Conta reativada.");return render();
    }
  } catch(err){error(err);}
});
$("close-dialog").addEventListener("click",closeDialog);
$("form-dialog").addEventListener("click",e=>{if(e.target===$("form-dialog"))closeDialog();});
$("logout").addEventListener("click",()=>{
  sessionStorage.removeItem("adotapet-sessao");
  window.location.href="/";
});
$("year").textContent=new Date().getFullYear();
(async function iniciar(){
  if(!sessao?.token){
    $("current-user").textContent="Sem sessão";
    await render();
    return;
  }
  try{
    const usuario=await api("/api/auth/me");
    if(!["ADMIN","VOLUNTARIO"].includes(usuario.perfil)) {
      sessao=null;
      sessionStorage.removeItem("adotapet-sessao");
      $("current-user").textContent="Acesso não autorizado";
      await render();
      return;
    }
    sessao.usuario=usuario;
    sessionStorage.setItem("adotapet-sessao",JSON.stringify(sessao));
    $("current-user").textContent=usuario.nome+" · "+usuario.perfil;
    $("panel-nav").querySelectorAll("[data-admin-only]").forEach(e=>e.hidden=usuario.perfil!=="ADMIN");
    await render();
  }catch(err){error(err);await render();}
})();
