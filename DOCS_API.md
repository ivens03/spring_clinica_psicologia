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
- **Autenticação:** JWT com Refresh Tokens e 2FA obrigatório para Gestores e Profissionais.
- **Hashing de Senhas:** **Argon2id** (com salt dinâmico e custo computacional configurável).
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
- As senhas podem ficar hardcoded somente nessa classe de desenvolvimento.
- Mesmo em desenvolvimento, as senhas devem ser persistidas usando o `PasswordEncoder` oficial da aplicação, atualmente Argon2id.
- A carga deve ser idempotente: se o CPF já existir, o usuário não deve ser recriado.
- Tokens hardcoded não devem ser usados enquanto o módulo real de autenticação JWT não existir, para evitar um contrato de segurança falso.

Contas criadas no ambiente `dev`:

| Papel | E-mail | CPF | Senha |
|---|---|---|---|
| Gestor Sistema | `dev.gestor.sistema@clinica.local` | `00000000001` | `Dev@123456` |
| Gestor Clínica | `dev.gestor.clinica@clinica.local` | `00000000002` | `Dev@123456` |
| Profissional Saúde | `dev.profissional@clinica.local` | `00000000003` | `Dev@123456` |
| Atendente | `dev.atendente@clinica.local` | `00000000004` | `Dev@123456` |
| Secretaria | `dev.secretaria@clinica.local` | `00000000005` | `Dev@123456` |
| Estagiário | `dev.estagiario@clinica.local` | `00000000006` | `Dev@123456` |
