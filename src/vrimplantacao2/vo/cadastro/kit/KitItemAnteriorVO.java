package vrimplantacao2.vo.cadastro.kit;

public class KitItemAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private KitItemVO codigoAtual;
    private String importIdKit;
    private KitVO codigoAtualKit;
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

    public KitItemVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(KitItemVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getImportIdKit() {
        return importIdKit;
    }

    public void setImportIdKit(String importIdKit) {
        this.importIdKit = importIdKit;
    }

    public KitVO getCodigoAtualKit() {
        return codigoAtualKit;
    }

    public void setCodigoAtualKit(KitVO codigoAtualKit) {
        this.codigoAtualKit = codigoAtualKit;
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
