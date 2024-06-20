package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum EScriptLojaOrigemSistema {

    ASOFT(1, 5, null),
    ACCESYS(2, 13, "select\n" +
                    "	COD_EMPRESA id,\n" +
                    "	NOMEFANTASIA descricao\n" +
                    "from\n" +
                    "	CONTROLE_CLIENTES.dbo.CC_EMPRESA"),
    ACOM(3, 13, "select Fil_cod id,Fil_nome descricao from filiais order by Fil_cod"),
    ALPHASYS(4, 5, "SELECT COD_EMPRESA AS id, RAZAO AS descricao  FROM EMPRESA e WHERE COD_FILIAL =1"),
    APOLLO(5, 9, "SELECT codempresa as id, fantasia as descricao FROM empresas order by codempresa"),
    ARTSYSTEM(6, 13, "select LOJCCODLOJ as id, LOJCEMLASU as descricao from dbo.ASENTLOJ"),
    ASEFE(7, 13, null),
    ATENAS(8, 5, "select codigo AS id , fantasia AS descricao from c999999"),
    ATHOS(9, 11, null),
    ATMA(10, 13, "select ID_EMP as id,FANTASIA as descricao from dbo.CG_EMP order by ID_EMP"),
    AUTOADM(11, 5, "SELECT l.nr_loja as id, p.nm_fantasia as descricao from tb_loja l join tb_pessoa p on p.cd_pessoa = l.cd_pessoa_loja order by l.nr_loja"),
    AUTOSYSTEM(12, 11, null),
    AUTOCOM(13, 5, "select codigo AS id, nome AS descricao from empresa order by 1"),
    AVANCE(15, 8, "SELECT distinct id_loja id, fantasia descricao FROM adm_empresas_estab ORDER BY 1"),
    AVISTARE(16, 13, "select distinct\n"
            + "	(select CfgValue from dbo.TB_CONFIG where CfgChave = ''CNPJ'') as id,\n"
            + " ((select CfgValue from dbo.TB_CONFIG where CfgChave = ''EmpresaRegistro'') + '' - '' + "
            + "(select CfgValue from dbo.TB_CONFIG where CfgChave = ''CNPJ'')) as descricao\n"
            + "from dbo.TB_CONFIG"),
    BRAJANGESTORES(19, 11, "select cod_filial as id, fantasia as descricao  from cad_filiais"),
    CFSOFTSIAECF(20, 5, null),
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
    CPLUS(22, 5, "SELECT e.codempresa AS id, e.nomeempresa AS descricao from empresa e"),
    CADASTRAFACIL(23, 5, "select lpad(id_empresa, 4, ''0'') as id, nome_razao AS descricao FROM empresa"),
    CEFAS(24, 9, "select codfilial id, nomefantasia descricao from filial"),
    EASYSAC(44, 13, "select cdloja id, fantas descricao from sac999"),
    FENIX(54, 5, "select id_empresa as id, CASE WHEN nm_fantasia IS NULL THEN NM_CONTRIBUINTE ELSE NM_FANTASIA end descricao from empresa order by 1"),
    GZSISTEMAS(66, 8, "select codigo id, nomfan descricao, cgc from mercodb.lojas order by codigo"),
    GETWAY(68, 13, "select CODLOJA id, descricao from LOJA order by id"),
    GUIASISTEMAS(71, 13, "select vfd_CodFilial id, vfd_Descricao descricao from tab_filial order by vfd_CodFilial"),
    HRTECH(72, 13, "select\n"
            + "    codigoenti id,\n"
            + "    apeltarefa descricao\n"
            + "from\n"
            + "    fl060loj\n"
            + "order by\n"
            + "    1"),
    HIPCOM(74, 8, "select lojcod as id, concat(lojcod,'' - '', lojfantas) descricao, lojcnpj from hiploj order by 1"),
    HIPER(75, 13, "select id_filial id, razao_social descricao from filial order by id_filial"),
    ISERVER(78, 8, "select Codigo id, NomeFantasia descricao from tbl_loja"),
    INOVA(81, 11, "select empresaid id, empresanomefantasia descricao from empresa"),
    INTERAGE(85, 5, "SELECT \n"
            + "f.CODFIL id, \n"
            + "COALESCE(f.CNPJFIL, 0) || '' - '' || f.NOMFIL descricao \n"
            + "FROM TABFIL f \n"
            + "ORDER BY f.CODFIL"),
    KCMS(92, 13, "select codloja id, fantasia descricao from empresa"),
    LINCE(95, 13, "select cod_loja id , concat(cnpj, '' - '', nome_reduzido) descricao from loja order by 1"),
    LOGUS(100, 7, "select cdg_filial id, dcr_fantasia descricao from cadfil"),
    MARKET(103, 11, "select cd_loja id, cd_loja || '' - '' ||nm_loja descricao from cadastro.tb_loja order by 1"),
    MILENIO(106, 13, "select lojcod id, LOJFAN descricao, LOJCGC, LOJEST from loja order by lojcod"),
    MOBILITY(107, 5, "select id, s_nome_fantasia descricao from configuracoes"),
    MRS(109, 11, "select loja id, nome_fantasia descricao from parametros order by loja"),
    PLENUS(119, 5, "select id_empresa id, nome_fantasia descricao from empresa"),
    MODELO(122, 5, "select id, descricao from empresa"),
    RMS(129, 9, "select loj_codigo||loj_digito as id, loj_codigo||'' - DIGITO ''||loj_digito as descricao from AA2CLOJA order by loj_codigo"),
    RPINFO(130, 11, "select unid_codigo id, unid_reduzido descricao from unidades order by 1"),
    SHI(147, 5, "select codigo id, razsoc descricao from filial"),
    SIAC(149, 9, "select empresa_id id, fantasia descricao from empresas"),
    SISMOURA(154, 13, "select Codigo id, (Fantasia + '' - '' + CNPJ) descricao from Empresa order by Codigo"),
    FIREBIRD_SYSPDV(170, 5, "SELECT prpcod id, prpfan descricao FROM PROPRIO"),
    SQLSERVER_SYSPDV(170, 13, "SELECT prpcod id, prpfan descricao FROM PROPRIO"),
    TOPSYSTEM(176, 8, "SELECT empresa id, Razao_Social descricao FROM cad_filial ORDER BY 1"),
    TSTI(178, 8, "select empcod id, concat(empcnpj, '' '', empnom) descricao from tsl.tsc008a"),
    UNIPLUS(179, 11, "select id, nome || '' - '' || cnpj as descricao from filial"),
    VRMASTER(182, 11, "select l.id, l.descricao from loja l join fornecedor f on l.id_fornecedor = f.id where l.id_situacaocadastro = 1 order by l.id"),
    VISUALMIX(186, 13, "select codigo as id,	descricao from dbo.Lojas order by 1"),
    WEBSAQ(188, 11, "select codestabelec id, razaosocial descricao from estabelecimento order by codestabelec"),
    ECO_CENTAURO(193, 5, "SELECT codigo AS id, NOMEFANTASIA AS descricao FROM TGEREMPRESA t"),
    LINEAR(196, 8, "SELECT emp_codigo AS id, concat(emp_cgc, '' - '', emp_fantasia) AS descricao FROM empresa ORDER BY 1"),
    ARIUS(197, 9, "SELECT id ,id || '' - '' || descritivo || '' - '' || cnpj_cpf descricao FROM empresas ORDER BY id"),
    GATEWAYSISTEMAS(198, 5, "SELECT l.COD_EMPRESA AS id, (l.COD_EMPRESA||'' - ''|| l.FANTASIA||'' - ''||l.CNPJ) AS descricao FROM EMITENTE l ORDER BY 1"),
    SG(199, 11, "select codfil99 id, cgcfil99 || '' - '' || apelido99 descricao from cadfil"),
    SYGMA(200, 5, "select 1 as id, val_parametro descricao from tparametro t where ordem_parametro = 1002"),
    MICROTAB(201, 5, "SELECT 1 as id, ''Loja_01'' descricao FROM CONFIG"),
    MRC6(202, 13, "select codigo as id, nome as descricao, cnpj from entidades"),
    BOMSOFT(203, 5, "SELECT ID_CFG id, FANTASIA_CFG descricao FROM CONFIG ORDER BY 1"),
    ASSIST(204, 11, "select 1 id, ''LOJA 01'' descricao"),
    DATABYTE(205, 5, "SELECT codigo AS id, nome AS descricao FROM FILIAL"),
    DOBESCGA(206, 5, "select ret000.\"Codigo\" as id, ret000.\"Fantasia\" as descricao, ret000.\"CNPJ\" from ret000 order by ret000.\"Codigo\""),
    TENTACULO(207, 5, "SELECT EMP_CODIGO id, EMP_FANTASIA descricao FROM EMPRESAS ORDER BY 1"),
    FXSISTEMAS(208, 5, "SELECT e.ID, e.RAZAO_SOCIAL descricao FROM EMPRESA e"),
    CONSINCO(209, 9, "SELECT nroempresa id, nomereduzido  || '' - '' || nrocgc || '''' || digcgc descricao FROM consinco.ge_empresa WHERE status = ''A''"),
    VERSATIL(210, 5, "SELECT COD_EMPRESA id, FANTASIA descricao FROM EMPRESA"),
    DSIC(211, 11, "select emp_id id, emp_nomefantasia descricao from empresa"),
    TSl(212, 8, "SELECT EMPCOD id, EMPNOM descricao FROM tsc008a"),
    SATFACIL(213, 5, "SELECT ID_REGISTRO id, NOME_LOJA descricao FROM PARAMETROS"),
    WBA(214, 5, "SELECT CAST(CODIGO AS integer) AS id, NOME descricao FROM FILIAL ORDER BY 1"),
    STOCK(215, 11, "select empid as id, empnome as descricao from tbempresas;"),
    CMM(216, 13, "select f.CodFil id, f.CNPJ + '' - '' + f.Fantasia as descricao from Filiais f"),
    GZPRODADOS(217, 8, "select IdEmpresa as id, RazaoSocial as descricao from empresa;"),
    PRISMAFLEX(218, 5, "SELECT EMPCODIGO id, EMPRAZAOS descricao FROM EMPRESAS ORDER BY 1"),
    JNP_MSUPER(219, 5, "SELECT SUP999 id, FANTASIA descricao, CNPJ cpfcnpj FROM SUP999 ORDER BY 1"),
    ETRADE(220, 13, "SELECT codigo id, Fantasia descricao FROM Filial"),
    GANSO(221, 5, "SELECT  CODIGO id, FANTASIA descricao FROM EMPRESA_FILIAL"),
    LCSISTEMAS(222, 8, "select id, fantasia descricao from empresa"),
    GESTORA(223, 13, "SELECT EMP_CODIGO id, EMP_NOME descricao from EMPRESA"),
    NEREUS(224, 11, "select id_emp id, fantasia descricao from cg_emp order by 1"),
    SCORPION(225, 5, "SELECT LOJA id, RAZAOSOCIAL descricao FROM TB_EMPRESA"),
    MEGASOFTWARE(226, 5, "SELECT cgc id, FANTASIA descricao FROM EMPRESA"),
    ORION_POSTGRES(227, 11, "select substring(cgc,1,7) id, firma descricao from config"),
    ARAUTO(228, 5, "select id, nomefantasia descricao from empresa"),
    SUPERCONTROLE(229, 13, "select lj.id id, descricaoLoja + '' - '' + f.cnpj as descricao from MultiLoja.Loja lj join Cadastro.Entidade f on lj.fkCliente = f.id order by lj.id"),
    FOCUS(230, 8, "select id, razao descricao from empresa"),
    MANAGER(231, 5, "select codigo id, fantasia descricao from empresa"),
    SCV(232, 5, "SELECT id, NOME_FANTASIA descricao FROM EMPRESAS where id = 1;"),
    SINC(233, 11, "select ncad_cgcocpf_2 id, ncad_fantasi_2 descricao from sincad where ncad_cgcocpf_2 = 44744589000108"), /*<-- CNPJ do cliente, alterar em novo projeto*/
    DX(234, 5, "select  c_codloja id, c_fantaloja descricao from empresa"),
    LIVRE(235, 5, "SELECT CODIGO id, EMPRESA descricao FROM EMPRESA"),
    WINTHOR(236, 9, "select codigo id, concat(concat(CAST(codigo AS varchar(10)),'' - ''),coalesce(fantasia, razaosocial)) descricao FROM pcfilial ORDER BY codigo"),
    WLS(237, 5, "SELECT COD_LOJA id, NOME_FANTASIA || '' - '' || CNPJ descricao FROM INFORMACOES ORDER BY COD_LOJA"),
    GSOFT(238, 13, "select Codigo id, nome descricao from MC_Empresa"),
    FACIL(239, 5, "select EMPRESAS_ID id, EMPRESA_NOME descricao from EMPRESAS"),
    BRDATA(240, 13, "select  C021_Codigo id, C021_Codigo + ''-'' + C021_Descricao descricao from C021_Deposito"),
    ARIUSWEB(241, 8, "select nroloja id, razao descricao from controle.pf_loja"),
    DEVSIS(242, 5, "SELECT REFERENCIAL id, NOME descricao FROM CON_EMPRESA"),
    PRIME(243, 11, "select empr_codigo as id,  empr_nomereduzido as descricao from empresas order by 1"),
    PALLAS(244, 8, "select cod_cli id, nome_cli descricao from cliente where cod_cli = 1"),
    ALCANCE(245, 8, "SELECT NumComanda id, Titulo descricao FROM ppcx.paramh"),
    GEP(246, 5, "SELECT CODIGO id, NOMEFANTASIA descricao FROM EMPRESA"),
    SOFTLOG(247, 5, "SELECT CODIGO id, FILIAL descricao FROM C000004"),
    MBD(248, 5, "SELECT COUNT(*) id, RAZAO_SOCIAL descricao FROM FIRMA f GROUP BY RAZAO_SOCIAL"),
    FENIXME(249, 5, "SELECT CODIGOFILIAL id, CODIGOFILIAL|| ''-'' ||NOME AS descricao FROM FILIAL ORDER BY 1"),
    TARGET_G3(250, 13, "select cd_emp id, raz_soc descricao from empresa"),
    WISE(251, 5, "SELECT 1 id, FANTASIA descricao FROM NFE_EMPRESAS"),
    GENERICO(252, 11, "vazio"),
    SISMASTER(253, 8, "select codigo id, concat(nomefantasia, '' '', cnpj) as descricao from tabdollar t"),
    ALTERDATAWSHOP(254, 11, "select cdempresa id,  nmempresa descricao from wshop.empshop order by cdempresa"),
    STI3(255, 8, "select codigo as id,razao as descricao from empresas"),
    CENTER_INFORMATICA(256, 5, "SELECT COD_EMP AS id , razao AS descricao FROM CONFIG c"),
    ARGO(257, 13, "select codemp id, razemp descricao from empresa"),
    VIVASISTEMAS(258, 5, "SELECT EMPR_PK id, EMPR_FANTASIA descricao FROM CDTR_EMPRESA"),
    RESULTHBUSINESS(259, 5, "SELECT 1 as id , empresa AS descricao FROM EMPRESA e "),
    LJSISTEMAS_SIG(260, 5, "SELECT EMPCONTADOR id, DESCEMPRESA descricao  FROM EMP001"),
    JMASTER(88, 13, "select LOJCODIGO as id, LOJRAZAO as descricao from dbo.CADLOJ "),
    G3(60, 8, "select idempresa id,RazaoSocial descricao from  empresa e "),
    SAURUSPDV(261, 13, "select loj_idLoja id, loj_fant descricao  from tbLojaDados"),
    EMPRESOFT(263, 8, "select  codigo as id, fantasia  as descricao from empresa "),
    CONNEXONE(264, 8, "select codigo id, nomeFantasia descricao from registroempresa "),
    SANTSYSTEM(265, 13, "select IdEmpresa as id, RSocial as descricao from Empresa"),
    VISUALCOMERCIO(185, 13, "select PA_CODIGO as id, PA_EMPRESA as descricao from parametros"),
    DIRECTOR(42, 13, "select DFcod_empresa as id, DFnome_fantasia  as descricao from TBempresa"),
    ARPA(262, 11, "select 1 as id, razao as descricao from registro r "),
    ATHOS_SQLSERVER(266, 13, "select 1 id, ''Loja Validar'' descricao"),
    IDEALSOFT(267, 13, "SELECT Ordem id, Razao_Social descricao from Filiais f "),
    SOLIDUSORACLE(268, 9, "select l.cod_loja id, l.cod_loja || '' - '' || l.des_fantasia descricao, num_cgc from intersolid.tab_loja l where flg_desativada = ''N'' order by 1"),
    PROSUPER(269, 11, "select emcodigo as id, emnome as descricao from empresa "),
    UPSOFTWARE(270, 13, "select cod_emp id, fantasia descricao from Tempresa t "),
    SERVSIC(271, 13, "select IdEmpre id, Empresa descricao from TabEmpre"),
    SIACRIARE(148,8,"select codigo_n id, descricao razao from empresas"),
    CONTROLWARE(31, 11, "select codestabelec id, razaosocial descricao from estabelecimento order by id");

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
