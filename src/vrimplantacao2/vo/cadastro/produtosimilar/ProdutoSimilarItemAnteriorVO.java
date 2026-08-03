package vrimplantacao2.vo.cadastro.produtosimilar;

import vrimplantacao2.vo.cadastro.ProdutoVO;

public class ProdutoSimilarItemAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private ProdutoSimilarItemVO codigoAtual;
    private String importIdProdutoSimilar;
    private ProdutoSimilarVO codigoAtualProdutoSimilar;
    private String importIdProduto;
    private ProdutoVO codigoAtualProduto;
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

    public ProdutoSimilarItemVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(ProdutoSimilarItemVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getImportIdProdutoSimilar() {
        return importIdProdutoSimilar;
    }

    public void setImportIdProdutoSimilar(String importIdProdutoSimilar) {
        this.importIdProdutoSimilar = importIdProdutoSimilar;
    }

    public ProdutoSimilarVO getCodigoAtualProdutoSimilar() {
        return codigoAtualProdutoSimilar;
    }

    public void setCodigoAtualProdutoSimilar(ProdutoSimilarVO codigoAtualProdutoSimilar) {
        this.codigoAtualProdutoSimilar = codigoAtualProdutoSimilar;
    }

    public ProdutoVO getCodigoAtualProduto() {
        return codigoAtualProduto;
    }

    public void setCodigoAtualProduto(ProdutoVO codigoAtualProduto) {
        this.codigoAtualProduto = codigoAtualProduto;
    }
    
    public String getImportIdProduto() {
        return importIdProduto;
    }

    public void setImportIdProduto(String importIdProduto) {
        this.importIdProduto = importIdProduto;
    }
    
    public String getObservacaoImportacao() {
        return observacaoImportacao;
    }

    public void setObservacaoImportacao(String observacaoImportacao) {
        this.observacaoImportacao = observacaoImportacao;
    }
}
