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
            "select " +
            "codfil99 id, " +
            "cgcfil99 || '' - '' || apelido99 descricao " +
            "from " +
            "cadfil");
    
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
