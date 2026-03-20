# Documentação Técnica e de Arquitetura (DOCS_API)

## 1. Visão Geral da Solução
Sistema de gestão para clínica de psicologia desenvolvido com **Java 17** e **Spring Boot 3.x**, focado em alta segurança e arquitetura **Multi-tenant**.

## 2. Padrões de Arquitetura
- **Multi-tenancy:** Estratégia de **Discriminator Column** (`tenant_id`) em todas as tabelas.
- **Isolamento de Configuração:** Contexto visual e parâmetros de agendamento por clínica.

## 3. Segurança e Privacidade (Prioridade Máxima)
- **Autenticação:** JWT com Refresh Tokens e 2FA obrigatório para Gestores e Profissionais.
- **Criptografia na Aplicação (Registro Clínico / Tela do Paciente):**
    - Dados sensíveis e anexos são criptografados usando **AES-256-GCM**.
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
