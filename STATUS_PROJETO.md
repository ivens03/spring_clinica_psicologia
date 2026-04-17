# Status do Projeto - Clinica de Psicologia

Atualizado em: 2026-04-17

Este documento funciona como o quadro de acompanhamento do projeto. Ele deve ser atualizado sempre que uma tarefa mudar de estado, um bloqueio for resolvido ou um novo modulo for planejado.

## Legenda

- [x] Concluido
- [~] Parcial
- [ ] Pendente
- [!] Bloqueado

## Resumo Executivo

Status atual: **base executavel restaurada, com JWT, refresh token, 2FA inicial e RBAC aplicado parcialmente**.

O projeto possui a base inicial de Spring Boot, modulo de clinicas, inicio do modulo de usuarios, modulo de acesso com tela unica de login, JWT, refresh token rotacionado, segundo fator obrigatório para gestores/profissionais, RBAC inicial, auditoria modelada parcialmente, OpenAPI configurado e migrations iniciais. A compilacao foi restaurada, as migrations ate `004_autenticacao_jwt_refresh_2fa.sql` foram aplicadas e os testes executados passaram.

Validacao executada:

- Comando: `./mvnw test`
- Resultado: sucesso
- Comando: `./mvnw -Dtest='*IT' test`
- Resultado: sucesso

## Bloqueios Imediatos

| Prioridade | Status | Item | Evidencia | Acao necessaria |
|---|---|---|---|---|
| P0 | [x] | Corrigir pacotes/classes de excecao | `ClinicaService`, `UsuarioService` e `GlobalExceptionHandler` importavam pacotes que nao existiam | Resolvido |
| P0 | [x] | Criar ou remover referencia da migration `003_perfis_and_auditoria_setup.sql` | `db.changelog-master.xml` incluia arquivo ausente | Resolvido com migration 003 |
| P0 | [x] | Reexecutar testes apos compilacao | `./mvnw test` e `./mvnw -Dtest='*IT' test` | Ambos passaram |
| P1 | [!] | Definir ambiente de teste isolado | Testes usam `@ActiveProfiles("dev")`, apontando para PostgreSQL local `psic_dev` | Criar profile `test` com banco isolado ou Testcontainers |

## Inventario Tecnico Atual

| Area | Status | O que existe | Lacunas |
|---|---|---|---|
| Documentacao base | [x] | `agentes.md`, `DOCS_BUSINESS.md`, `DOCS_API.md`, `DOCS_DB.md` | Numeracao do `DOCS_BUSINESS.md` esta fora de ordem em alguns topicos |
| Build Maven | [x] | `pom.xml`, Maven Wrapper, Java 21 e Spring Boot 3.5.11 | Sem lacuna documental conhecida |
| Modulo Clinica | [~] | Entity, DTOs, repository, service, controller, testes unitarios e integracao | Sem `tenant_id`; usa identificador fiscal como chave natural; sem auditoria de alteracoes |
| Modulo Usuario | [~] | Entity, DTOs, repository, service, controller, testes iniciais; GET/DELETE protegidos por RBAC inicial | Sem update; criacao ainda publica para bootstrap |
| Modulo Acesso | [~] | `POST /acesso/entrar`, `POST /acesso/confirmar-2fa`, `POST /acesso/refresh`, `GET/POST /login` e `GET/POST /login/2fa`; emite JWT, refresh token e registra auditoria | Entrega real do codigo 2FA ainda nao integrada a e-mail/SMS/app autenticador |
| Perfis | [~] | Entidades `Gestor`, `Funcionario`, `Estagiario`, enums e migration inicial | Regras de supervisor e perfil ainda incompletas |
| Auditoria | [~] | Entidades `Acesso`, `Alteracao`, repositories, interface `Auditable` e migration inicial | Logs nao sao globais; falta IP real; falta imutabilidade forte |
| OpenAPI | [~] | Configuracao com versao `0.0.7` | Falta detalhar schemas de seguranca no Swagger |
| Seguranca | [~] | `PasswordEncoder` Argon2id com pepper via `PASSWORD_PEPPER`; JWT via `JWT_SECRET`; refresh token persistido; 2FA inicial; RBAC por roles | `POST /clinicas` e `POST /usuarios` ainda publicos para bootstrap/testes; sem canal real de entrega 2FA |
| Seed de desenvolvimento | [x] | Classe `DevUsuariosSeeder` restrita ao profile `dev`, com usuários por papel, e-mails/senha via ambiente ou `.env`, e senha Argon2id | Ainda depende do banco `psic_dev` até existir profile `test` isolado |
| Banco/Liquibase | [~] | Schemas/tabelas iniciais para clinicas, usuarios, perfis e auditoria | Sem `tenant_id` padrao |
| Tratamento de erros | [~] | `ApiError`, `GlobalExceptionHandler`, excecoes comuns e excecoes de usuario | CorrelationId e gerado no handler, mas nao propagado por request/log |
| Testes | [~] | Testes para clinicas, usuarios e OpenAPI passando | Integracao depende de PostgreSQL dev |

