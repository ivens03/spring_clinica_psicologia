# Documentação do Banco de Dados

## 1. Princípios de Modelagem e Integridade
- **Normalização:** O banco de dados deve seguir rigorosamente a **Terceira Forma Normal (3NF)** para eliminar redundância e garantir a integridade dos dados, prevenindo qualquer perda acidental por anomalias de atualização ou exclusão.
- **Integridade Referencial:** Uso obrigatório de chaves estrangeiras (`FKs`) e restrições (`Constraints`) para manter a consistência entre as entidades.
- **Multi-tenancy:** Toda e qualquer tabela deve possuir a coluna `tenant_id` para isolamento lógico absoluto.
- **Audit Trails:** Implementação de tabelas de auditoria (Shadow Tables ou Tabelas de Log de Alteração) para registrar o estado anterior e posterior de cada registro sensível.
- **Soft Delete (Não-Exclusão Física):** Uso de campos `deleted_at` ou flags de inatividade para preservar o histórico jurídico e de rateio.

## 2. Diagrama ER (Entidade-Relacionamento)
*(Inserir diagrama aqui)*

## Tabelas

### `pacientes`
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT (PK) | Identificador único |
| nome | VARCHAR(255) | Nome completo |
| cpf | VARCHAR(11) | CPF (apenas números) |

### `agendamentos`
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT (PK) | Identificador único |
| paciente_id | BIGINT (FK) | Referência ao paciente |
| data_hora | TIMESTAMP | Data e hora da consulta |

## Migrations
O sistema utiliza [Flyway/Liquibase] para controle de versão do banco.
