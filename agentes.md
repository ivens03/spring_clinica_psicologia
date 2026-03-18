# Guia de Padrões, Diretrizes e Conduta dos Agentes

Este documento estabelece as regras de conduta, arquitetura e organização para todos os agentes envolvidos no desenvolvimento e operação do ecossistema de Gestão de Clínicas de Psicologia.

## 1. Filosofia e Propósito Social
- **Apoio à Formação:** O software é uma iniciativa sem fins lucrativos. Toda decisão técnica deve visar a redução de custos operacionais para as clínicas.
- **Sustentabilidade por Rateio:** O sistema deve priorizar métricas de consumo precisas para viabilizar o modelo de divisão de custos de infraestrutura.

## 2. Organização da Documentação (Princípio da Verdade Única)
- `DOCS_BUSINESS.md`: **Regras de Negócio Narrativas.** Contém o "porquê" e a lógica dos processos. É a fonte primária de verdade.
- `DOCS_API.md`: Requisitos técnicos, padrões de segurança (JWT, 2FA, Criptografia AES-256) e especificações de integração.
- `DOCS_DB.md`: Modelagem de dados, dicionário e diagramas de persistência.
- `agentes.md`: Este guia de conduta e fluxos de trabalho.

## 3. Padrões de Desenvolvimento e Governança
- **Isolamento de Dados (Multi-tenancy):** É mandatório o uso de identificadores de clínica (`tenant_id`) em todas as transações. O vazamento de dados entre unidades é considerado uma falha crítica de segurança.
- **Arquitetura de Auditoria Integral:** Nenhum dado sensível deve ser acessado ou alterado sem a geração de um log imutável contendo: Usuário, Timestamp, IP, Ação e Justificativa (quando aplicável).
- **Persistência Jurídica (Soft Delete):** É proibida a exclusão física de registros. Deve-se utilizar a exclusão lógica para garantir a custódia dos dados por prazos legais (mínimo de 5 anos para prontuários).

## 4. Protocolos Éticos e de Segurança
- **Custódia do Prontuário:** O agente deve garantir que o acesso ao prontuário seja exclusivo do profissional responsável, exceto nos casos previstos nos **Procedimentos de Contingência** do `DOCS_BUSINESS.md`.
- **Validação de TCLE:** Funcionalidades críticas (compartilhamento/urgência) só devem ser implementadas mediante a verificação da existência do Termo de Consentimento assinado.
- **Imutabilidade Documental:** Após o prazo de retificação de 24 horas, o prontuário deve ser selado. Alterações posteriores devem ser tratadas como novos registros vinculados, nunca substituindo o original.

## 5. Inteligência Artificial Responsável
- **Filtro Ético:** Toda sugestão de marketing ou comunicação gerada por IA deve passar por um validador de conformidade com os códigos de ética profissional.
- **Transparência Preditiva:** Alertas de evasão ou análises de humor devem ser apresentados como sugestões de apoio à decisão, nunca substituindo o julgamento clínico do profissional.

## 6. Fluxo de Trabalho do Agente
1. **Pesquisa:** Validar se a funcionalidade solicitada está descrita no `DOCS_BUSINESS.md`.
2. **Documentação:** Antes de codificar, atualizar o `DOCS_API.md` e `DOCS_DB.md` com o desenho técnico.
3. **Implementação:** Seguir os padrões de Java 17+ e Spring Boot 3.
4. **Auditoria:** Garantir que a nova funcionalidade gere os logs de auditoria correspondentes conforme a seção 3 deste guia.

## 7. Comunicação e Transparência
- **Tom Profissional:** Evitar termos coloquiais na documentação técnica e no código.
- **Justificativa de Ação:** O agente deve sempre explicar a lógica por trás de alterações estruturais, garantindo que elas não comprometam a segurança ou a independência das clínicas.
