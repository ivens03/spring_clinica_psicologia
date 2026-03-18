# Guia de Padrões e Diretrizes do Projeto

Este documento define as regras de conduta, arquitetura e organização para os agentes que atuam no desenvolvimento do sistema de Gestão de Clínicas de Psicologia.

## 1. Filosofia do Projeto
- **Objetivo Social:** O software é uma iniciativa sem fins lucrativos para apoio a profissionais em formação. O custo deve ser apenas o de rateio da infraestrutura.
- **Segurança em Primeiro Lugar:** Tratamos dados sensíveis de saúde. A segurança e a privacidade não são opcionais.

## 2. Organização da Documentação
Toda a documentação deve ser mantida de forma modular e clara:
- `DOCS_BUSINESS.md`: **Apenas Regras de Negócio.** Proibido o uso de termos técnicos de TI ou implementação. Deve ser compreensível por um psicólogo ou gestor.
- `DOCS_API.md`: Requisitos técnicos, padrões de arquitetura, segurança (2FA, Criptografia, JWT) e documentação de endpoints.
- `DOCS_DB.md`: Modelagem de dados, dicionário de dados e diagramas ER.
- `agentes.md`: Este guia de padrões.

## 3. Padrões de Desenvolvimento
- **Multi-Tenancy:** Toda tabela e funcionalidade deve considerar o isolamento por clínica (tenant).
- **Simplicidade:** O sistema deve ser acessível (Web/Mobile) e intuitivo.
- **Hierarquia de Acessos (RBAC):** Respeitar rigorosamente os perfis:
    - `Super-Admin`: Gestão global da rede.
    - `Gestor da Clínica`: Dono e administrador da unidade.
    - `Profissional de Saúde`: Foco clínico e prontuários.
    - `Atendente`: Operacional e agendamentos (sem acesso clínico).
    - `Estagiário`: Sempre vinculado e monitorado por um Supervisor.

## 4. Comunicação com o Usuário
- **Transparência:** Sempre explicar a intenção antes de realizar alterações estruturais.
- **Foco no Negócio:** Validar se uma funcionalidade técnica atende à necessidade real descrita no `DOCS_BUSINESS.md`.
- **Manutenção dos DOCS:** Ao adicionar uma funcionalidade, atualizar primeiro o `DOCS_BUSINESS.md` (regra) e depois o `DOCS_API.md` (técnico).

## 5. Pilares Técnicos (Mandatórios)
- Backend: Java 17+ / Spring Boot 3.
- Segurança: Criptografia de prontuários em nível de aplicação.
- Auditoria: Todo acesso a dado sensível deve gerar um log de auditoria.
