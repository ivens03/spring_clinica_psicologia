--liquibase formatted sql

--changeset ivens magno da costa lisboa:001-create-schema-clinicas
CREATE SCHEMA IF NOT EXISTS clinicas;

--changeset ivens magno da costa lisboa:001-create-table-clinicas
CREATE TABLE IF NOT EXISTS clinicas.clinicas (
    identificador_fiscal VARCHAR(14) NOT NULL,
    nome_exibicao VARCHAR(255) NOT NULL,
    tipo_pessoa VARCHAR(20) NOT NULL,
    registro_conselho_clinica VARCHAR(50),
    tipo_clinica VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP,
    excluido_em TIMESTAMP,
    versao INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_clinicas PRIMARY KEY (identificador_fiscal)
);
