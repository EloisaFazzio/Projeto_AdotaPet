#!/usr/bin/env python3
"""Teste de homologação HTTP sem dependências externas; utiliza dados fictícios temporários.

Requer backend Spring Boot ativo em BASE_URL (padrão http://localhost:8080).
Para validar operações ADMIN, configure ADOTAPET_ADMIN_EMAIL e ADOTAPET_ADMIN_PASSWORD
somente por variáveis de ambiente, nunca em arquivos versionados.
"""
import json
import os
import sys
import urllib.error
import urllib.request
import uuid
from datetime import date, timedelta

BASE = os.environ.get("BASE_URL", "http://localhost:8080").rstrip("/")
ADMIN_EMAIL = os.environ.get("ADOTAPET_ADMIN_EMAIL")
ADMIN_PASSWORD = os.environ.get("ADOTAPET_ADMIN_PASSWORD")


def request(method, path, data=None, token=None, expected=200, response_json=True):
    headers = {"Accept": "application/json" if response_json else "*/*"}
    if data is not None:
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = "Bearer " + token
    payload = json.dumps(data).encode("utf-8") if data is not None else None
    req = urllib.request.Request(BASE + path, data=payload, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=15) as res:
            status = res.status
            body = res.read().decode("utf-8")
    except urllib.error.HTTPError as err:
        status = err.code
        body = err.read().decode("utf-8")
    if status != expected:
        raise AssertionError(f"{method} {path}: esperado HTTP {expected}, recebido {status}; {body[:280]}")
    if not response_json:
        return body
    return json.loads(body) if body else {}


def main():
    index = request("GET", "/", response_json=False)
    assert "AdotaPet" in index and 'id="pet-grid"' in index
    painel = request("GET", "/painel.html", response_json=False)
    assert 'id="panel-content"' in painel
    for path in ("/styles.css", "/app.js", "/painel.css", "/painel.js"):
        assert request("GET", path, response_json=False)
    print("PASS - páginas públicas e scripts servidos pelo Spring Boot")

    schema = request("GET", "/v3/api-docs")
    assert "AdotaPet" in schema["info"]["title"]
    assert "/api/animais" in schema["paths"]
    catalogo = request("GET", "/api/animais?size=2")
    assert isinstance(catalogo["content"], list)
    request("GET", "/api/abrigos")
    print("PASS - catálogo e Swagger/OpenAPI")

    email = "smoke-" + uuid.uuid4().hex[:12] + "@example.invalid"
    senha = uuid.uuid4().hex + "A9!"
    sessao = request("POST", "/api/auth/registrar", {
        "nome": "Adotante Smoke",
        "email": email,
        "senha": senha,
        "perfil": "ADMIN",  # campo extra ignorado; perfil deve permanecer ADOTANTE.
    }, expected=201)
    assert sessao["usuario"]["perfil"] == "ADOTANTE"
    token = request("POST", "/api/auth/login", {"email": email, "senha": senha})["token"]
    assert request("GET", "/api/auth/me", token=token)["email"] == email
    request("GET", "/api/usuarios", token=token, expected=403)
    request("GET", "/api/relatorios/animais-por-status", token=token, expected=403)
    print("PASS - autocadastro ADOTANTE, login e bloqueio de privilégios")

    if not ADMIN_EMAIL or not ADMIN_PASSWORD:
        print("SKIP - operações administrativas: configure variáveis de teste no ambiente")
        return
    admin = request("POST", "/api/auth/login", {
        "email": ADMIN_EMAIL, "senha": ADMIN_PASSWORD
    })["token"]
    abrigo = request("POST", "/api/abrigos", {
        "nome": "Abrigo de Homologação",
        "endereco": "Rua do Teste, 123",
        "cidade": "Jaraguá do Sul",
        "uf": "SC",
        "capacidade": 9,
    }, token=admin, expected=201)
    animal = request("POST", "/api/animais", {
        "nome": "Pet Smoke",
        "especie": "CAO", "porte": "MEDIO", "sexo": "FEMEA",
        "abrigoId": abrigo["id"],
        "descricao": "Animal fictício do teste de homologação local"
    }, token=admin, expected=201)
    encontrado = request("GET", "/api/animais/" + str(animal["id"]))
    assert encontrado["status"] == "DISPONIVEL"
    print("PASS - criação de abrigo e animal + consulta pública")

    vacina = request("POST", "/api/vacinas", {
        "nome": "Vacina Smoke " + uuid.uuid4().hex[:8],
        "especieAlvo": "CAO", "intervaloDias": 365
    }, token=admin, expected=201)
    registro = request("POST", "/api/animais/" + str(animal["id"]) + "/vacinacoes", {
        "vacinaId": vacina["id"], "dataAplicacao": date.today().isoformat(),
        "veterinario": "Profissional de teste"
    }, token=admin, expected=201)
    assert registro["animalId"] == animal["id"]
    print("PASS - vacinação persistida e consulta de histórico")

    usuario = request("POST", "/api/usuarios", {
        "nome": "Voluntário Smoke",
        "email": "voluntario-" + uuid.uuid4().hex[:8] + "@example.invalid",
        "senha": uuid.uuid4().hex + "A9!",
        "perfil": "VOLUNTARIO"
    }, token=admin, expected=201)
    assert usuario["perfil"] == "VOLUNTARIO"
    request("POST", "/api/abrigos/" + str(abrigo["id"]) + "/voluntarios", {
        "usuarioId": usuario["id"]
    }, token=admin)
    print("PASS - gestão de equipe e associação do voluntário")

    pedido = request("POST", "/api/solicitacoes", {
        "animalId": animal["id"],
        "justificativa": "Possuo condições para acolher e acompanhar este animal."
    }, token=token, expected=201)
    assert pedido["status"] == "PENDENTE"
    request("PATCH", "/api/solicitacoes/" + str(pedido["id"]) + "/analisar",
            token=admin)
    aprovado = request("PATCH", "/api/solicitacoes/" + str(pedido["id"]) + "/aprovar",
            {"parecer": "Aprovado na homologação."}, token=admin)
    assert aprovado["status"] == "APROVADA"
    adocao = request("POST", "/api/adocoes", {
        "solicitacaoId": pedido["id"], "termoAssinado": True,
        "observacoes": "Teste de formalização"
    }, token=admin, expected=201)
    assert request("GET", "/api/animais/" + str(animal["id"]), token=admin)["status"] == "ADOTADO"
    visita_url = "/api/adocoes/" + str(adocao["id"]) + "/visitas"
    # O modelo somente permite registrar visitas por VOLUNTARIO, não ADMIN.
    request("POST", visita_url, {
        "dataVisita": date.today().isoformat(),
        "observacoes": "Acompanhamento responsável do animal.",
        "animalBemCuidado": True
    }, token=admin, expected=403)
    print("PASS - pedido, análise, aprovação, adoção e autorização de visitas")

    totais = request("GET", "/api/relatorios/animais-por-status", token=admin)
    assert totais["ADOTADO"] >= 1
    resultado = request("GET", "/api/relatorios/adocoes-por-periodo?de=" +
        (date.today()-timedelta(days=1)).isoformat() + "&ate=" +
        (date.today()+timedelta(days=1)).isoformat(), token=admin)
    assert resultado["total"] >= 1
    print("PASS - relatórios coerentes com o fluxo de adoção")
    print("SMOKE FULL STACK: OK")


if __name__ == "__main__":
    try:
        main()
    except Exception as error:
        print("SMOKE FULL STACK: FALHOU", type(error).__name__, error, file=sys.stderr)
        sys.exit(1)
