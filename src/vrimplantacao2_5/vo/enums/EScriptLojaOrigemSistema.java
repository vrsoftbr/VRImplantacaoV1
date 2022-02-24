package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum EScriptLojaOrigemSistema {

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
    VRMASTER(182, 11, "select\n" +
                    "	l.id,\n" +
                    "	l.descricao\n" +
                    "from \n" +
                    "	loja l \n" +
                    "inner join fornecedor f on l.id_fornecedor = f.id \n" +
                    "where l.id_situacaocadastro = 1\n" +
                    "order by\n" +
                    "	l.id"),
    SYSPDV(170, 5, "SELECT prpcod id, prpfan descricao FROM PROPRIO"),
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
    LINEAR(196, 8, "SELECT emp_codigo AS id, emp_razao AS razao FROM empresa ORDER BY 1"),
    CPGESTOR(21, 9, "SELECT \n" +
                    "	LJ_ASSOCIACAO id,\n" +
                    "	''SANTA FE'' razao\n" +
                    "FROM \n" +
                    "	vw_exp_produtos_sta\n" +
                    "WHERE \n" +
                    "	rownum <= 1\n" +
                    "UNION ALL \n" +
                    "SELECT \n" +
                    "	lj_associacao id,\n" +
                    "	''ZUZU'' razao\n" +
                    "FROM \n" +
                    "	vw_exp_produtos_zuzu\n" +
                    "WHERE \n" +
                    "	rownum <= 1");

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
