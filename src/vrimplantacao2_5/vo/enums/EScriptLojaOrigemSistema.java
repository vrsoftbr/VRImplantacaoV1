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
    SYGMA(200,5, "select "
            + "1 as id, "
            + "val_parametro descricao "
            + "from tparametro t "
            + "where ordem_parametro = 1002"),
    UNIPLUS(179, 11, 
            "select \n" +
            "	id,\n" +
            "	nome || '' - '' || cnpj as descricao\n" +
            "from \n" +
            "	filial"),
    AVISTARE(16, 13, "select distinct\n"
            + "	(select CfgValue from dbo.TB_CONFIG where CfgChave = ''CNPJ'') as id,\n"
            + " ((select CfgValue from dbo.TB_CONFIG where CfgChave = ''EmpresaRegistro'') + '' - '' + "
            + "(select CfgValue from dbo.TB_CONFIG where CfgChave = ''CNPJ'')) as descricao\n"
            + "from dbo.TB_CONFIG");

    
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
