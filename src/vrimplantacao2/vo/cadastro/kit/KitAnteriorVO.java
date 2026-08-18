package vrimplantacao2.vo.cadastro.kit;

public class KitAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private KitVO codigoAtual;
    private String importIdProduto;
    private int codigoAtualProduto;
    private String observacaoImportacao = "";

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

    public KitVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(KitVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getImportIdProduto() {
        return importIdProduto;
    }

    public void setImportIdProduto(String importIdProduto) {
        this.importIdProduto = importIdProduto;
    }

    public int getCodigoAtualProduto() {
        return codigoAtualProduto;
    }

    public void setCodigoAtualProduto(int codigoAtualProduto) {
        this.codigoAtualProduto = codigoAtualProduto;
    }

    public String getObservacaoImportacao() {
        return observacaoImportacao;
    }

    public void setObservacaoImportacao(String observacaoImportacao) {
        this.observacaoImportacao = observacaoImportacao;
    }
}
