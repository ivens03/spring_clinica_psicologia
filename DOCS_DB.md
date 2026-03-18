# Documentação do Banco de Dados

## Diagrama ER (Entidade-Relacionamento)
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
