import { test, expect } from '@playwright/test';
import { mkdirSync } from 'node:fs';

const pastaCapturas = 'test-results/capturas';
mkdirSync(pastaCapturas, { recursive: true });

// Os testes usam um servidor local com H2 temporário e contas sem dados reais.
// O backend é iniciado pelo GitHub Actions com conta ADMIN gerada para a execução.
const adminEmail = process.env.ADOTAPET_ADMIN_EMAIL;
const adminPassword = process.env.ADOTAPET_ADMIN_PASSWORD;

test('site público abre, oferece filtros e navegação responsiva', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/AdotaPet/);
  await expect(page.locator('#filter-form')).toBeVisible();
  await expect(page.locator('#btn-account')).toBeVisible();
  await page.screenshot({ path: pastaCapturas + '/site-desktop.png', fullPage: true, animations: 'disabled' });

  // O catálogo só mostra animais cadastrados: não inventamos fichas de pets.
  await expect(page.locator('#catalog-feedback')).not.toContainText('Carregando');
  await page.locator('#filter-species').selectOption('GATO');
  await expect(page.locator('#catalog-feedback')).not.toContainText('Buscando');

  // Em telas estreitas, os links ficam no menu responsivo.
  await page.setViewportSize({ width: 390, height: 844 });
  await expect(page.locator('#menu-toggle')).toBeVisible();
  await page.locator('#menu-toggle').click();
  await expect(page.locator('#main-menu')).toBeVisible();
  await expect(page.locator('#main-menu a[href="#animais"]')).toBeVisible();
  await page.locator('#menu-toggle').click();
  await expect(page.locator('#main-menu')).toBeHidden();
  await page.screenshot({ path: pastaCapturas + '/site-mobile.png', fullPage: true, animations: 'disabled' });
});

test('painel não revela dados administrativos a visitante sem login', async ({ page }) => {
  await page.goto('/painel.html');
  await expect(page.locator('#panel-content')).toContainText('Acesso restrito');
  await expect(page.locator('#panel-content').getByRole('link', { name: 'Voltar ao site' })).toBeVisible();
});