## Superficie de API Implementada

| Endpoint | Status | Observacoes |
|---|---|---|
| `POST /clinicas` | [~] | Cria clinica; validacao basica de identificador fiscal por tamanho |
| `GET /clinicas` | [~] | Lista clinicas ativas por filtro `@Where` |
| `GET /clinicas/{id}` | [~] | Busca por identificador fiscal |
| `PUT /clinicas/{id}` | [~] | Atualiza dados basicos |
| `DELETE /clinicas/{id}` | [~] | Endpoint HTTP DELETE, mas service executa soft delete |
| `POST /acesso/entrar` | [~] | Valida credenciais por e-mail/senha; emite tokens para perfis sem 2FA ou abre desafio para perfis obrigatorios |
| `POST /acesso/confirmar-2fa` | [~] | Confirma desafio de segundo fator e emite access/refresh token |
| `POST /acesso/refresh` | [~] | Rotaciona refresh token e emite novo access token |
| `GET /login` | [~] | Exibe a tela unica de login |
| `POST /login` | [~] | Valida credenciais e redireciona para 2FA quando obrigatorio |
| `GET /login/2fa` | [~] | Exibe formulario de segundo fator |
| `POST /login/2fa` | [~] | Confirma segundo fator, grava cookies HttpOnly e redireciona por perfil |
| `GET /gestao/sistema` | [~] | Placeholder com `<h1>GESTOR_SISTEMA</h1>` |
| `GET /gestao/clinica` | [~] | Placeholder com `<h1>GESTOR_CLINICA</h1>` |
| `GET /atendimento/pacientes` | [~] | Placeholder com `<h1>PROFISSIONAL_SAUDE</h1>` |
| `GET /supervisao/estagiario` | [~] | Placeholder com `<h1>ESTAGIARIO</h1>` |
| `GET /atendimento/agenda` | [~] | Placeholder com `<h1>ATENDIMENTO</h1>` |
| `POST /usuarios` | [~] | Implementado parcialmente; cria usuario, perfis e log inicial de auditoria |
| `GET /usuarios` | [~] | Implementado parcialmente |
| `GET /usuarios/{cpf}` | [!] | Usa CPF na URL, o que conflita com a diretriz de nao expor dados sensiveis em parametros/rotas |
| `DELETE /usuarios/{cpf}` | [!] | Soft delete implementado, mas usa CPF na URL |

## Conformidade com Regras dos Documentos

| Regra | Status | Observacao |
|---|---|---|
| Nao usar termo proibido na interface/documentacao de usuario | [x] | Nao foi encontrado uso fora da regra em `agentes.md` |
| Package by feature | [x] | Estrutura atual segue `clinica`, `usuario`, `infrastructure`, `exception` |
| DTOs como records | [x] | DTOs atuais usam `record` |
| Soft delete | [~] | Clinica e usuario usam `ativo`/`excluido_em`; falta padrao global e migrations completas |
| Auditoria integral | [~] | Estrutura parcial existe; ainda nao cobre todo acesso e alteracao sensivel |
| Multi-tenancy com `tenant_id` | [!] | Implementacao usa `clinica_id` em usuarios/auditoria e nao tem `tenant_id` em todas as tabelas |
| Sigilo clinico e RBAC | [!] | Ainda nao ha RBAC aplicado; todas as rotas estao liberadas |
| Argon2id | [~] | Encoder oficial com pepper e testes dedicados | Falta integracao com autenticacao real |
| PHI fora de URL/query params | [!] | `GET/DELETE /usuarios/{cpf}` expoe CPF em rota |
| TCLE obrigatorio | [ ] | Modulo ainda nao implementado |
| Triagem com campo de surto psicotico | [ ] | Modulo ainda nao implementado |
| Registro Clinico imutavel apos 24h | [ ] | Modulo ainda nao implementado |
| AES-256-GCM para dados sensiveis/anexos | [ ] | Modulo ainda nao implementado |
| Correlation ID em todas as requisicoes | [~] | Existe no `ApiError`, mas nao ha filtro de propagacao |
| Liquibase com autor correto | [x] | Migrations existentes usam `ivens magno da costa lisboa` |

## Quadro de Trabalho

