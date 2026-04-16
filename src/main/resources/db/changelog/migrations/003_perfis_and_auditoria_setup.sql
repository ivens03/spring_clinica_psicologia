--liquibase formatted sql

--changeset ivens magno da costa lisboa:003-0
CREATE SCHEMA IF NOT EXISTS perfis;

--changeset ivens magno da costa lisboa:003-1
CREATE SCHEMA IF NOT EXISTS auditoria;

--changeset ivens magno da costa lisboa:003-2
ALTER TABLE usuarios.usuarios
    ADD COLUMN IF NOT EXISTS data_nascimento DATE;

--changeset ivens magno da costa lisboa:003-3
CREATE TABLE IF NOT EXISTS perfis.gestores (
    usuario_cpf CHAR(11) NOT NULL,
    cargo VARCHAR(100),
    CONSTRAINT pk_gestores PRIMARY KEY (usuario_cpf),
    CONSTRAINT fk_gestor_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf)
);

--changeset ivens magno da costa lisboa:003-4
CREATE TABLE IF NOT EXISTS perfis.funcionarios (
    usuario_cpf CHAR(11) NOT NULL,
    sub_perfil VARCHAR(50) NOT NULL,
    conselho_classe VARCHAR(50),
    registro_conselho VARCHAR(50),
    CONSTRAINT pk_funcionarios PRIMARY KEY (usuario_cpf),
    CONSTRAINT fk_funcionario_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf)
);

--changeset ivens magno da costa lisboa:003-5
CREATE TABLE IF NOT EXISTS perfis.estagiarios (
    usuario_cpf CHAR(11) NOT NULL,
    supervisor_cpf CHAR(11),
    instituicao_ensino VARCHAR(255),
    periodo_atual INTEGER,
    CONSTRAINT pk_estagiarios PRIMARY KEY (usuario_cpf),
    CONSTRAINT fk_estagiario_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf),
    CONSTRAINT fk_estagiario_supervisor FOREIGN KEY (supervisor_cpf) REFERENCES perfis.funcionarios(usuario_cpf)
);

--changeset ivens magno da costa lisboa:003-6
CREATE TABLE IF NOT EXISTS auditoria.acessos (
    id UUID NOT NULL,
    usuario_cpf CHAR(11) NOT NULL,
    clinica_id VARCHAR(14) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    ip VARCHAR(45),
    acao VARCHAR(100),
    detalhes TEXT,
    CONSTRAINT pk_acessos PRIMARY KEY (id),
    CONSTRAINT fk_acesso_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf),
    CONSTRAINT fk_acesso_clinica FOREIGN KEY (clinica_id) REFERENCES clinicas.clinicas(identificador_fiscal)
);

--changeset ivens magno da costa lisboa:003-7
CREATE TABLE IF NOT EXISTS auditoria.alteracoes (
    id UUID NOT NULL,
    usuario_cpf CHAR(11) NOT NULL,
    clinica_id VARCHAR(14) NOT NULL,
    tabela_nome VARCHAR(100),
    registro_id VARCHAR(100),
    valor_antigo JSONB,
    valor_novo JSONB,
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT pk_alteracoes PRIMARY KEY (id),
    CONSTRAINT fk_alteracao_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf),
    CONSTRAINT fk_alteracao_clinica FOREIGN KEY (clinica_id) REFERENCES clinicas.clinicas(identificador_fiscal)
);
