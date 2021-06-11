INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%CGA%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'));

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%HIPCOM%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'));	 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%SYSPDV%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%SQL SERVER%'));	 	 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%VR%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%AVISTARE%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%SQL SERVER%'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%INTERAGE%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'));	 	

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%SYSPDV%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%RKSOFTWARE%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%RKSOFTWARE%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'Office25', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%LINEAR%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'adminlinear', '@2013linear', 'sglinx');


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%ISERVER%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'root', '750051', 'db_iserver');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%TSTI%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'tsti', '1234', 'tsl');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%SIRCOM%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%INTELICASH%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'masterkey', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%FUTURA%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%FIREBIRD%'), 'SYSDBA', 'sbofutura', 'Dados.fdb');

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%LOGUS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%INFORMIX%'), 'informix', 'loooge', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%SIIT%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'root', 'JesusCristo', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%AVANCE%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'root', 'infor', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%GZSISTEMAS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'root', 'mestre', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%SIACRIARE%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'root', 'Hs8Tw13kPx7uDPs', null);


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%HIPCOM%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%MYSQL%'), 'root', 'hpc00', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%ARIUS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%ORACLE%'), 'PROREG', 'automa', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%SUPERUS'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%ORACLE%'), 'xe', 'smart', null);

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%DEVMASTER'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'), 'devmaster', 'devmaster', null); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%BRAJAN%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'orple', null); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%NCA%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'post', null);  

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%UNIPLUS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'postgres', null);   


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%ATHOS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'), 'athos', 'j2mhw82dyu1kn5g4', null);   	 


INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%RESULTMAIS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%POSTGRESQL%'), 'postgres', 'rmpostgres', 'rmbancodados');   	  

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%DIRECTOR%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%SQL SERVER%'), 'sa', '#1qwer0987', null);   	   

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%ATENAS%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%SQL SERVER%'), 'sa', 'personal', 'nca_adm'); 

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%TELECON%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%SQL SERVER%'), 'sa', 'a2m8x7h5', 'GESTAO');   	     

INSERT INTO implantacao2_5.sistemabancodados (id_sistema, id_bancodados, usuario, senha, nomeschema)
VALUES ((SELECT id FROM implantacao2_5.sistema
	 WHERE nome LIKE '%POLIGON%'),
	 (SELECT id FROM implantacao2_5.bancodados
	 WHERE nome LIKE '%SQL SERVER%'), 'sa', 'Pol!gon5oft', 'PADARIA');   	     