### P0 - Restaurar Base Executavel

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [x] | Corrigir imports/pacotes de excecao comuns | `./mvnw test` passa da fase de compilacao |
| [x] | Criar excecoes especificas de usuario ou ajustar `UsuarioService` para excecoes existentes | `CpfDuplicadoException`, `EmailDuplicadoException` e `UsuarioException` resolvidas |
| [x] | Criar migration `003_perfis_and_auditoria_setup.sql` | Liquibase encontra todos os arquivos referenciados |
| [x] | Rodar `./mvnw test` novamente | Suite executada e resultado registrado neste arquivo |
| [ ] | Criar profile de teste | Testes nao dependem do banco `psic_dev` de desenvolvimento |

### P1 - Segurança Basica e Identidade

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [x] | Implementar autenticacao JWT | Login/2FA emite access token e refresh token |
| [x] | Implementar refresh token | Renovacao controlada, persistida e rotacionada |
| [~] | Remover `permitAll` global | Rotas principais exigem autenticacao conforme perfil; bootstrap de clinica/usuario ainda publico |
| [~] | Implementar RBAC inicial | Gestor, profissional, atendente, estagiario e secretaria com permissoes separadas em rotas iniciais |
| [~] | Implementar 2FA para gestores e profissionais | Fluxo exige segundo fator; falta canal real de entrega em producao |
| [ ] | Evitar CPF em rotas publicas | Usar identificadores opacos quando houver exposicao por URL |
| [x] | Criar usuários padrão de desenvolvimento | Usuários dev criados somente no profile `dev`, com senha codificada |
| [x] | Aplicar pepper nas senhas | `PasswordEncoder` aplica pepper via `PASSWORD_PEPPER` antes do Argon2id |
| [x] | Criar entrada inicial no sistema | Endpoint retorna direcionamento por perfil sem expor CPF, senha ou hash |
| [x] | Criar tela unica de login inicial | `GET /login` exibe formulario e `POST /login` redireciona por perfil |
| [x] | Criar tabelas de autenticacao | Refresh tokens e desafios de segundo fator persistidos via Liquibase |

### P1 - Banco, Multi-tenancy e Auditoria

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [!] | Decidir padrao `tenant_id` versus `clinica_id` | Documentos e schema ficam consistentes |
| [ ] | Aplicar coluna de tenant em todas as tabelas necessarias | Todas as tabelas multi-tenant seguem o padrao definido |
| [x] | Criar tabelas `perfis.gestores`, `perfis.funcionarios`, `perfis.estagiarios` | Entidades JPA validam contra schema |
| [x] | Criar tabelas `auditoria.acessos` e `auditoria.alteracoes` | Repositories funcionam com Liquibase |
| [ ] | Implementar auditoria automatica de alteracoes | Antes/depois de registros sensiveis sao persistidos |
| [ ] | Registrar IP real nos acessos auditaveis | Log inclui usuario, data/hora, IP e acao |

### P1 - Modulo Clinica

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [~] | CRUD basico de clinicas | Fluxo compila, testa e persiste |
| [ ] | Validar CPF/CNPJ de forma real | Nao aceitar apenas por tamanho |
| [ ] | Revisar chave primaria natural | Decisao documentada sobre identificador fiscal versus UUID opaco |
| [ ] | Auditar criacao/alteracao/remocao logica de clinicas | Evento de auditoria salvo por acao |

### P1 - Modulo Usuario e Perfis

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [~] | Criacao de usuario | Compila, persiste usuario e perfil especifico |
| [ ] | Atualizacao de usuario | Endpoint e service com testes |
| [ ] | Soft delete com auditoria | Remocao logica registra evento |
| [ ] | Validar supervisor obrigatorio para estagiario | Estagiario nao nasce sem supervisor valido |
| [ ] | Validar conselho profissional quando aplicavel | Profissional de saude exige conselho e registro |
| [ ] | Proteger senha no response | DTO nunca retorna senha ou hash |

### P2 - Modulo Clinico

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [ ] | Cadastro de pacientes | Modelo, migration, API e testes |
| [ ] | Triagem biopsicossocial | Campo de surto psicotico obrigatorio |
| [ ] | Upload e controle de TCLE | Registro de atendimento bloqueado sem TCLE |
| [ ] | Registro Clinico | Evolucoes, anexos e controle de acesso por profissional |
| [ ] | Janela de retificacao de 24h | Alteracao direta bloqueada apos prazo |
| [ ] | Adendos vinculados | Alteracoes apos 24h somente por adendo |
| [ ] | Contrato de uso academico | Acesso academico bloqueado sem contrato |

