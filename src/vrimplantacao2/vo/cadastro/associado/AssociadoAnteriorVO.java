package vrimplantacao2.vo.cadastro.associado;

import vrimplantacao2.vo.cadastro.ProdutoVO;

public class AssociadoAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private AssociadoVO codigoAtual;
    private String importIdProduto;
    private ProdutoVO codigoAtualProdutoAssociado;
    private int qtdEmbalagem;
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

    public AssociadoVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(AssociadoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getImportIdProduto() {
        return importIdProduto;
    }

    public void setImportIdProduto(String importIdProduto) {
        this.importIdProduto = importIdProduto;
    }

    public ProdutoVO getCodigoAtualProdutoAssociado() {
        return codigoAtualProdutoAssociado;
    }

    public void setCodigoAtualProdutoAssociado(ProdutoVO codigoAtualProdutoAssociado) {
        this.codigoAtualProdutoAssociado = codigoAtualProdutoAssociado;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public String getObservacaoImportacao() {
        return observacaoImportacao;
    }

    public void setObservacaoImportacao(String observacaoImportacao) {
        this.observacaoImportacao = observacaoImportacao;
    }
}
