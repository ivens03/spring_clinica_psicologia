# Documentação Técnica e de Arquitetura (DOCS_API)

## 1. Visão Geral da Solução
Sistema de gestão para clínica de psicologia desenvolvido com **Java 17** e **Spring Boot 3.x**, focado em alta segurança e arquitetura **Multi-tenant** (isolamento lógico de clínicas em uma única instância).

## 2. Padrões de Arquitetura
- **Estilo Arquitetural:** Hexagonal (Portas e Adaptadores) ou Layered (Controller -> Service -> Repository), priorizando a separação de interesses e testabilidade.
- **Multi-tenancy:** Estratégia de **Discriminator Column** (`tenant_id`) em todas as tabelas. O isolamento será garantido via filtros globais do Hibernate (`@Filter`) ou interceptores no nível do Spring Data JPA.
- **Isolamento de Configuração:** Cada clínica possui seu próprio contexto de cores, logo e parâmetros de agendamento (ex: tempo de sessão padrão).

## 3. Segurança e Privacidade (Prioridade Máxima)
- **Autenticação:** JWT (JSON Web Tokens) com tempo de expiração curto e Refresh Tokens.
- **2FA (Autenticação de Dois Fatores):** Obrigatória para perfis de `Gestor` e `Profissional de Saúde` para acesso a dados sensíveis.
- **Criptografia na Aplicação (Prontuários):**
    - Dados de texto das sessões e anexos multimídia (fotos, áudio, vídeo) serão criptografados usando **AES-256-GCM**.
    - A chave de criptografia deve ser única por clínica ou por paciente (a definir na modelagem de DB).
- **Assinatura Digital:**
    - Documentos oficiais (Atestados, Relatórios) serão assinados utilizando certificados digitais (Padrão ICP-Brasil) via integração com bibliotecas de assinatura ou APIs de terceiro (ex: Assinatura Digital do Governo ou SDKs de certificadoras).
- **RBAC (Role Based Access Control):**
    - `ROLE_SUPER_ADMIN`: Gestão da rede de clínicas.
    - `ROLE_CLINIC_MANAGER`: Gestão total da unidade e auditoria.
    - `ROLE_PROFESSIONAL`: Acesso clínico aos seus pacientes.
    - `ROLE_INTERN`: Acesso restrito a pacientes vinculados sob supervisão.
    - `ROLE_ATTENDANT`: Gestão de agenda e recepção (sem acesso a dados clínicos).

## 4. Gestão de Mídias e Armazenamento
- **Arquivos Multimídia:** Fotos de sessões, áudios de gravação e vídeos serão armazenados em **Object Storage** (S3-compatible como MinIO ou AWS S3).
- **Isolamento de Storage:** Os arquivos serão organizados em pastas por `tenant_id/patient_id/`.
- **Streaming de Áudio/Vídeo:** A API deve suportar o streaming de arquivos criptografados para evitar o download completo antes da reprodução no navegador.

## 5. Notificações e Mensageria
- **Provedores:** Integração com APIs de WhatsApp (ex: Twilio, Z-API) e Email (ex: SendGrid, Amazon SES).
- **Agendamento de Lembretes:**
    - Uso de **Spring Scheduling** ou **Quartz** para processar as filas de notificações de 24h e 1h antes das consultas.
    - O disparo deve considerar o fuso horário configurado para cada clínica.

## 6. Auditoria e Logs
- **Logs de Acesso:** Registro imutável de quem acessou cada prontuário, contendo: `timestamp`, `user_id`, `patient_id`, `action` e `ip_address`.
- **Auditoria de Dados:** Uso de **Envers** ou tabelas de histórico customizadas para rastrear alterações em dados financeiros e cadastrais.

## 7. Módulos da API (Recursos Principais)

### 7.1 Módulo de Identidade (`/auth`, `/users`)
- Login, 2FA, Gestão de Usuários (Vínculo Supervisor-Estagiário), Perfis e Permissões.

### 7.2 Módulo de Clínicas (`/clinics`)
- Cadastro de unidades, identidade visual, gestão de salas e sublocações.

### 7.3 Módulo de Pacientes (`/patients`)
- Cadastro completo, vínculo com responsáveis (menores), histórico de evolução clínica (prontuário).

### 7.4 Módulo de Agendamento (`/appointments`)
- Calendário, reserva de salas, bloqueio de conflitos, confirmação de presença.

### 7.5 Módulo Clínico (`/records`, `/documents`)
- Evoluções (texto/mídia), emissão de atestados e relatórios com assinatura digital.

### 7.6 Módulo Financeiro (`/finance`)
- Fluxo de caixa, pagamentos de sessões (avulso/pacote), rateio de custos entre clínicas.

## 8. Monitoramento e Rateio de Custos
- **Métricas:** Coleta de volume de armazenamento (GB) e número de requisições por clínica para gerar o relatório de rateio de custos operacionais da infraestrutura.
