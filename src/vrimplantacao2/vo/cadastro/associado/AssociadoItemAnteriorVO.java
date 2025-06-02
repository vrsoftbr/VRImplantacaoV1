package vrimplantacao2.vo.cadastro.associado;

import vrimplantacao2.vo.cadastro.ProdutoVO;

public class AssociadoItemAnteriorVO {

    private String importSistema;
    private String importLoja;
    private String importId;
    private AssociadoItemVO codigoAtual;
    private String importIdAssociado;
    private AssociadoVO codigoAtualAssociado;
    private String importIdProduto;
    private ProdutoVO codigoAtualProdutoAssociadoItem;
    private int qtdEmbalagemItem = 1;
    private double percentualPreco = 0;
    private boolean aplicaPreco = false;
    private boolean aplicaCusto = false;
    private boolean aplicaEstoque = true;
    private double percentualcustoestoque = 0;
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

    public AssociadoItemVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(AssociadoItemVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getImportIdAssociado() {
        return importIdAssociado;
    }

    public void setImportIdAssociado(String importIdAssociado) {
        this.importIdAssociado = importIdAssociado;
    }

    public AssociadoVO getCodigoAtualAssociado() {
        return codigoAtualAssociado;
    }

    public void setCodigoAtualAssociado(AssociadoVO codigoAtualAssociado) {
        this.codigoAtualAssociado = codigoAtualAssociado;
    }

    public String getImportIdProduto() {
        return importIdProduto;
    }

    public void setImportIdProduto(String importIdProduto) {
        this.importIdProduto = importIdProduto;
    }

    public ProdutoVO getCodigoAtualProdutoAssociadoItem() {
        return codigoAtualProdutoAssociadoItem;
    }

    public void setCodigoAtualProdutoAssociadoItem(ProdutoVO codigoAtualProdutoAssociadoItem) {
        this.codigoAtualProdutoAssociadoItem = codigoAtualProdutoAssociadoItem;
    }

    public int getQtdEmbalagemItem() {
        return qtdEmbalagemItem;
    }

    public void setQtdEmbalagemItem(int qtdEmbalagemItem) {
        this.qtdEmbalagemItem = qtdEmbalagemItem;
    }

    public double getPercentualPreco() {
        return percentualPreco;
    }

    public void setPercentualPreco(double percentualPreco) {
        this.percentualPreco = percentualPreco;
    }

    public boolean isAplicaPreco() {
        return aplicaPreco;
    }

    public void setAplicaPreco(boolean aplicaPreco) {
        this.aplicaPreco = aplicaPreco;
    }

    public boolean isAplicaCusto() {
        return aplicaCusto;
    }

    public void setAplicaCusto(boolean aplicaCusto) {
        this.aplicaCusto = aplicaCusto;
    }

    public boolean isAplicaEstoque() {
        return aplicaEstoque;
    }

    public void setAplicaEstoque(boolean aplicaEstoque) {
        this.aplicaEstoque = aplicaEstoque;
    }

    public double getPercentualcustoestoque() {
        return percentualcustoestoque;
    }

    public void setPercentualcustoestoque(double percentualcustoestoque) {
        this.percentualcustoestoque = percentualcustoestoque;
    }


    public String getObservacaoImportacao() {
        return observacaoImportacao;
    }

    public void setObservacaoImportacao(String observacaoImportacao) {
        this.observacaoImportacao = observacaoImportacao;
    }
}
