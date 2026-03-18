# Documentação Técnica e de Arquitetura (DOCS_API)

## 1. Visão Geral da Solução
Sistema de gestão para clínica de psicologia desenvolvido com Spring Boot, focado em alta segurança e arquitetura multi-tenant (várias clínicas em uma única instância).

## 2. Pilares de Segurança (Prioridade Máxima)
- **Criptografia de Ponta a Ponta:** Implementação de criptografia em nível de aplicação para campos sensíveis (prontuários e notas de sessões).
- **Autenticação:** Utilização de JWT (JSON Web Tokens) com suporte a Autenticação de Dois Fatores (2FA).
- **Autorização (RBAC):** Controle de acesso baseado em funções (Super Admin, Gestor de Clínica, Psicólogo, Secretário, Estagiário).
- **Audit Log:** Trilha de auditoria imutável para registros de acesso e alteração de dados sensíveis (Conformidade LGPD).
- **LGPD:** APIs específicas para portabilidade de dados e exclusão lógica/física (direito ao esquecimento).

## 3. Arquitetura Multi-Tenant
- **Estratégia de Isolamento:** Separação lógica de dados via `tenant_id` em todas as tabelas (ou esquema por clínica, a definir).
- **Independência de Assets:** Armazenamento isolado de logos e arquivos de configuração visual por clínica.

## 4. Tecnologias e Plataformas
- **Backend:** Java 17+ com Spring Boot 3.x.
- **Frontend Web:** React ou Angular (Responsivo).
- **Mobile:** Mobile-first design ou App nativo/híbrido para acesso rápido.
- **Banco de Dados:** PostgreSQL (recomendado para suporte robusto a esquemas e criptografia).

## 5. Infraestrutura e Custos
- **Monitoramento:** Coleta de métricas de uso por clínica para cálculo automático de rateio de custos de servidor.
- **CI/CD:** Pipelines automatizados para garantir a integridade do código e segurança em cada deploy.

## 6. Documentação da API
- **Swagger/OpenAPI:** Disponível em `/swagger-ui.html` para testes e integração com o frontend/mobile.
