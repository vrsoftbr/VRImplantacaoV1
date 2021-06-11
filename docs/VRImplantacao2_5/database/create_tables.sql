CREATE TABLE implantacao2_5.sistema(
	id serial PRIMARY KEY NOT NULL,
	nome VARCHAR(60) NOT NULL
);

CREATE TABLE implantacao2_5.bancodados(
	id serial PRIMARY KEY NOT NULL,
	nome VARCHAR(60) NOT NULL
);

CREATE TABLE implantacao2_5.sistemabancodados(
	id serial PRIMARY KEY NOT NULL,
	id_sistema INTEGER NOT NULL,
	id_bancodados INTEGER NOT NULL,
	CONSTRAINT fk_id_sistema FOREIGN KEY (id_sistema)
		REFERENCES implantacao2_5.sistema (id),
	CONSTRAINT fk_id_bancodados FOREIGN KEY (id_bancodados)
		REFERENCES implantacao2_5.bancodados (id)
);

ALTER TABLE implantacao2_5.sistemabancodados ADD CONSTRAINT un_sistema_bancodados UNIQUE (id_sistema, id_bancodados);
ALTER TABLE implantacao2_5.sistemabancodados ADD nomeschema VARCHAR(60);
ALTER TABLE implantacao2_5.sistemabancodados ADD usuario VARCHAR(30);
ALTER TABLE implantacao2_5.sistemabancodados ADD senha VARCHAR(30);


ALTER TABLE implantacao2_5.bancodados DROP nomeschema;
ALTER TABLE implantacao2_5.bancodados DROP usuario;
ALTER TABLE implantacao2_5.bancodados DROP senha;


select * from implantacao2_5.sistema;
select * from implantacao2_5.bancodados;
select * from implantacao2_5.sistemabancodados;