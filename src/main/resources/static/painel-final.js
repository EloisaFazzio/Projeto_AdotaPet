"use strict";
/* Sem anexo de vacina; foto exclusivamente em Cadastrar/Editar animal. */
(function(){
 if(typeof views==='undefined'||typeof openForm!=='function')return;
 // Restaura as abas gerais sem os blocos promocionais exclusivos para gatos.
 if(typeof vacinas==='function')views.vacinas=vacinas;
 if(typeof aplicacoes==='function')views.aplicacoes=aplicacoes;
 if(typeof adocoes==='function')views.adocoes=adocoes;
 formVacina=async function(id){
   const v=id?await api(f('vacinas/'+id)):null;
   openForm(id?'Editar vacina':'Cadastrar vacina',
     field('nome','Nome da vacina','text',{required:true,maxlength:100,value:v?.nome})+
     select('especieAlvo','Espécie-alvo',[['CAO','Cachorro'],['GATO','Gato'],['OUTRA','Outro']],v?.especieAlvo)+
     field('intervaloDias','Intervalo entre doses (dias, opcional)','number',{min:1,value:v?.intervaloDias}),
     d=>api(f('vacinas'+(id?'/'+id:'')),{method:id?'PUT':'POST',body:JSON.stringify({
       nome:d.nome.trim(),especieAlvo:d.especieAlvo,intervaloDias:d.intervaloDias?Number(d.intervaloDias):null
     })})
   );
 };
 formAnimal=async function(id){
   const [abrigos,a]=await Promise.all([api(f('abrigos')),id?api(f('animais/'+id)):Promise.resolve(null)]);
   if(!abrigos.length){toast('Cadastre um abrigo antes de cadastrar um animal.');return;}
   const foto='<div class="animal-foto-form"><label for="animal-foto-final">Anexar foto do animal (opcional)</label>'+
     '<input id="animal-foto-final" type="file" name="foto" accept="image/jpeg,image/png,image/webp">'+
     '<small>JPG, PNG ou WebP; máximo de 2 MB. A nova foto substitui a anterior.</small></div>';
   openForm(id?'Editar animal':'Cadastrar animal',
    '<div class="panel-form-row">'+field('nome','Nome','text',{required:true,maxlength:80,value:a?.nome})+
    select('abrigoId','Abrigo',abrigos.map(b=>[b.id,b.nome+' — '+b.cidade]),a?.abrigoId)+'</div>'+
    '<div class="panel-form-row">'+select('especie','Espécie',[['CAO','Cachorro'],['GATO','Gato'],['OUTRA','Outro']],a?.especie)+
    select('sexo','Sexo',[['FEMEA','Fêmea'],['MACHO','Macho']],a?.sexo)+'</div>'+
    '<div class="panel-form-row">'+select('porte','Porte',[['PEQUENO','Pequeno'],['MEDIO','Médio'],['GRANDE','Grande']],a?.porte)+
    field('dataNascimento','Data de nascimento','date',{value:a?.dataNascimento})+'</div>'+
    field('raca','Raça ou tipo de animal (opcional)','text',{maxlength:80,value:a?.raca})+
    '<label><input type="checkbox" name="castrado" value="true"'+(a?.castrado?' checked':'')+'> Castrado</label>'+
    '<div class="panel-field"><label for="f-descricao">Descrição</label><textarea name="descricao" id="f-descricao" maxlength="1000">'+esc(a?.descricao||'')+'</textarea></div>'+foto,
    async d=>{
      const imagem=d.foto;
      if(imagem?.size){
        if(imagem.size>2*1024*1024)throw Error('Escolha uma foto de até 2 MB.');
        if(!['image/jpeg','image/png','image/webp'].includes(imagem.type))throw Error('Use JPG, PNG ou WebP.');
      }
      const salvo=await api(f('animais'+(id?'/'+id:'')),{method:id?'PUT':'POST',body:JSON.stringify({
       nome:d.nome,abrigoId:Number(d.abrigoId),especie:d.especie,sexo:d.sexo,
       porte:d.porte,dataNascimento:d.dataNascimento||null,raca:d.raca||null,
       castrado:d.castrado==='true',descricao:d.descricao||null
      })});
      if(imagem?.size){
        const fd=new FormData();fd.append('arquivo',imagem,imagem.name);
        const resposta=await fetch(f('animais/'+salvo.id+'/imagem'),{method:'PUT',headers:{Authorization:'Bearer '+sessao.token},body:fd});
        if(!resposta.ok)throw Error('O animal foi salvo, mas não foi possível anexar a foto. Tente editar novamente.');
      }
      return salvo;
    });
 };
 const original=views.animais;
 views.animais=async function(){
  await original();
  document.querySelectorAll('#panel-content .panel-table tbody tr').forEach(tr=>{
   const acao=tr.querySelector('[data-action="editar-animal"]'),coluna=tr.querySelector('td:first-child');
   if(!acao||!coluna||coluna.querySelector('.animal-foto-mini'))return;
   const img=document.createElement('img');img.className='animal-foto-mini';img.loading='lazy';img.alt='Foto do animal';
   img.onerror=()=>{const nome=coluna.querySelector('b')?.textContent||'';const url=window.AdotaPetRetratos?.arquivo(nome);if(url&&img.src!==location.origin+url){img.onerror=()=>img.remove();img.src=url;}else img.remove();};
   img.src=f('animais/'+encodeURIComponent(acao.dataset.id)+'/imagem');coluna.prepend(img);
  });
 };
})();
