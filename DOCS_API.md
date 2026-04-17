# Documentação Técnica e de Arquitetura (DOCS_API)

## 1. Visão Geral da Solução
Sistema de gestão para clínica de psicologia desenvolvido com **Java 21** e **Spring Boot 3.x**, focado em alta segurança e arquitetura **Multi-tenant**.

## 2. Padrões de Arquitetura
- **Multi-tenancy:** Estratégia de **Discriminator Column** (`tenant_id`) em todas as tabelas.
- **Isolamento de Configuração:** Contexto visual e parâmetros de agendamento por clínica.
- **Estrutura de Pacotes (Package by Feature):**
    - `psicologia.clinica.<entidade>.{model, dtos, repository, service, controller}`.
- **Estrutura de Infraestrutura:**
    - `psicologia.clinica.infrastructure.config.{openapi, security}`: Configurações técnicas isoladas.
    - `psicologia.clinica.exception`: Centralização de tratamento de erros global.
- O objetivo é manter a coesão do domínio e facilitar a manutenção.

## 3. Segurança e Privacidade (Prioridade Máxima)
- **Autenticação:** JWT HMAC-SHA256 com Refresh Tokens persistidos e rotacionados.
- **2FA:** obrigatório para `GESTOR_SISTEMA`, `GESTOR_CLINICA` e `PROFISSIONAL_SAUDE`. Em `dev`, o código pode ser exposto para testes locais; em produção, a entrega do código deve ser integrada a canal externo seguro.
- **RBAC:** aplicado via roles derivadas de `PerfilRoot` e `SubPerfil`.
- **Hashing de Senhas:** **Argon2id** via `PasswordEncoder` oficial, com salt dinâmico por senha e custo computacional configurável.
- **Pepper de Senhas:** O segredo global de pepper deve vir de `PASSWORD_PEPPER`. Em desenvolvimento local, pode ser carregado por arquivo `.env`; em produção, deve vir de variável de ambiente, secret manager ou mecanismo equivalente. O `.env` local não deve ser versionado.
- **Minimização de PHI em URLs:** 
    - PHI (Protected Health Information) nunca deve transitar via `Query Parameters`.
    - Uso de IDs opacos (UUIDs) para exposição em rotas REST, mitigando ataques de enumeração.
- **Criptografia na Aplicação (Registro Clínico / Tela do Paciente):**
    - Dados sensíveis e anexos são criptografados usando **AES-256-GCM**.
...
- **RBAC (Role Based Access Control):**
    - `ROLE_CLINIC_MANAGER`: Sem acesso ao conteúdo do **Registro Clínico (Tela do Paciente)**.
    - `ROLE_PROFESSIONAL`: Acesso integral apenas aos seus próprios pacientes na **Tela do Paciente**.
    - `ROLE_INTERN`: Acesso ao **Registro Clínico** sob supervisão.

## 4. Gestão de Mídias e Armazenamento
- **Arquivos Multimídia:** Armazenados em Object Storage (S3-compatible) organizados por `tenant_id/patient_id/`.

## 5. Auditoria e Logs
- **Logs de Acesso:** Registro imutável de quem acessou cada **Registro Clínico (Tela do Paciente)**.
- **Não-Exclusão Física:** O sistema implementa exclusão lógica. Registros "apagados" são apenas marcados como inativos, preservando dados para auditoria.

## 6. Módulos da API

### 6.0 Módulo de Acesso (`/acesso`)
- `POST /acesso/entrar`: valida e-mail e senha enviados no corpo da requisição, registra auditoria de acesso quando o usuário é identificado e retorna o direcionamento inicial conforme perfil.
- Se o perfil exigir 2FA, retorna `segundoFatorObrigatorio=true`, `desafioSegundoFatorId` e não emite tokens até a confirmação do código.
- `POST /acesso/confirmar-2fa`: valida o desafio de segundo fator e emite `accessToken` JWT e `refreshToken`.
- `POST /acesso/refresh`: valida o refresh token, revoga o token usado e emite um novo par de tokens.
- A resposta não expõe CPF, senha ou hash de senha.
- A tela única de login fica em `GET /login`; o formulário envia `POST /login` e redireciona para a rota inicial retornada pelo serviço de acesso.
- Quando o perfil exige 2FA, `POST /login` redireciona para `GET /login/2fa`; `POST /login/2fa` confirma o código, grava cookies HttpOnly `ACCESS_TOKEN` e `REFRESH_TOKEN` e redireciona para o painel do perfil.
- `GET /` redireciona para `GET /login`.
- Direcionamentos atuais:
    - `GESTOR_SISTEMA`: `PAINEL_GESTOR_SISTEMA`, rota `/gestao/sistema`.
    - `GESTOR_CLINICA`: `PAINEL_GESTOR_CLINICA`, rota `/gestao/clinica`.
    - `PROFISSIONAL_SAUDE`: `PAINEL_PROFISSIONAL_SAUDE`, rota `/atendimento/pacientes`.
    - `ESTAGIARIO`: `PAINEL_ESTAGIARIO`, rota `/supervisao/estagiario`.
    - `SECRETARIA` e `ATENDENTE`: `PAINEL_ATENDIMENTO`, rota `/atendimento/agenda`.
- As telas de destino atuais são placeholders simples com `<h1>` contendo o tipo do usuário; já exigem JWT/cookie de acesso e role compatível.

