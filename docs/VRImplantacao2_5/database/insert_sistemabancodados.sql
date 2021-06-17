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
	 WHERE nome = 'POSTGRES'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AVANCE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'infor', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'AVISTARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQLSERVER'));	 	

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
	 WHERE nome = 'SQL SERVER'), 'sa', '#1qwer0987', null);   	   

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





INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'HIPCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'));	 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSPDV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQL SERVER'));	 	 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'VR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'));	 	


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INTERAGE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SYSPDV'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RKSOFTWARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RKSOFTWARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'Office25', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LINEAR'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'adminlinear', '@2013linear', 'sglinx');


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'ISERVER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', '750051', 'db_iserver');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TSTI'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'tsti', '1234', 'tsl');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIRCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'INTELICASH'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'masterkey', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'FUTURA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'FIREBIRD'), 'SYSDBA', 'sbofutura', 'Dados.fdb');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'LOGUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'INFORMIX'), 'informix', 'loooge', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIIT'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'JesusCristo', null);




INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'GZSISTEMAS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'mestre', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SIACRIARE'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'Hs8Tw13kPx7uDPs', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'HIPCOM'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'MYSQL'), 'root', 'hpc00', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'SUPERUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'ORACLE'), 'xe', 'smart', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'NCA'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'post', null);  

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'UNIPLUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'postgres', null);   




INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'RESULTMAIS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'POSTGRESQL'), 'postgres', 'rmpostgres', 'rmbancodados');   	  


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'TELECON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQL SERVER'), 'sa', 'a2m8x7h5', 'GESTAO');   	     

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome = 'POLIGON'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome = 'SQL SERVER'), 'sa', 'Pol!gon5oft', 'PADARIA');   	     