### P2 - Modulos Operacionais

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [ ] | Agenda e recorrencia | Criar, listar, remarcar e cancelar horarios |
| [ ] | Urgencia clinica | Priorizacao sem quebrar sigilo |
| [ ] | Financeiro e livro caixa | Lancamentos, pagamentos e selagem mensal |
| [ ] | Salas e sublocacao | Bloqueio de conflito de horarios |
| [ ] | Aba de acompanhamento | Indicadores quantitativos sem conteudo clinico |
| [ ] | Relatorios de rateio | Consumo de armazenamento, trafego e notificacoes por clinica |

### P2 - Observabilidade

| Status | Tarefa | Criterio de aceite |
|---|---|---|
| [~] | `ApiError` padronizado | Todos os erros seguem contrato e compilam |
| [ ] | Filtro de correlation ID | ID entra na request, logs e resposta |
| [ ] | Logs semanticos | Acoes de dominio relevantes registradas |
| [ ] | Preparacao para APM | Metricas/tracing definidos por profile |

## Riscos Tecnicos

| Risco | Impacto | Mitigacao |
|---|---|---|
| Rotas com CPF | Viola regra de nao expor dado sensivel em URL | Adotar UUID opaco ou rotas por corpo de requisicao quando adequado |
| Bootstrap publico | `POST /clinicas` e `POST /usuarios` ainda publicos | Criar provisionamento administrativo autenticado e fechar essas rotas |
| `tenant_id` nao padronizado | Risco de isolamento fraco entre clinicas | Consolidar estrategia multi-tenant antes de expandir schema |
| Testes em profile dev | Suite fragil e dependente de ambiente local | Profile `test` isolado |

## Decisoes Pendentes

| Decisao | Opcao atual | Necessidade |
|---|---|---|
| Identificador publico de usuario | CPF esta em path variable | Trocar por UUID opaco ou outro identificador seguro |
| Nome da coluna tenant | Docs exigem `tenant_id`; implementacao usa `clinica_id` | Padronizar antes das proximas migrations |
| Estrategia de auditoria | Entidades existem, mas sem mecanismo global | Definir listener, service ou aspecto |
| Banco de teste | Atualmente `dev` | Criar `test` com isolamento |

## Historico de Validacao

| Data | Comando | Resultado | Observacao |
|---|---|---|---|
| 2026-04-15 | `./mvnw test` | Falhou | Erros de compilacao por imports/classes de excecao inexistentes |
| 2026-04-15 | `./mvnw test` | Passou | 8 testes executados, 0 falhas, 0 erros |
| 2026-04-15 | `./mvnw -Dtest='*IT' test` | Passou | 9 testes de integracao executados, 0 falhas, 0 erros |
| 2026-04-15 | `./mvnw test` | Passou | 10 testes executados, 0 falhas, 0 erros apos seed dev |
| 2026-04-15 | `./mvnw -Dtest='*IT' test` | Passou | 9 testes de integracao executados, 0 falhas, 0 erros apos seed dev |
| 2026-04-16 | Revisao dos Markdown | Passou | `agentes.md`, `DOCS_BUSINESS.md`, `DOCS_API.md`, `DOCS_DB.md` e `STATUS_PROJETO.md` alinhados ao estado atual |
| 2026-04-17 | `./mvnw test` | Passou | 12 testes executados, 0 falhas, 0 erros apos acesso inicial |
| 2026-04-17 | `./mvnw -Dtest='*IT' test` | Passou | 11 testes de integracao executados, 0 falhas, 0 erros apos acesso inicial |
| 2026-04-17 | `./mvnw test` | Passou | 12 testes executados, 0 falhas, 0 erros apos tela unica de login |
| 2026-04-17 | `./mvnw -Dtest='*IT' test` | Passou | 15 testes de integracao executados, 0 falhas, 0 erros apos tela unica de login |
| 2026-04-17 | `./mvnw test` | Passou | 12 testes executados, 0 falhas, 0 erros apos JWT, refresh token, 2FA e RBAC inicial |
| 2026-04-17 | `./mvnw -Dtest='*IT' test` | Passou | 18 testes de integracao executados, 0 falhas, 0 erros apos JWT, refresh token, 2FA e RBAC inicial |

## Proxima Sprint Recomendada

1. Criar profile de teste isolado.
2. Remover CPF das rotas de usuario antes de consolidar a API.
3. Fechar bootstrap autenticado para `POST /clinicas` e `POST /usuarios`.
4. Decidir e aplicar padrao de multi-tenancy (`tenant_id`).
5. Integrar entrega real de 2FA.
6. Evoluir auditoria para capturar IP real e eventos globais.
