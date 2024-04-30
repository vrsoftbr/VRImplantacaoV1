/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

/**
 *
 * @Lucas
 */
public enum ESistemaBancoDados {

//ACCESS    
    ACCESS_BASE(17, "BASE", 1, "ACCESS", "", "", "", 0, ""),
    ACCESS_ORYON(116, "ORYON", 1, "ACCESS", "", "", "paralelo", 0, ""),
    ACCESS_W2A(187, "W2A", 1, "ACCESS", "", "", "banco820318", 0, ""),
//DB2/DBF    
    DB2_CISS(28, "CISS", 6, "DB2", "", "", "", 0, ""),
    DBF_DJSYSTEM(35, "DJSYSTEM", 4, "DBF", "", "", "", 0, ""),
    DBF_DTCOM(43, "DTCOM", 4, "DBF", "", "", "", 0, ""),
    DBF_ESSYSTEM(47, "ESSYSTEM", 4, "DBF", "", "", "", 0, ""),
    DBF_JACSYS(89, "JACSYS", 4, "DBF", "", "", "", 0, ""),
    DBF_MSIINFOR(102, "MSIINFOR", 4, "DBF", "", "", "", 0, ""),
    DBF_ORION(114, "ORION", 4, "DBF", "", "", "", 0, ""),
    DBF_ROOTAC(134, "ROOTAC", 4, "DBF", "", "", "", 0, ""),
    DBF_VCASH(181, "VCASH", 4, "DBF", "", "", "", 0, ""),
//FIREBIRD    
    FIREBIRD_ASOFT(1, "ASOFT", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_ALPHASYS(4, "ALPHASYS", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_ATENAS(8, "ATENAS", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_AUTOADM(11, "AUTOADM", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_AUTOCOM(13, "AUTOCOM", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_CFSOFTSIAECF(20, "CFSOFTSIAECF", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_CPLUS(22, "CPLUS", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_CADASTRAFACIL(23, "CADASTRAFACIL", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_CEREBRO(25, "CEREBRO", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_CGA(27, "CGA", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_DSOFT(37, "DSOFT", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_DELFI(39, "DELFI", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_DESTRO(40, "DESTRO", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_ETICA(48, "ETICA", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_FACILITE(52, "FACILITE", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_FENIX(54, "FENIX", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_FLASH(55, "FLASH", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_FORT(57, "FORT", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_FUTURA(58, "FUTURA", 5, "FIREBIRD", "SYSDBA", "masterkey", "", 3050, ""),
    FIREBIRD_GCOM(61, "GCOM", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_GDI(62, "GDI", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_GDOOR(63, "GDOOR", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_GIGATRON(69, "GIGATRON", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_INFOBRASIL(79, "INFOBRASIL", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_INTELLICASH(82, "INTELLICASH", 5, "FIREBIRD", "", "SYSDBA", "k", 3050, ""),
    FIREBIRD_INTELLICON(83, "INTELLICON", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_INTERDATA(84, "INTERDATA", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_INTERAGE(85, "INTERAGE", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_LITECI(97, "LITECI", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_LOGICBOX(99, "LOGICBOX", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_MASTER(104, "MASTER", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_MERCALITE(105, "MERCALITE", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_MOBILITY(107, "MOBILITY", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_ORIONTECH(115, "ORIONTECH", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_PLENUS(119, "PLENUS", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_MODELO(122, "MODELO", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_PWGESTOR(124, "PWGESTOR", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_PWS(125, "PWS", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_RKSOFTWARE(127, "RKSOFTWARE", 5, "FIREBIRD", "", "SYSDBA", "Office25", 3050, ""),
    FIREBIRD_REPLEIS(132, "REPLEIS", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SDINFORMATICA(135, "SDINFORMATICA", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SGMASTER(136, "SGMASTER", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SAAC(140, "SAAC", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SCEF(146, "SCEF", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SHI(147, "SHI", 5, "FIREBIRD", "SCO.FDB", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SIGMA(151, "SIGMA", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SIRCOM(153, "SIRCOM", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SOLIDUS(159, "SOLIDUS", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SOLUTIONSUPERA(160, "SOLUTIONSUPERA", 5, "FIREBIRD", "", "SYSDBA", "online", 3050, ""),
    FIREBIRD_SOPHYX(161, "SOPHYX", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SRI(162, "SRI", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SUPER(164, "SUPER", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SYNCTEC(167, "SYNCTEC", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_SYSAUT(168, "SYSAUT", 5, "FIREBIRD", "bank", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SYSPDV(170, "SYSPDV", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SYSMO(171, "SYSMO", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_TGA(172, "TGA", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_TECNOSOFT(173, "TECNOSOFT", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_UPFORTI(180, "UPFORTI", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_WEBER(189, "WEBER", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_WISASOFT(191, "WISASOFT", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_ZPF(195, "ZPF", 5, "FIREBIRD", "", "", "", 3050, ""),
    FIREBIRD_GATEWAYSISTEMAS(198, "GATEWAY SISTEMAS", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SYGMA(200, "SYGMA", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_MICROTAB(201, "MICROTAB", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_BOMSOFT(203, "BOMSOFT", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_DATABYTE(205, "DATABYTE", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_DOBESCGA(206, "DOBESCGA", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_TENTACULO(207, "TENTACULO", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_FXSISTEMAS(208, "FXSISTEMAS", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_VERSATIL(210, "VERSATIL", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SATFACIL(213, "SATFACIL", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_WBA(214, "WBA", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_PRISMAFLEX(218, "PRISMAFLEX", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_JNP_MSUPER(219, "JNP-MSUPER", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_GANSO(221, "GANSO", 5, "FIREBIRD", "", "SYSDBA", "1652498327", 3050, ""),
    FIREBIRD_SCORPION(225, "SCORPION", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_MEGASOFTWARE(226, "MEGA SOFTWARE", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_ARAUTO(228, "ARAUTO", 5, "FIREBIRD", "C:\\Gensis\\MARKET.GDB", "SYSDBA", "1234", 3050, ""),
    FIREBIRD_MANAGER(231, "MANAGER", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SCV(232, "SCV", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_DX(234, "DX", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_LIVRE(235, "LIVRE", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_WLS(237, "WLS", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_FACIL(239, "FACIL", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_DEVSIS(242, "DEVSIS", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_GEP(246, "GEP", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_SOFTLOG(247, "SOFTLOG", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_MBD(248, "MBD", 5, "FIREBIRD", "C:\\MBD\\DB\\ARCOIRIS.FDB", "sysdba", "sysdbambd", 3050, ""),
    FIREBIRD_FENIXME(249, "FENIXME", 5, "FIREBIRD", "", "SYSDBA", "23248290FENIX", 3050, ""),
    FIREBIRD_WISE(251, "WISE", 5, "FIREBIRD", "C:\\\\Dados\\\\Rede\\\\SUPERMERCADO.FDB", "sysdba", "masterkey", 3050, ""),
    FIREBIRD_GENERICO(252, "GENERICO", 5, "FIREBIRD", "postgres", "postgres", "VrPost@Server", 8745, ""),
    FIREBIRD_CENTER_INFORMATICA(256, "CENTER_INFORMATICA", 5, "FIREBIRD", "", "SYSDBA", "masterkey", 3050, ""),
    FIREBIRD_VIVASISTEMAS(258, "VIVASISTEMAS", 5, "FIREBIRD", "C:\\VIVA SOFTWARE\\appdata\\WMDADOS.EMM", "sysdba", "masterkey", 3050, ""),
    FIREBIRD_RESULTHBUSINESS(259, "RESULTHBUSINESS", 5, "FIREBIRD", "", "sysdba", "masterkey", 3050, ""),
    FIREBIRD_ECO_CENTAURO(193, "ECO_CENTAURO", 5, "FIREBIRD", "", "sysdba", "masterkey", 3050, ""),
    FIREBIRD_LJSISTEMAS_SIG(260, "LJSISTEMAS_SIG", 5, "FIREBIRD", "", "sysdba", "masterkey", 3050, ""),
//INFORMIX
    INFORMIX_LOGUS(100, "LOGUS", 7, "INFORMIX", "bd_nomecliente_m", "informix", "loooge", 9088, "Senha no arquivo password.txt no dir C:\\logus"),
//MYSQL
    MYSQL_AVANCE(15, "AVANCE", 8, "MYSQL", "", "root", "infor", 3006, ""),
    MYSQL_CLICK(29, "CLICK", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_DLINK(36, "DLINK", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_EMPORIO(45, "EMPORIO", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_EPTUS(46, "EPTUS", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_EXODUS(49, "EXODUS", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_G3(60, "G3", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_GR7(64, "GR7", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_GTECH(65, "GTECH", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_GZSISTEMAS(66, "GZSISTEMAS", 8, "MYSQL", "", "root", "mestre", 3006, ""),
    MYSQL_GESTORPDV(67, "GESTORPDV", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_HIPCOM(74, "HIPCOM", 8, "MYSQL", "", "root", "hpc00", 3006, ""),
    MYSQL_IQSISTEMAS(77, "IQSISTEMAS", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_ISERVER(78, "ISERVER", 8, "MYSQL", "db_iserver", "root", "750051", 3006, ""),
    MYSQL_MOBNEPDV(108, "MOBNEPDV", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_OPEN(113, "OPEN", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_PLENOKW(118, "PLENOKW", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_RCNET(126, "RCNET", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_STI(138, "STI", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_SATECFE(144, "SATECFE", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_SIACRIARE(148, "SIACRIARE", 8, "MYSQL", "", "root", "Hs8Tw13kPx7uDPs", 3006, ""),
    MYSQL_SIFAT(150, "SIFAT", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_SIIT(152, "SIIT", 8, "MYSQL", "", "root", "JesusCristo", 3006, ""),
    MYSQL_SUPERLOJA10(165, "SUPERLOJA10", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_TITECNOLOGIA(175, "TITECNOLOGIA", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_TOPSYSTEM(176, "TOPSYSTEM", 8, "MYSQL", "", "", "", 3006, ""),
    MYSQL_TSTI(178, "TSTI", 8, "MYSQL", "tsl", "tsti", "1234", 3006, "Dados de conexão no arquivo: \"C:\\sistemas\\tsl\\PARAMETRO.DBF\""),
    MYSQL_LINEAR(196, "LINEAR", 8, "MYSQL", "sglinx", "adminlinear", "@2013linear", 3006, ""),
    MYSQL_TSL(212, "TSL", 8, "MYSQL", "tsl", "tsti", "1234", 3306, ""),
    MYSQL_GZPRODADOS(217, "GZPRODADOS", 8, "MYSQL", "", "root", "mestre", 3006, ""),
    MYSQL_LCSISTEMAS(222, "LCSISTEMAS", 8, "MYSQL", "lc_sistemas", "root", "123456", 3306, ""),
    MYSQL_FOCUS(230, "FOCUS", 8, "MYSQL", "aut_db_me", "root", "root", 3306, ""),
    MYSQL_ARIUSWEB(241, "ARIUSWEB", 8, "MYSQL", "retag", "consulta", "123456", 3306, ""),
    MYSQL_PALLAS(244, "PALLAS", 8, "MYSQL", "", "", "", 3306, ""),
    MYSQL_ALCANCE(245, "ALCANCE", 8, "MYSQL", "pprt", "root", "alcan2143", 3306, ""),
    MYSQL_SISMASTER(253, "SISMASTER", 8, "MYSQL", "sismaster", "suporte", "sismaster123", 3306, ""),
    MYSQL_STI3(255, "STI3", 8, "MYSQL", "sti3database", "root", "", 3306, ""),
    MYSQL_EMPRESOFT(263,"EMPRESOFT",8,"MYSQL","empresoft","root","575757",3306,""),
    MYSQL_CONNEXONE(264,"CONNEXONE",8,"MYSQL","connexone","root","",3306,""),
//ORACLE
    ORACLE_APOLLO(5, "APOLLO", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_CEFAS(24, "CEFAS", 9, "ORACLE", "ORCL", "rb", "avemaria", 1521, ""),
    ORACLE_CPGESTOR(21, "CPGESTOR", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_CUPERMAX(34, "CUPERMAX", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_GONDOLA(70, "GONDOLA", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_INFOMAC(80, "INFOMAC", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_LINNER(96, "LINNER", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_PROTON(123, "PROTON", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_RMS(129, "RMS", 9, "ORACLE", "", "rms", "rmsprd", 1521, ""),
    ORACLE_SIAC(149, "SIAC", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_SOLIDUS(159, "SOLIDUS", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_SUPERUS(166, "SUPERUS", 9, "ORACLE", "", "xe", "smart", 1521, ""),
    ORACLE_VIASOFT(183, "VIASOFT", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_WMSI(192, "WMSI", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_ARIUS(197, "ARIUS", 9, "ORACLE", "", "PROREG", "automa", 1521, ""),
    ORACLE_CONSINCO(209, "CONSINCO", 9, "ORACLE", "", "", "", 1521, ""),
    ORACLE_WINTHOR(236, "WINTHOR", 9, "ORACLE", "", "", "", 1521, ""),
//POSTGRESQL
    POSTGRESQL_ATHOS(9, "ATHOS", 11, "POSTGRESQL", "", "athos", "j2mhw82dyu1kn5g4", 5432, ""),
    POSTGRESQL_AUTOSYSTEM(12, "AUTOSYSTEM", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_BRAJANGESTORES(19, "BRAJANGESTORES", 11, "POSTGRESQL", "", "", "orple", 5432, ""),
    POSTGRESQL_CERVANTES(26, "CERVANTES", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_CONTROLWARE(31, "CONTROLWARE", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_CRONOS20(33, "CRONOS20", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_DEVMASTER(41, "DEVMASTER", 11, "POSTGRESQL", "", "devmaster", "devmaster", 5432, ""),
    POSTGRESQL_FLATAN(56, "FLATAN", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_G10(59, "G10", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_INOVA(81, "INOVA", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_JRF(90, "JRF", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_LOGTEC(98, "LOGTEC", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_LYNCIS(101, "LYNCIS", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_MARKET(103, "MARKET", 11, "POSTGRESQL", "viza", "postgres", "hm1722+#()", 5432, ""),
    POSTGRESQL_MRS(109, "MRS", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_NCA(112, "NCA", 11, "POSTGRESQL", "", "postgres", "post", 5432, ""),
    POSTGRESQL_RMSAUTOMAHELP(128, "RMSAUTOMAHELP", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_RPINFO(130, "RPINFO", 11, "POSTGRESQL", "erp", "postgres", "s3gr3d0", 5432, ""),
    POSTGRESQL_RESULTMAIS(133, "RESULTMAIS", 11, "POSTGRESQL", "rmbancodados", "postgres", "rmpostgres", 5432, "Dados de conexão no arquivo: \"C:\\ResultMais\\PDV\\config.serializada\"\n" +
"Buscar pelo nome do banco que é normalmente \"rmbancodados\"."),
    POSTGRESQL_SOFTTECH(157, "SOFTTECH", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_SYSMO(171, "SYSMO", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_UNIPLUS(179, "UNIPLUS", 11, "POSTGRESQL", "", "postgres", "postgres", 5432, ""),
    POSTGRESQL_VRMASTER(182, "VRMASTER", 11, "POSTGRESQL", "vr", "postgres", "VrPost@Server", 8745, ""),
    POSTGRESQL_VIGGO(184, "VIGGO", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_WEBSAC(188, "WEBSAC", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_ZOOMBOX(194, "ZOOMBOX", 11, "POSTGRESQL", "", "", "", 5432, ""),
    POSTGRESQL_SG(199, "SG", 11, "POSTGRESQL", "cardoso_dbf", "postgres", "VrPost@Server", 5432, ""),
    POSTGRESQL_ASSIST(204, "ASSIST", 11, "POSTGRESQL", "avenida_dbf", "postgres", "VrPost@Server", 5432, ""),
    POSTGRESQL_DSIC(211, "DSIC", 11, "POSTGRESQL", "DSIC_SIAGNE_Professional", "postgres", "postgres", 5432, ""),
    POSTGRESQL_STOCK(215, "STOCK", 11, "POSTGRESQL", "orion", "postgres", "VrPost@Server", 8745, ""),
    POSTGRESQL_NEREUS(224, "NEREUS", 11, "POSTGRESQL", "", "ansg", "atma123@#$", 5432, ""),
    POSTGRESQL_ORION(227, "ORION_POSTGRES", 11, "POSTGRESQL", "cli_", "postgres", "VrPost@Server", 8745, ""),
    POSTGRESQL_SINC(233, "SINC", 11, "POSTGRESQL", "sincprod", "postgres", "postgres", 5432, ""),
    POSTGRESQL_PRIME(243, "PRIME", 11, "POSTGRESQL", "", "postgres", "post", 5432, ""),
    POSTGRESQL_GENERICO(252, "GENERICO", 11, "POSTGRESQL", "postgres", "postgres", "VrPost@Server", 8745, ""),
    POSTGRESQL_ARPA(262,"ARPA",11, "POSTGRESQL", "postgres","postgres","postgres",5432,""),
    POSTGRESQL_ALTERDATAWSHOP(254, "ALTERDATAWSHOP", 11, "POSTGRESQL", "ALTERDATA_SHOP", "postgres", "#abc123#", 5432, ""),
//SQLSERVER
    SQLSERVER_ACCESYS(2, "ACCESYS", 13, "SQLSERVER", "", "sa", "@66E$Y$", 1433, ""),
    SQLSERVER_ACOM(3, "ACOM", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_ARTSYSTEM(6, "ARTSYSTEM", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_ASEFE(7, "ASEFE", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_ATHOS(266, "ATHOS_SQLSERVER",13,"SQLSERVER","adm","","",1433,""),
    SQLSERVER_ATMA(10, "ATMA", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_AVISTARE(16, "AVISTARE", 13, "SQLSERVER", "Avistare", "sa", "@vs2008", 1433, ""),
    SQLSERVER_CONTROLX(32, "CONTROLX", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_DATASYNC(38, "DATASYNC", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_DIRECTOR(42, "DIRECTOR", 13, "SQLSERVER", "", "sa", "#1qwer0987", 1433, ""),
    SQLSERVER_EASYSAC(44, "EASYSAC", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_FHONLINE(50, "FHONLINE", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_FABTECH(51, "FABTECH", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_GETWAY(68, "GETWAY", 13, "SQLSERVER", "GWOLAP", "sa", "Gwsql2008", 1433, ""),
    SQLSERVER_GUIASISTEMAS(71, "GUIASISTEMAS", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_HRTECH(72, "HRTECH", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_HERCULESINTCASH(73, "HERCULESINTCASH", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_HIPER(75, "HIPER", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_ICOMMERCE(76, "ICOMMERCE", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_INVENTER(86, "INVENTER", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_JM2ONLINE(87, "JM2ONLINE", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_JMASTER(88, "JMASTER", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_KAIROS(91, "KAIROS", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_KCMS(92, "KCMS", 13, "SQLSERVER", "DBSOLUS", "sa", "kcms2011", 1433, ""),
    SQLSERVER_LINCE(95, "LINCE", 13, "SQLSERVER", "master", "Web", "932239w", 1433, "usuarios e senha podem variar: sa - PLUSECF; admin - SYSTEM; retaguardaPlus - d@t@b@5e"),
    SQLSERVER_MILENIO(106, "MILENIO", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_NATISISTEMAS(111, "NATISISTEMAS", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_PHIXA(117, "PHIXA", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_POLIGON(120, "POLIGON", 13, "SQLSERVER", "PADARIA", "sa", "Pol!gon5oft", 1433, ""),
    SQLSERVER_POMARES(121, "POMARES", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_RENSOFTWARE(131, "RENSOFTWARE", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_STSITEMAS(139, "STSITEMAS", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SABTECH(141, "SABTECH", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SAEF(142, "SAEF", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SAV(145, "SAV", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SISMOURA(154, "SISMOURA", 13, "SQLSERVER", "CLIENTE", "sa", "epilef", 1433, ""),
    SQLSERVER_SNSISTEMA(155, "SNSISTEMA", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SOFTCOM(156, "SOFTCOM", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SUPERSERVER(163, "SUPERSERVER", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SYSERP(169, "SYSERP", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SYSPDV(170, "SYSPDV", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_TELECON(174, "TELECON", 13, "SQLSERVER", "GESTAO", "sa", "a2m8x7h5", 1433, ""),
    SQLSERVER_TPAROOTAC(177, "TPAROOTAC", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_VISUALCOMERCIO(185, "VISUALCOMERCIO", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_VISUALMIX(186, "VISUALMIX", 13, "SQLSERVER", "Digger", "sa", "", 1433, ""),
    SQLSERVER_WINNEXUS(190, "WINNEXUS", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_MRC6(202, "MRC6", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_CMM(216, "CMM", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_ETRADE(220, "ETRADE", 13, "SQLSERVER", "ETrade", "dba", "master1", 1433, ""),
    SQLSERVER_GESTORA(223, "GESTORA", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_SUPERCONTROLE(229, "SUPERCONTROLE", 13, "SQLSERVER", "sc2010", "sc2010", "P0sa4P0s0", 1433, ""),
    SQLSERVER_GSOFT(238, "GSOFT", 13, "SQLSERVER", "GCOM", "sa", "@Gsbrasil", 1433, ""),
    SQLSERVER_BRDATA(240, "BRDATA", 13, "SQLSERVER", "", "", "", 1433, ""),
    SQLSERVER_TARGET_G3(250, "TARGET-G3", 13, "SQLSERVER", "MOINHO", "sa", "BqMQPcI2IHkyaVt9Arvd", 1433, ""),
    SQLSERVER_ARGO(257, "ARGO", 13, "SQLSERVER", "argo", "sa", "1BC27CA04E2C2C1204", 1433, ""),
    SQLSERVER_SAURUSPDV(261, "SAURUSPDV", 13, "SQLSERVER", "", "sa", "", 1433, ""),
    SQLSERVER_SANTSYSTEM(265, "SANTSYSTEM", 13, "SQLSERVER", "dadosretorno", "sa", "sant", 1433, ""),
    SQLSERVER_IDEALSOFT(267, "IDEALSOFT", 13, "SQLSERVER", "S9_Real", "sa", "Servidor123", 3414, "");

    private int idSistema;
    private String nomeSistema;
    private int idBancodados;
    private String nomeBancoDados;
    private String nomeSchema;
    private String usuario;
    private String senha;
    private int porta;
    private String observacao;
    
    private ESistemaBancoDados(int idSistema, String nomeSistema, 
                               int idBancoDados, String nomeBancoDados,
                               String nomeSchema, String usuario,
                               String senha, int porta, String observacao) {
        
        this.idSistema = idSistema;
        this.nomeSistema = nomeSistema;
        this.idBancodados = idBancoDados;
        this.nomeBancoDados = nomeBancoDados;
        this.nomeSchema = nomeSchema;
        this.usuario = usuario;
        this.senha = senha;
        this.porta = porta;
        this.observacao = observacao;
    }
    
    public int getIdSistema() {
        return this.idSistema;
    }
    
    public String getNomeSistema() {
        return this.nomeSistema;
    }
    
    public int getIdBancoDados() {
        return this.idBancodados;
    }
    
    public String getNomeBancoDados() {
        return this.nomeBancoDados;
    }
    
    public String getNomeSchema() {
        return this.nomeSchema;
    }
    
    public String getUsuario() {
        return this.usuario;
    }
    
    public String getSenha() {
        return this.senha;
    }
    
    public int getPorta() {
        return this.porta;
    }
    
    public void setIdSistema(int idSistema) {
        this.idSistema = idSistema;
    }
    
    public void setNomeSistema(String nomeSistema) {
        this.nomeSistema = nomeSistema;
    }
    
    public void setIdBancoDados(int idBancoDados) {
        this.idBancodados = idBancoDados;
    }
    
    public void setNomeBancoDados(String nomeBancoDados) {
        this.nomeBancoDados = nomeBancoDados;
    }
    
    public void setNomeSchema(String nomeSchema) {
        this.nomeSchema = nomeSchema;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}