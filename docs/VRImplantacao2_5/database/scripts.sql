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

select * from implantacao2_5.sistema;
select * from implantacao2_5.bancodados;