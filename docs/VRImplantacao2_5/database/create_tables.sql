CREATE SCHEMA IF NOT EXISTS implantacao2_5;

CREATE TABLE IF NOT EXISTS implantacao2_5.sistema(
	id serial PRIMARY KEY NOT NULL,
	nome VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS implantacao2_5.bancodados(
	id serial PRIMARY KEY NOT NULL,
	nome VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS implantacao2_5.sistemabancodados(
	id serial PRIMARY KEY NOT NULL,
	id_sistema INTEGER NOT NULL,
	id_bancodados INTEGER NOT NULL,
	nomeschema VARCHAR(60),
	usuario VARCHAR(30),
	senha VARCHAR(30),
	CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)
		REFERENCES implantacao2_5.sistema (id),
	CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)
		REFERENCES implantacao2_5.bancodados (id),
	CONSTRAINT un_sistema_bancodados 
		UNIQUE (id_sistema, id_bancodados)
);

CREATE TABLE IF NOT EXISTS implantacao2_5.conexao(
	id serial PRIMARY KEY NOT NULL,
	host VARCHAR(20) NOT NULL,
	porta INTEGER NOT NULL,
	usuario VARCHAR(30) NOT NULL,
	senha VARCHAR(30) NOT NULL,
	descricao VARCHAR (60),
	id_sistemabancodados INTEGER NOT NULL,
	CONSTRAINT fk_id_sistemabancodados FOREIGN KEY (id_sistemabancodados)
		REFERENCES implantacao2_5.sistemabancodados (id)
);

CREATE TABLE IF NOT EXISTS implantacao2_5.conexaoloja(
	id serial PRIMARY KEY NOT NULL,
	id_conexao INTEGER NOT NULL,
	id_lojaorigem VARCHAR NOT NULL,
	id_lojadestino INTEGER NOT NULL,
	datacadastro TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
	id_situacaomigracao INTEGER NOT NULL,
	lojamatriz BOOLEAN,
	CONSTRAINT fk_id_conexao FOREIGN KEY (id_conexao)
		REFERENCES implantacao2_5.conexao(id)
);

CREATE TABLE IF NOT EXISTS implantacao2_5.sistemabancodadosscripts(
	id serial PRIMARY KEY NOT NULL,
	id_sistema INTEGER NOT NULL,
	id_bancodados INTEGER NOT NULL,
	script_getlojas TEXT,
	CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)
		REFERENCES implantacao2_5.sistema (id),
	CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)
		REFERENCES implantacao2_5.bancodados (id),
	CONSTRAINT un_sistema_bancodados_scripts UNIQUE (id_sistema, id_bancodados)
);

/*ALTER TABLE implantacao2_5.sistemabancodados ADD CONSTRAINT un_sistema_bancodados UNIQUE (id_sistema, id_bancodados);
ALTER TABLE implantacao2_5.sistemabancodados ADD nomeschema VARCHAR(60);
ALTER TABLE implantacao2_5.sistemabancodados ADD usuario VARCHAR(30);
ALTER TABLE implantacao2_5.sistemabancodados ADD senha VARCHAR(30);*/