# Documentação do Banco de Dados

## 1. Princípios de Modelagem e Integridade
- **Normalização:** O banco de dados deve seguir rigorosamente a **Terceira Forma Normal (3NF)** para eliminar redundância e garantir a integridade dos dados, prevenindo qualquer perda acidental por anomalias de atualização ou exclusão.
- **Integridade Referencial:** Uso obrigatório de chaves estrangeiras (`FKs`) e restrições (`Constraints`) para manter a consistência entre as entidades.
- **Multi-tenancy:** Toda e qualquer tabela deve possuir a coluna `tenant_id` para isolamento lógico absoluto.
- **Audit Trails:** Implementação de tabelas de auditoria (Shadow Tables ou Tabelas de Log de Alteração) para registrar o estado anterior e posterior de cada registro sensível.
- **Soft Delete (Não-Exclusão Física):** Uso de campos `deleted_at` ou flags de inatividade para preservar o histórico jurídico e de rateio.

## 2. Diagrama ER (Entidade-Relacionamento)
*(Inserir diagrama aqui)*

## 3. Definição das Tabelas e Schemas (Modelagem 3NF)

### Colunas de Auditoria Padrão (Mandatório)
Toda tabela deve possuir estes metadados para conformidade ética e técnica:
- `criado_em`: TIMESTAMP (Default NOW())
- `atualizado_em`: TIMESTAMP
- `excluido_em`: TIMESTAMP (Para Soft Delete)
- `versao`: INTEGER (Para Optimistic Locking)

### Schema `clinicas` (Gestão de Tenants)
#### Tabela: `clinicas`
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| **identificador_fiscal** | VARCHAR(14) (**PK**) | CPF (11) ou CNPJ (14) |
| nome_exibicao | VARCHAR(255) | Nome da Clínica ou Nome Profissional |
| tipo_pessoa | ENUM | 'FISICA' ou 'JURIDICA' |
| registro_conselho_clinica | VARCHAR(50) | Registro opcional no conselho |
| **tipo_clinica** | ENUM | 'EMPRESA', 'ESCOLA', 'EMPRESA_ESCOLAR' |
| ativo | BOOLEAN | Status operacional |

### Schema `usuarios` (Identidade e Autenticação)
#### Tabela: `usuarios`
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| **cpf** | CHAR(11) (**PK**) | Identificador natural (11 dígitos) |
| nome | VARCHAR(255) | Nome completo |
| data_nascimento | DATE | Data de nascimento |
| email | VARCHAR(255) (Unique) | E-mail de acesso |
| **senha** | VARCHAR(255) | Hash Argon2id |
| **clinica_id** | VARCHAR(14) (FK) | Vínculo com `clinicas.clinicas` |
| perfil_root | ENUM | GESTOR_SISTEMA, GESTOR_CLINICA, etc. |
| ativo | BOOLEAN | Status do usuário |

### Schema `perfis` (Papéis e Especializações)
#### Tabela: `gestores`
- `usuario_cpf`: CHAR(11) (PK, FK -> `usuarios.usuarios`)
- `cargo`: VARCHAR(100)

#### Tabela: `funcionarios`
- `usuario_cpf`: CHAR(11) (PK, FK -> `usuarios.usuarios`)
- `sub_perfil`: ENUM ('PROFISSIONAL_SAUDE', 'SECRETARIA', 'ATENDENTE')
- `conselho_classe`: VARCHAR(50)
- `registro_conselho`: VARCHAR(50)

#### Tabela: `estagiarios`
- `usuario_cpf`: CHAR(11) (PK, FK -> `usuarios.usuarios`)
- `supervisor_cpf`: CHAR(11) (FK -> `perfis.funcionarios`)
- `instituicao_ensino`: VARCHAR(255)
- `periodo_atual`: INTEGER

### Schema `auditoria` (Rastreabilidade e Segurança)
#### Tabela: `acessos`
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | UUID (PK) | Identificador do log |
| usuario_cpf | CHAR(11) (FK) | Quem acessou |
| clinica_id | VARCHAR(14) (FK) | Em qual tenant |
| data_hora | TIMESTAMP | Momento do acesso |
| ip | VARCHAR(45) | Endereço IP (Suporta IPv6) |
| acao | VARCHAR(100) | Ex: 'LOGIN', 'LOGOUT', 'FALHA_LOGIN' |
| detalhes | TEXT | Detalhes técnicos (Navegador, etc) |

#### Tabela: `alteracoes`
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | UUID (PK) | Identificador do log |
| usuario_cpf | CHAR(11) (FK) | Quem alterou |
| clinica_id | VARCHAR(14) (FK) | Em qual tenant |
| tabela_nome | VARCHAR(100) | Nome da tabela alterada |
| registro_id | VARCHAR(100) | ID do registro alterado |
| valor_antigo | JSONB | Estado anterior do dado |
| valor_novo | JSONB | Novo estado do dado |
| data_hora | TIMESTAMP | Momento da alteração |

## 4. Migrations
O sistema utiliza **Liquibase** para controle de versão do banco.
As migrations são organizadas em arquivos XML localizados em `src/main/resources/db/changelog/`.
Cada migration deve garantir a criação do schema antes da criação da tabela.
