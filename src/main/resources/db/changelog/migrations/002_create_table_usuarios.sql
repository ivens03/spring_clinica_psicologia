--liquibase formatted sql

--changeset ivens magno da costa lisboa:002-0
CREATE SCHEMA IF NOT EXISTS usuarios;

--changeset ivens magno da costa lisboa:002-1
CREATE TABLE IF NOT EXISTS usuarios.usuarios (
    cpf CHAR(11) NOT NULL,
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil_root VARCHAR(50) NOT NULL,
    clinica_id VARCHAR(14) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP,
    excluido_em TIMESTAMP,
    versao INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_usuarios PRIMARY KEY (cpf),
    CONSTRAINT fk_usuario_clinica FOREIGN KEY (clinica_id) REFERENCES clinicas.clinicas(identificador_fiscal)
);

--changeset ivens magno da costa lisboa:002-2
CREATE INDEX idx_usuario_email ON usuarios.usuarios(email);
CREATE INDEX idx_usuario_clinica ON usuarios.usuarios(clinica_id);
