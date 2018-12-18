package vrimplantacao2.vo.cadastro;

import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoProduto;

public class ProdutoComplementoVO {
    
    private int id = 0;
    private ProdutoVO produto;
    private int idLoja = 1;
    private double estoqueMinimo = 0;
    private double estoqueMaximo = 0;
    private double estoque = 0;
    private double custoSemImposto = 0;
    private double custoComImposto = 0;
    private double precoVenda = 0;
    private double precoDiaSeguinte = 0;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private boolean descontinuado = false;
    private TipoProduto tipoProduto = TipoProduto.MERCADORIA_REVENDA;

    public void setId(int id) {
        this.id = id;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public void setEstoqueMinimo(double estoqueMinimo) {
        this.estoqueMinimo = MathUtils.round(estoqueMinimo, 0, 99999999D);
    }

    public void setEstoqueMaximo(double estoqueMaximo) {
        this.estoqueMaximo = MathUtils.round(estoqueMaximo, 0, 99999999D);
    }

    public void setEstoque(double estoque) {
        this.estoque = MathUtils.round(estoque, 3, 9999999D);
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4);
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4);
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = MathUtils.round(precoVenda, 4);
    }

    public void setPrecoDiaSeguinte(double precoDiaSeguinte) {
        this.precoDiaSeguinte = MathUtils.round(precoDiaSeguinte, 2);
    }

    public void setDescontinuado(boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

    public int getId() {
        return id;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public double getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public double getEstoque() {
        return estoque;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public double getPrecoDiaSeguinte() {
        return precoDiaSeguinte;
    }

    public boolean isDescontinuado() {
        return descontinuado;
    }    

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto == null ? TipoProduto.MERCADORIA_REVENDA : tipoProduto;
    }

}
