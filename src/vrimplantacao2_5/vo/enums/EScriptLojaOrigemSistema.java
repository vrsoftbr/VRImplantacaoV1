/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum EScriptLojaOrigemSistema {

    GATEWAYSISTEMAS(198, 5, 
        "SELECT l.COD_EMPRESA AS id, (l.NOME||'' - ''||l.CNPJ) AS descricao FROM EMITENTE l ORDER BY 1");
    
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
