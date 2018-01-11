package vrimplantacao.vo.vrimplantacao;

import vrimplantacao.utils.Utils;

public class FamiliaProdutoVO {
    
    public int id = 0;
    public String descricao = "";
    public int id_situacaocadastro = 1;
    public int codigoant = 0;
    public long idLong = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {      
        this.descricao = Utils.acertarTexto(descricao, 35, "SEM DESCRICAO " + getId());
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public int getCodigoant() {
        return codigoant;
    }

    public void setCodigoant(int codigoant) {
        this.codigoant = codigoant;
    }

    public long getIdLong() {
        return idLong;
    }

    public void setIdLong(long idLong) {
        this.idLong = idLong;
    }

    
    //<editor-fold defaultstate="collapsed" desc="Campos para importação">    
    private String impSistema;
    private String impLoja;
    private String impId;
    
    public String getImpSistema() {
        return impSistema;
    }
    
    public void setImpSistema(String impSistema) {
        this.impSistema = impSistema;
    }
    
    public String getImpLoja() {
        return impLoja;
    }
    
    public void setImpLoja(String impLoja) {
        this.impLoja = impLoja;
    }
    
    public String getImpId() {
        return impId;
    }
    
    public void setImpId(String impId) {
        this.impId = impId;
    }
    //</editor-fold>
    
    
   
}
