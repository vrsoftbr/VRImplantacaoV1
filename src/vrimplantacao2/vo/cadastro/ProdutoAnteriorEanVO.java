package vrimplantacao2.vo.cadastro;

import vrimplantacao.utils.Utils;

public class ProdutoAnteriorEanVO {
    
    private String importSistema;
    private String importLoja;
    private String importId;
    private String ean;
    private int qtdEmbalagem = 1;
    private double valor = 0;
    private String tipoEmbalagem = "UN";

    public String getImportSistema() {
        return importSistema;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = Utils.acertarTexto(ean);
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem != null ? tipoEmbalagem : "UN";
    }
    
    
    
}
