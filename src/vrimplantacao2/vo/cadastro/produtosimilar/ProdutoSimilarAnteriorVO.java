package vrimplantacao2.vo.cadastro.produtosimilar;

public class ProdutoSimilarAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private ProdutoSimilarVO codigoAtual;
    private String descricao;
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

    public ProdutoSimilarVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(ProdutoSimilarVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacaoImportacao() {
        return observacaoImportacao;
    }

    public void setObservacaoImportacao(String observacaoImportacao) {
        this.observacaoImportacao = observacaoImportacao;
    }
}
