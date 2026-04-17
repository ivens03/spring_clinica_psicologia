--liquibase formatted sql

--changeset ivens magno da costa lisboa:004-0
CREATE SCHEMA IF NOT EXISTS autenticacao;

--changeset ivens magno da costa lisboa:004-1
CREATE TABLE IF NOT EXISTS autenticacao.refresh_tokens (
    id UUID NOT NULL,
    usuario_cpf CHAR(11) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    emitido_em TIMESTAMP NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    revogado_em TIMESTAMP,
    ip VARCHAR(45),
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_token_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf)
);

--changeset ivens magno da costa lisboa:004-2
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_usuario_cpf
    ON autenticacao.refresh_tokens(usuario_cpf);

--changeset ivens magno da costa lisboa:004-3
CREATE TABLE IF NOT EXISTS autenticacao.segundo_fator_desafios (
    id UUID NOT NULL,
    usuario_cpf CHAR(11) NOT NULL,
    codigo_hash VARCHAR(128) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    usado_em TIMESTAMP,
    ip VARCHAR(45),
    CONSTRAINT pk_segundo_fator_desafios PRIMARY KEY (id),
    CONSTRAINT fk_segundo_fator_usuario FOREIGN KEY (usuario_cpf) REFERENCES usuarios.usuarios(cpf)
);

--changeset ivens magno da costa lisboa:004-4
CREATE INDEX IF NOT EXISTS idx_segundo_fator_desafios_usuario_cpf
    ON autenticacao.segundo_fator_desafios(usuario_cpf);