### 6.0.1 Regras RBAC iniciais
- `/gestao/sistema`: exige `ROLE_GESTOR_SISTEMA`.
- `/gestao/clinica`: exige `ROLE_GESTOR_CLINICA`.
- `/atendimento/pacientes`: exige `ROLE_PROFISSIONAL_SAUDE`.
- `/supervisao/estagiario`: exige `ROLE_ESTAGIARIO`.
- `/atendimento/agenda`: exige `ROLE_ATENDENTE` ou `ROLE_SECRETARIA`.
- `GET/DELETE /usuarios/**`: exige `ROLE_GESTOR_SISTEMA` ou `ROLE_GESTOR_CLINICA`.
- `/clinicas/**`, exceto `POST /clinicas`: exige `ROLE_GESTOR_SISTEMA` ou `ROLE_GESTOR_CLINICA`.
- `POST /clinicas` e `POST /usuarios` permanecem públicos temporariamente para bootstrap/testes até existir fluxo de provisionamento administrativo autenticado.

### 6.1 Módulo Clínico (`/records`, `/documents`)
- Gerenciamento do **Registro Clínico (Tela do Paciente)**: evoluções, mídias e contratos acadêmicos.

### 6.2 Módulo de Supervisão (`/monitoring`)
- Fornece indicadores quantitativos (status de preenchimento, frequência) para o Gestor, garantindo o sigilo do conteúdo.

## 7. Padrões de Implementação Técnica
- **Princípios S.O.L.I.D. e Clean Architecture.**
- **Padrões de Projeto (Design Patterns):** Nomeação explícita do padrão no sufixo ou prefixo (ex: `PacienteBuilder`).
- **Records do Java 21:** Uso obrigatório para DTOs.
- **Validação:** Uso de `Spring Validation` (`@Valid`, `@NotNull`) nos endpoints.
- **Tratamento de Exceções Global:** Concentrado em um `ControllerAdvice` para respostas de erro padronizadas.

### 7.1 Padrão de Resposta de Erro (ApiError)
Toda resposta de erro (4xx e 5xx) deve seguir obrigatoriamente esta estrutura:
- `timestamp`: Data e hora do ocorrido (ISO-8601).
- `status`: Código HTTP do erro.
- `error`: Nome curto do erro (ex: "Bad Request").
- `message`: Mensagem amigável para o desenvolvedor/usuário.
- `path`: URL do endpoint que originou o erro.
- `correlationId`: ID único da requisição para rastreamento em logs (Observabilidade).

## 8. Observabilidade e Monitoramento
...
- **Rastreabilidade (Tracing):** Uso de `Correlation ID` em todas as requisições para rastrear o fluxo de uma operação entre diferentes camadas e serviços.

## 9. Gestão de Ambientes
O sistema utiliza **Spring Profiles** para isolamento de configurações:
- **Development (`dev`):** Banco de dados local `psic_dev`, logs em nível DEBUG, banner customizado.
- **Production (`prod`):** Banco de dados de produção (configurado via variáveis de ambiente), logs em nível INFO, banner customizado, auditoria estrita.

### 9.1 Carga de Usuários de Desenvolvimento
No profile `dev`, o sistema pode criar automaticamente usuários de teste para facilitar validações locais.

- A carga deve ser restrita ao profile `dev` por `@Profile("dev")`.
- A senha e os e-mails podem ser configurados por variáveis de ambiente ou pelo arquivo `.env` local.
- Mesmo em desenvolvimento, as senhas devem ser persistidas usando o `PasswordEncoder` oficial da aplicação, atualmente Argon2id com pepper.
- A carga deve ser idempotente: se o CPF já existir, o usuário não deve ser recriado; em `dev`, e-mail e senha podem ser sincronizados a partir das variáveis locais.
- Tokens hardcoded não devem ser usados enquanto o módulo real de autenticação JWT não existir, para evitar um contrato de segurança falso.

Variáveis reconhecidas em `dev`:

| Variável | Uso | Padrão |
|---|---|---|
| `DEV_USER_PASSWORD` | Senha comum para os usuários dev | `Dev@123456` |
| `DEV_GESTOR_SISTEMA_EMAIL` | E-mail do gestor do sistema | `dev.gestor.sistema@clinica.local` |
| `DEV_GESTOR_CLINICA_EMAIL` | E-mail do gestor da clínica | `dev.gestor.clinica@clinica.local` |
| `DEV_PROFISSIONAL_EMAIL` | E-mail do profissional de saúde | `dev.profissional@clinica.local` |
| `DEV_ATENDENTE_EMAIL` | E-mail do atendente | `dev.atendente@clinica.local` |
| `DEV_SECRETARIA_EMAIL` | E-mail da secretaria | `dev.secretaria@clinica.local` |
| `DEV_ESTAGIARIO_EMAIL` | E-mail do estagiário | `dev.estagiario@clinica.local` |
| `JWT_SECRET` | Segredo de assinatura do JWT | Sem padrão seguro; em dev pode reutilizar segredo local |
| `DEV_2FA_CODE` | Código fixo de 2FA para automação em dev | Código aleatório quando ausente |

Contas criadas no ambiente `dev`:

| Papel | E-mail | CPF | Senha |
|---|---|---|---|
| Gestor Sistema | `dev.gestor.sistema@clinica.local` | `00000000001` | `Dev@123456` |
| Gestor Clínica | `dev.gestor.clinica@clinica.local` | `00000000002` | `Dev@123456` |
| Profissional Saúde | `dev.profissional@clinica.local` | `00000000003` | `Dev@123456` |
| Atendente | `dev.atendente@clinica.local` | `00000000004` | `Dev@123456` |
| Secretaria | `dev.secretaria@clinica.local` | `00000000005` | `Dev@123456` |
| Estagiário | `dev.estagiario@clinica.local` | `00000000006` | `Dev@123456` |
