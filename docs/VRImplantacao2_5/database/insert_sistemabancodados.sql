INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ACCESYS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ACOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ALPHASYS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'APOLLO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ARIUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'), 'PROREG', 'automa', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ARTSYSTEM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ASEFE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ASOFT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ATENAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ATHOS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'athos', 'j2mhw82dyu1kn5g4', null);   	 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ATMA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AUTOADM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AUTOCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AUTOSYSTEM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AVANCE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'infor', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AVISTARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'), 'sa', '@vs2008', 'Avistare');	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'BASE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ACCESS'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'BRAJANGESTORES'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));	 	

/**** C */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CADASTRAFACIL'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CEFAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CEREBRO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CERVANTES'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CFSOFTSIAECF'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));	 	
	 
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CGA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CISS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DB2'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CLICK'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CONTROLWARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CONTROLX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CPGESTOR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CPLUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CRONOS20'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'CUPERMAX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

/***** D */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DATASYNC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER')); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DELFI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD')); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DESTRO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD')); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DEVMASTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'devmaster', 'devmaster', null); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DIRECTOR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'), 'sa', '#1qwer0987', null);   	   

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DJSYSTEM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DLINK'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DSOFT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'DTCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

/***** E */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'EASYSAC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'EMPORIO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'EPTUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ESSYSTEM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ETICA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'EXODUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

/*** F */	 
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FABTECH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FACILITE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FENIX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FHONLINE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FLASH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FLATAN'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FORT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FUTURA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

/**** G */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'G10'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'G3'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GDI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GDOOR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GESTORPDV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GETWAY'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'), 'gws', 'gws', 'GWOLAP');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GIGATRON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GONDOLA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GR7'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GTECH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GUIASISTEMAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GZSISTEMAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'mestre', null);

/**** H */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'HERCULESINTCASH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'HIPCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'hpc00', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'HIPER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'HRTECH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

/**** I */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ICOMMERCE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INFOBRASIL'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INFOMAC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INOVA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INTELLICASH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'k', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INTELLICON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INTERAGE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INTERDATA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INVENTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'IQSISTEMAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ISERVER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', '750051', 'db_iserver');

/**** J */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'JACSYS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'JM2ONLINE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'JMASTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'JRF'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

/**** K */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'KAIROS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'KCMS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

/**** L */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LINCE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LINEAR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'adminlinear', '@2013linear', 'sglinx');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LINNER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LITECI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LOGICBOX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LOGTEC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LOGUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'INFORMIX'), 'informix', 'loooge', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LYNCIS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

/**** M */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MARKET'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MASTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MERCALITE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MILENIO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MOBILITY'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MOBNEPDV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MRS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'MSIINFOR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

/**** N */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'NATISISTEMAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'NCA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'post');

/**** O*/
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'OPEN'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ORION'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ORIONTECH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ORYON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ACCESS'), 'paralelo');

/*** P */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'PHIXA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'PLENOKW'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'PLENUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'POLIGON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'), 'sa', 'Pol!gon5oft', 'PADARIA');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'POMARES'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'PROTON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'PWGESTOR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'PWS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

/**** R */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RCNET'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RENSOFTWARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'REPLEIS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RESULTMAIS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'rmpostgres', 'rmbancodados');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RKSOFTWARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'Office25');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RMS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'), 'rms', 'rmsprd');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RMSAUTOMAHELP'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ROOTAC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RPINFO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

/**** S */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SAAC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SABTECH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SAEF'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SATECFE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SAV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SCEF'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SDINFORMATICA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SGMASTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SHI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIAC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIACRIARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'Hs8Tw13kPx7uDPs');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIFAT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIGMA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIIT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'JesusCristo');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIRCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SISMOURA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SNSISTEMA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SOFTCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SOFTTECH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SOLIDUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SOLIDUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SOLUTIONSUPERA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'online');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SOPHYX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SRI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'STI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'STSITEMAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SUPER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SUPERLOJA10'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SUPERSERVER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SUPERUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'), 'xe', 'smart');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYNCTEC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSAUT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSERP'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSMO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSMO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSPDV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));	 	 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSPDV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');	 	 

/**** T */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TECNOSOFT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TELECON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'), 'sa', 'a2m8x7h5', 'GESTAO');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TGA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TITECNOLOGIA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TOPSYSTEM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TPAROOTAC'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TSTI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'tsti', '1234', 'tsl');

/*** U */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'UNIPLUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'postgres');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'UPFORTI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));

/*** V */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VCASH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'DBF'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VIASOFT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VIGGO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VISUALCOMERCIO'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VISUALMIX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VRMASTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'VrPost@Server', 'vr');

/**** Z */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ZOOMBOX'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ZPF'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));
	 
/**** W */
INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'W2A'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ACCESS'), 'banco820318');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'WEBER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'WEBSAQ'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'WINNEXUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'WISASOFT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'WMSI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'));
