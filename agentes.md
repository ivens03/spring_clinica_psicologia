# Guia de Padrões, Diretrizes e Conduta dos Agentes

Este documento é a autoridade máxima de conduta para o desenvolvimento do sistema. Ele consolida a ética profissional da psicologia com a engenharia de software de alta segurança.

## 1. Filosofia e Propósito Social
- **Apoio à Formação:** O software é uma iniciativa sem fins lucrativos. Toda decisão técnica deve visar a redução de custos operacionais (rateio de infraestrutura).
- **Sustentabilidade:** Priorizar métricas de consumo precisas para viabilizar a divisão justa dos custos do servidor entre as clínicas.
- **Prevenção de Perda de Dados:** A normalização do banco de dados (3NF) e o uso de integridade referencial são inegociáveis. Perder dados é uma falha ética grave.
- **Observabilidade Total:** O software deve ser transparente. Logs semânticos no terminal e auditoria em banco de dados são fundamentais para a segurança e confiança no sistema. O sistema deve estar preparado para integração com ferramentas de monitoramento externas (APM).

## 2. Terminologia e Interface (Mandatório)
- **Nomenclatura:** É expressamente proibido o uso do termo "Prontuário" na interface ou documentação de usuário.
- **Termo Correto:** **Registro Clínico (Tela do Paciente)** ou **Histórico do Paciente**.
- **Contexto:** O sistema deve parecer uma ferramenta de apoio ao cuidado, não apenas um repositório médico burocrático.

## 3. Padrões de Desenvolvimento e Governança
- **Multi-tenancy:** Uso obrigatório de `tenant_id` em todas as tabelas e queries. O vazamento de dados entre clínicas é uma falha crítica.
- **Exclusão Lógica Permanente:** **Proibido o uso de `DELETE` físico.** Todos os registros devem usar flags de inatividade (`soft delete`) para preservar a auditoria e o histórico de rateio.
- **Auditoria Integral:** Todo acesso ou tentativa de acesso a um **Registro Clínico (Tela do Paciente)** deve gerar um log imutável contendo: Usuário, Data/Hora, IP e Ação.

## 4. Protocolos Éticos de Segurança (RBAC)
- **Sigilo Clínico:** O **Gestor da Clínica** e o **Atendente** jamais devem ter acesso ao conteúdo textual das evoluções clínicas.
- **Hashing de Senhas:** Uso obrigatório do algoritmo **Argon2** (Argon2id preferencialmente) para armazenamento de credenciais.
- **Exposição em URLs:** É terminantemente proibido o envio de dados sensíveis (IDs de pacientes, nomes, CPFs, diagnósticos) via parâmetros de URL (`Query Params`). Devem ser utilizados corpos de requisição (`Request Body`) criptografados ou identificadores opacos (UUIDs) quando estritamente necessário na URL.
- **Monitoramento Quantitativo:** APIs para gestores devem retornar apenas metadados (ex: "Registro preenchido: Sim/Não", "Número de faltas", "Status financeiro"), nunca o conteúdo clínico.
- **Clínica Escola:** O acesso acadêmico é bloqueado por padrão e só é liberado mediante a validação sistêmica do **Contrato de Uso Acadêmico** assinado pelo paciente.
- **Estagiários:** Devem estar obrigatoriamente vinculados a um **Supervisor**, que detém a responsabilidade legal e acesso aos registros para orientação.

## 5. Regras Clínicas Inegociáveis
- **Triagem:** Campo obrigatório para histórico de **Surto Psicótico**.
- **TCLE:** O upload do Termo de Consentimento é o gatilho para liberar qualquer registro de atendimento na **Tela do Paciente**.
- **Imutabilidade:** Após 24 horas (janela de retificação), o registro clínico torna-se imutável. Alterações só via adendos vinculados.

## 6. Organização da Verdade Única
- `DOCS_BUSINESS.md`: O "Porquê" e as Regras de Negócio (Narrativo).
- `DOCS_API.md`: O "Como" (Técnico e Segurança).
- `DOCS_DB.md`: A "Estrutura" (Dados).
- `agentes.md`: A "Conduta" e Padrões Éticos.

**Nota:** Qualquer conflito entre os documentos deve ser resolvido priorizando o `DOCS_BUSINESS.md` para regras de negócio e o `agentes.md` para conduta ética.
## 7. Protocolo de Desenvolvimento (Fluxo Mandatório)
Toda nova funcionalidade ou correção deve seguir rigorosamente esta ordem:
1.  **Documentação:** Atualização dos arquivos `DOCS_*.md` para refletir as mudanças ou novas regras.
2.  **Versionamento da API:** A cada alteração de código que impacte a API, a versão no `OpenApiConfig` deve ser incrementada (ex: `.version("0.0.1")`).
3.  **Testes (TDD):** Escrita de testes automatizados (Unitários, de Integração e Integrados) organizados em pastas e arquivos.
...
3.  **Validação de Testes:** Execução e garantia de sucesso total dos testes antes de iniciar a implementação final.
4.  **Implementação:** Escrita do código seguindo a documentação e os testes.
5.  **Feedback Técnico (Post-Mortem):** Relatório final com pontos de melhoria, lacunas identificadas e análise técnica profunda.

## 8. Organização de Código
O projeto deve ser organizado por **Entidade (Package by Feature)** para garantir coesão e isolamento:
- `psicologia.clinica.<entidade>.model`: Entidades JPA.
- `psicologia.clinica.<entidade>.dtos`: Objetos de transferência de dados (Record preferencialmente).
- `psicologia.clinica.<entidade>.repository`: Interfaces Spring Data.
- `psicologia.clinica.<entidade>.service`: Lógica de negócio e regras de segurança.
- `psicologia.clinica.<entidade>.controller`: Endpoints REST.

**Camadas de Suporte e Infraestrutura:**
- `psicologia.clinica.infrastructure.config.openapi`: Configurações de documentação automática.
- `psicologia.clinica.infrastructure.config.security`: Implementação de Argon2, JWT e filtros de segurança.
- `psicologia.clinica.exception`: Tratamento global de erros e exceções de domínio.

## 9. Excelência Técnica e Padrões de Engenharia
- **Clean Architecture:** Manter a independência entre a lógica de negócio (Entidades/Casos de Uso) e detalhes de infraestrutura (Frameworks, DB, APIs).
- **Clean Code:** Funções pequenas, nomes altamente semânticos, ausência de comentários óbvios e tratamento de exceções específico.
- **Design Patterns:** Uso estratégico de padrões (Factory, Strategy, Observer, Template Method, etc.). O nome das classes deve refletir o padrão utilizado quando pertinente (ex: `RelatorioFinanceiroStrategy`).
- **Java Conventions:** Aderência total às convenções de nomenclatura Java (CamelCase para variáveis/métodos, PascalCase para classes). Uso de **Records** para imutabilidade de DTOs.
- **S.O.L.I.D.:** Aplicação rigorosa dos cinco princípios, especialmente o Princípio da Responsabilidade Única (SRP) e da Inversão de Dependência (DIP).
