package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum EScriptLojaOrigemSistema {

    AVANCE(15, 8, "SELECT distinct id_loja id, fantasia descricao FROM adm_empresas_estab ORDER BY 1"),
    ARIUS(197, 9, "SELECT id ,id || '' - '' || descritivo || '' - '' || cnpj_cpf descricao FROM empresas ORDER BY id"),
    GATEWAYSISTEMAS(198, 5,
            "SELECT l.COD_EMPRESA AS id, "
            + "(l.COD_EMPRESA||'' - ''|| l.FANTASIA||'' - ''||l.CNPJ) AS descricao "
            + "FROM EMITENTE l ORDER BY 1"),
    SG(199, 11,
            "select "
            + "codfil99 id, "
            + "cgcfil99 || '' - '' || apelido99 descricao "
            + "from "
            + "cadfil"),
    HIPCOM(74, 8, "select "
            + "lojcod as id, "
            + "concat(lojcod,'' - '', lojfantas) descricao, "
            + "lojcnpj from hiploj order by 1"),
    LOGUS(100, 7, "select cdg_filial id, dcr_fantasia descricao from cadfil"),
    MRS(109, 11, "select loja id, nome_fantasia descricao from parametros order by loja"),
    SYGMA(200, 5,
            "select "
            + "1 as id, "
            + "val_parametro descricao "
            + "from tparametro t "
            + "where ordem_parametro = 1002"),
    UNIPLUS(179, 11,
            "select \n"
            + "	id,\n"
            + "	nome || '' - '' || cnpj as descricao\n"
            + "from \n"
            + "	filial"),
    AVISTARE(16, 13, "select distinct\n"
            + "	(select CfgValue from dbo.TB_CONFIG where CfgChave = ''CNPJ'') as id,\n"
            + " ((select CfgValue from dbo.TB_CONFIG where CfgChave = ''EmpresaRegistro'') + '' - '' + "
            + "(select CfgValue from dbo.TB_CONFIG where CfgChave = ''CNPJ'')) as descricao\n"
            + "from dbo.TB_CONFIG"),
    BOMSOFT(203, 5,
            "SELECT "
            + " ID_CFG id, "
            + " FANTASIA_CFG descricao "
            + "FROM CONFIG "
            + "ORDER BY 1"),
    MICROTAB(201, 5, "SELECT "
            + " 1 as id,"
            + " ''Loja_01'' descricao"
            + " FROM CONFIG"),
    MRC6(202, 13, "select"
            + " codigo as id, "
            + " nome as descricao,"
            + " cnpj "
            + " from "
            + " entidades"),
    ASSIST(204, 11, "select 1 id, ''LOJA 01'' descricao"),
    DATABYTE(205, 5, "SELECT codigo AS id, nome AS descricao FROM FILIAL"),
    DOBESCGA(206, 5, "select\n"
            + " ret000.\"Codigo\" as id,\n"
            + " ret000.\"Fantasia\" as descricao,\n"
            + " ret000.\"CNPJ\"\n"
            + " from ret000\n"
            + " order by ret000.\"Codigo\""),
    TENTACULO(207, 5,
            "SELECT "
            + "	EMP_CODIGO id, "
            + "	EMP_FANTASIA descricao "
            + "FROM "
            + "	EMPRESAS "
            + "ORDER BY 1"),
    CONSINCO(209, 9, "SELECT \n"
            + "	nroempresa id,\n"
            + "	nomereduzido  || '' - '' || nrocgc || '''' || digcgc descricao\n"
            + "FROM \n"
            + "	consinco.ge_empresa\n"
            + "WHERE \n"
            + "	status = ''A''"),
    FXSISTEMAS(208, 5, "SELECT "
            + "	e.ID, "
            + "	e.RAZAO_SOCIAL descricao "
            + "FROM "
            + "	EMPRESA e"),
    VERSATIL(210, 5, "SELECT\n"
            + "	COD_EMPRESA id,\n"
            + "	FANTASIA descricao\n"
            + "FROM\n"
            + "	EMPRESA"),
    VRMASTER(182, 11, "select\n"
            + "	l.id,\n"
            + "	l.descricao\n"
            + "from \n"
            + "	loja l \n"
            + "inner join fornecedor f on l.id_fornecedor = f.id \n"
            + "where l.id_situacaocadastro = 1\n"
            + "order by\n"
            + "	l.id"),
    MOBILITY(107, 5, "select id, s_nome_fantasia descricao from configuracoes"),
    SYSPDV(170, 5, "SELECT prpcod id, prpfan descricao FROM PROPRIO"),
    TOPSYSTEM(176, 8, "SELECT empresa id, Razao_Social descricao FROM cad_filial ORDER BY 1"),
    DSIC(211, 11, "select emp_id id, emp_nomefantasia descricao from empresa"),
    WEBSAQ(188, 11, "select \n"
            + "codestabelec id, \n"
            + "razaosocial descricao \n"
            + "from estabelecimento\n"
            + "order by codestabelec"),
    TSl(212, 8, "SELECT\n"
            + "	EMPCOD id,\n"
            + "	EMPNOM descricao\n"
            + "FROM\n"
            + "	tsc008a"),
    SATFACIL(213, 5, "SELECT ID_REGISTRO id, NOME_LOJA descricao FROM PARAMETROS"),
    WBA(214, 5, "SELECT CAST(CODIGO AS integer) AS id, NOME descricao FROM FILIAL ORDER BY 1"),
    CPGESTOR(21, 9, "SELECT \n"
            + "	LJ_ASSOCIACAO id,\n"
            + "	''SANTA FE'' descricao\n"
            + "FROM \n"
            + "	vw_exp_produtos_sta\n"
            + "WHERE \n"
            + "	rownum <= 1\n"
            + "UNION ALL \n"
            + "SELECT \n"
            + "	lj_associacao id,\n"
            + "	''ZUZU'' descricao\n"
            + "FROM \n"
            + "	vw_exp_produtos_zuzu\n"
            + "WHERE \n"
            + "	rownum <= 1\n"
            + "UNION ALL\n"
            + "SELECT \n"
            + "	lj_associacao id,\n"
            + "	''PANIFICADORA VOVO'' descricao\n"
            + "FROM \n"
            + "	vw_exp_produtos_panificadora\n"
            + "WHERE \n"
            + "	rownum <= 1"),
    LINEAR(196, 8, "SELECT emp_codigo AS id, emp_fantasia AS descricao FROM empresa ORDER BY 1"),
    STOCK(215, 11, "select empid as id, empnome as descricao from tbempresas;"),
    CMM(216, 13, "select \n"
            + "	f.CodFil id,\n"
            + "	f.CNPJ + '' - '' + f.Fantasia as descricao\n"
            + "from \n"
            + "	Filiais f"),
    GZPRODADOS(217, 8, "select IdEmpresa as id, RazaoSocial as descricao from empresa;"),
    PRISMAFLEX(218, 5, "SELECT EMPCODIGO id, EMPRAZAOS descricao FROM EMPRESAS ORDER BY 1"),
    JNP_MSUPER(219, 5, "SELECT\n"
            + "	SUP999 id,\n"
            + "	FANTASIA descricao,\n"
            + "	CNPJ cpfcnpj\n"
            + "FROM\n"
            + "	SUP999\n"
            + "ORDER BY 1"),
    ETRADE(220, 13, "SELECT codigo id, Fantasia descricao FROM Filial"),
    GESTORA(223, 13, "SELECT EMP_CODIGO id, EMP_NOME descricao from EMPRESA"),
    KCMS(92, 13, "select \n"
            + "	codloja id,\n"
            + "	fantasia descricao\n"
            + "from empresa"),
    GETWAY(68, 13, "select\n"
            + "	CODLOJA id,\n"
            + "	descricao\n"
            + "from\n"
            + "	LOJA\n"
            + "order by\n"
            + "	id"),
    GANSO(221, 5, "SELECT  CODIGO id, FANTASIA descricao FROM EMPRESA_FILIAL"),
    LCSISTEMAS(222, 8, "select id, fantasia descricao from empresa"),
    INOVA(81, 11, "select empresaid id, empresanomefantasia descricao from empresa"),
    ISERVER(78, 8, "select Codigo id, NomeFantasia descricao from tbl_loja"),
    SHI(147, 5, "select\n"
            + "    codigo id,\n"
            + "    razsoc descricao\n"
            + "from filial"),
    SIAC(149, 9, "select empresa_id id, fantasia descricao from empresas"),
    NEREUS(224, 11, "select id_emp id, fantasia descricao from cg_emp order by 1"),
    SCORPION(225, 5, "SELECT LOJA id, RAZAOSOCIAL descricao FROM TB_EMPRESA"),
    MEGASOFTWARE(226, 5, "SELECT cgc id, FANTASIA descricao FROM EMPRESA"),
    ORION_POSTGRES(227, 11, "select cgc id, firma descricao from config"),
    CEFAS(24, 9, "select codfilial id, nomefantasia descricao from filial"),
    ARAUTO(228, 5, "select id, nomefantasia descricao from empresa"),
    SUPERCONTROLE(229, 13, "select lj.id id, descricaoLoja + '' - '' + f.cnpj as descricao from MultiLoja.Loja lj join Cadastro.Entidade f on lj.fkCliente = f.id order by lj.id"),
    RPINFO(130, 11, "select unid_codigo id, unid_reduzido descricao from unidades order by 1"),
    FOCUS(230, 8, "select id, razao descricao from empresa"),
    MANAGER(231, 5, "select codigo id, fantasia descricao from empresa"),
    SCV(232, 5, "SELECT id, NOME_FANTASIA descricao FROM EMPRESAS where id = 1;"),
    SINC(233, 11, "select ncad_cgcocpf_2 id, ncad_fantasi_2 descricao from sincad where ncad_cgcocpf_2 = 44744589000108"), /*<-- CNPJ do cliente, alterar em novo projeto*/
    PLENUS(119, 5, "select id_empresa id, nome_fantasia descricao from empresa"),
    DX(234, 5, "select  c_codloja id, c_fantaloja descricao from empresa"),
    LIVRE(235, 5, "SELECT CODIGO id, EMPRESA descricao FROM EMPRESA"),
    GUIASISTEMAS(71, 13, "select vfd_CodFilial id, vfd_Descricao descricao from tab_filial order by vfd_CodFilial"),
    HIPER(75, 13, "select id_filial id, razao_social descricao from filial order by id_filial"),
    WINTHOR(236, 9, "select codigo id, concat(concat(CAST(codigo AS varchar(10)),'' - ''),coalesce(fantasia, razaosocial)) descricao FROM pcfilial ORDER BY codigo"),
    WLS(237, 5, "SELECT COD_LOJA id, NOME_FANTASIA || '' - '' || CNPJ descricao FROM INFORMACOES ORDER BY COD_LOJA"),
    GSOFT(238, 13, "select Codigo id, nome descricao from MC_Empresa"),
    FACIL(239, 5, "select EMPRESAS_ID id, EMPRESA_NOME descricao from EMPRESAS"),
    BRDATA(240, 13, "select  C021_Codigo id, C021_Codigo + ''-'' + C021_Descricao descricao from C021_Deposito"),
    ARIUSWEB(241, 8, "select nroloja id, razao descricao from controle.pf_loja"),
    DEVSIS(242,5, "SELECT REFERENCIAL id, NOME descricao FROM CON_EMPRESA"),
    PRIME(243,11, "select empr_codigo as id,  empr_nomereduzido as descricao from empresas order by 1"),
    GEP(246, 5, "SELECT CODIGO id, NOMEFANTASIA descricao FROM EMPRESA"),
    PALLAS(244,8, "select cod_cli id, nome_cli descricao from cliente where cod_cli = 1"),
    ALCANCE(245,8, "SELECT NumComanda id, Titulo descricao FROM ppcx.paramh"),
    SOFTLOG(247,5,"SELECT CODIGO id, FILIAL descricao FROM C000004"),
    MBD(248,5,"SELECT COUNT(*) id, RAZAO_SOCIAL descricao FROM FIRMA f GROUP BY RAZAO_SOCIAL"),
    FENIX(54,5,"select id_empresa as id, nm_fantasia as descricao from empresa order by 1"),
    FENIXME(249, 5, "SELECT CODIGOFILIAL id, CODIGOFILIAL|| ''-'' ||NOME AS descricao FROM FILIAL ORDER BY 1"),
    TARGET_G3(250, 13, "select cd_emp id, raz_soc descricao from empresa"),
    WISE(251, 5, "SELECT 1 id, FANTASIA descricao FROM NFE_EMPRESAS"),
    MARKET(103, 11, "select cd_loja id, cd_loja || '' - '' ||nm_loja descricao from cadastro.tb_loja order by 1"),
    GENERICO(252, 11, "vazio"),
    SISMASTER(253, 8, "select codigo id, concat(nomefantasia, '' '', cnpj) as descricao from tabdollar t"),
    ALTERDATAWSHOP(254,11,"select cdempresa id, nrcgc || ''-'' || nmempresa descricao from ishop.empshop order by cdempresa"),
    STI3(255, 8, "select codigo as id,razao as descricao from empresas"),
    ARGO(257, 13, "select codemp id, razemp descricao from empresa"),
    RMS(129, 9, "select loj_codigo||loj_digito as id, loj_codigo||'' - DIGITO ''||loj_digito as descricao from AA2CLOJA order by loj_codigo"),
    VISUALMIX(186, 13, "select codigo as id,	descricao from dbo.Lojas order by 1");

    private int idSistema;
    private int idBancoDados;
    private String scriptGetLojaOrigem;

    EScriptLojaOrigemSistema(int idSistema, int idBancoDados, String scriptGetLojaOrigem) {
        this.idSistema = idSistema;
        this.idBancoDados = idBancoDados;
        this.scriptGetLojaOrigem = scriptGetLojaOrigem;
    }

    public int getIdSistema() {
        return this.idSistema;
    }

    public int getIdBancoDados() {
        return this.idBancoDados;
    }

    public String getScriptGetLojaOrigem() {
        return this.scriptGetLojaOrigem;
    }

    public void setIdSistema(int idSistema) {
        this.idSistema = idSistema;
    }

    public void setIdBancoDados(int idBancoDados) {
        this.idBancoDados = idBancoDados;
    }

    public void setScriptGetLojaOrigem(String scriptGetLojaOrigem) {
        this.scriptGetLojaOrigem = scriptGetLojaOrigem;
    }
}
