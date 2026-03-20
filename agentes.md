# Guia de Padrões, Diretrizes e Conduta dos Agentes

Este documento é a autoridade máxima de conduta para o desenvolvimento do sistema. Ele consolida a ética profissional da psicologia com a engenharia de software de alta segurança.

## 1. Filosofia e Propósito Social
- **Apoio à Formação:** O software é uma iniciativa sem fins lucrativos. Toda decisão técnica deve visar a redução de custos operacionais (rateio de infraestrutura).
- **Sustentabilidade:** Priorizar métricas de consumo precisas para viabilizar a divisão justa dos custos do servidor entre as clínicas.

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