test('adotante solicita e ONG formaliza adoção pelo navegador', async ({ page }) => {
  test.skip(!adminEmail || !adminPassword, 'Requer credenciais ADMIN efêmeras somente na CI.');

  const unique = Date.now().toString(36) + '-' + Math.random().toString(36).slice(2, 7);
  const abrigo = 'Abrigo E2E ' + unique;
  const animal = 'Pet E2E ' + unique;
  const adotanteEmail = 'adotante-e2e-' + unique + '@example.invalid';
  const adotanteSenha = 'SenhaE2eForte123!';

  // Primeiro, o ADMIN faz login usando o próprio formulário do frontend.
  await page.goto('/');
  await page.locator('#btn-account').click();
  await expect(page.locator('#auth-form')).toBeVisible();
  await page.locator('#auth-email').fill(adminEmail);
  await page.locator('#auth-password').fill(adminPassword);
  await page.locator('#auth-form button[type="submit"]').click();
  await expect(page.locator('#btn-staff')).toBeVisible();

  // Cadastro de abrigo via painel real, sem inserir fixtures diretamente no banco.
  await page.locator('#btn-staff').click();
  await expect(page).toHaveURL(/\/painel\.html/);
  await expect(page.locator('#current-user')).toContainText('ADMIN');
  await page.locator('#panel-nav [data-view="abrigos"]').click();
  await page.locator('#view-main-action').click();
  await expect(page.locator('#operation-form')).toBeVisible();
  await page.locator('#f-nome').fill(abrigo);
  await page.locator('#f-endereco').fill('Rua Exemplo, 123');
  await page.locator('#f-cidade').fill('Jaraguá do Sul');
  await page.locator('#f-uf').fill('SC');
  await page.locator('#f-capacidade').fill('5');
  await page.locator('#operation-form button[type="submit"]').click();
  await expect(page.locator('#form-dialog')).toBeHidden();
  await expect(page.locator('#panel-content')).toContainText(abrigo);

  // Cadastra o animal e confirma que aparece na tabela administrativa.
  await page.locator('#panel-nav [data-view="animais"]').click();
  await page.locator('#view-main-action').click();
  await page.locator('#f-nome').fill(animal);
  await page.locator('#f-abrigoId').selectOption({ label: abrigo + ' — Jaraguá do Sul' });
  await page.locator('#f-especie').selectOption('CAO');
  await page.locator('#f-sexo').selectOption('FEMEA');
  await page.locator('#f-porte').selectOption('PEQUENO');
  await page.locator('#operation-form button[type="submit"]').click();
  await expect(page.locator('#form-dialog')).toBeHidden();
  await expect(page.locator('#panel-content')).toContainText(animal);
  await page.screenshot({ path: pastaCapturas + '/painel-ong-animais.png', fullPage: true, animations: 'disabled' });

  // Volta à home; encerra sessão do ADMIN.
  await page.goto('/');
  await expect(page.locator('#pet-grid')).toContainText(animal);
  await page.locator('#btn-logout').click();
  await expect(page.locator('#btn-account')).toBeVisible();

  // Autocadastro do adotante via interface — sem privilégios de gestão.
  await page.locator('#btn-account').click();
  await page.locator('#switch-auth').click();
  await page.locator('#auth-name').fill('Pessoa de Homologação');
  await page.locator('#auth-email').fill(adotanteEmail);
  await page.locator('#auth-password').fill(adotanteSenha);
  await page.locator('#auth-form button[type="submit"]').click();
  await expect(page.locator('#btn-requests')).toBeVisible();
  await expect(page.locator('#btn-staff')).toBeHidden();

  // Filtra catálogo e envia justificativa de adoção.
  await page.locator('#filter-species').selectOption('CAO');
  const filtroCidade = page.waitForResponse(
    response => response.url().includes('cidade=Jaragu') &&
      response.url().includes('/api/animais?') && response.ok()
  );
  await page.locator('#filter-city').fill('Jaraguá do Sul');
  await filtroCidade;
  const card = page.locator('.pet-card').filter({ hasText: animal });
  await expect(card).toBeVisible();
  await card.locator('[data-adopt]').click();
  await expect(page.locator('#adoption-form')).toBeVisible();
  await page.locator('#adoption-reason').fill('Tenho condições de oferecer um lar seguro e cuidado veterinário.');
  await page.locator('#adoption-form button[type="submit"]').click();
  await expect(page.locator('#app-dialog')).toBeHidden();

  // O pedido precisa aparecer na conta do adotante como PENDENTE.
  await page.locator('#btn-requests').click();
  await expect(page.locator('#dialog-content')).toContainText(animal);
  await expect(page.locator('#dialog-content')).toContainText('Pendente');
  await page.locator('#dialog-close').click();
  await page.locator('#btn-logout').click();

  // Retorna como ADMIN, analisa, aprova e formaliza dentro do painel.
  await page.locator('#btn-account').click();
  await page.locator('#auth-email').fill(adminEmail);
  await page.locator('#auth-password').fill(adminPassword);
  await page.locator('#auth-form button[type="submit"]').click();
  await expect(page.locator('#btn-staff')).toBeVisible();
  await page.locator('#btn-staff').click();
  await page.locator('#panel-nav [data-view="solicitacoes"]').click();
  const linhaPedido = page.locator('#panel-content tr').filter({ hasText: animal });
  await expect(linhaPedido).toBeVisible();
  page.once('dialog', dialog => dialog.accept());
  await linhaPedido.locator('[data-action="analisar"]').click();
  await expect(linhaPedido.locator('[data-action="aprovar"]')).toBeVisible();
  await linhaPedido.locator('[data-action="aprovar"]').click();
  await page.locator('#f-parecer').fill('Aprovado após verificação de condições e responsabilidades.');
  await page.locator('#operation-form button[type="submit"]').click();
  await expect(page.locator('#form-dialog')).toBeHidden();
  await expect(linhaPedido.locator('[data-action="formalizar"]')).toBeVisible();
  await linhaPedido.locator('[data-action="formalizar"]').click();
  await page.locator('[name="termoAssinado"]').check();
  await page.locator('#f-observacoes').fill('Termo de responsabilidade assinado.');
  await page.locator('#operation-form button[type="submit"]').click();
  await expect(page.locator('#form-dialog')).toBeHidden();

  // A adoção formalizada deve constar da tela específica do painel.
  await page.locator('#panel-nav [data-view="adocoes"]').click();
  await expect(page.locator('#panel-content')).toContainText(animal);
  await page.locator('#panel-nav [data-view="relatorios"]').click();
  await expect(page.locator('#view-title')).toHaveText('Relatórios');
  await expect(page.locator('#panel-content')).toContainText('Animais adotados');
});
