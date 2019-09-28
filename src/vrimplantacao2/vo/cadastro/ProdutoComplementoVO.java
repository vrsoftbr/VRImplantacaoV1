package vrimplantacao2.vo.cadastro;

import java.util.Date;
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
    private double troca = 0;
    private double custoSemImposto = 0;
    private double custoComImposto = 0;
    private double precoVenda = 0;
    private double precoDiaSeguinte = 0;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private boolean descontinuado = false;
    private boolean fabricacaoPropria = false;
    private boolean emiteEtiqueta = true;
    private TipoProduto tipoProduto = TipoProduto.MERCADORIA_REVENDA;
    private int idAliquotaCredito = 0;
    private Date dataPrimeiraAlteracao;

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
        this.estoqueMinimo = MathUtils.round(estoqueMinimo, 0, 9999999D);
    }

    public void setEstoqueMaximo(double estoqueMaximo) {
        this.estoqueMaximo = MathUtils.round(estoqueMaximo, 0, 9999999D);
    }

    public void setEstoque(double estoque) {
        this.estoque = MathUtils.round(estoque, 3, 9999999D);
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4, 9999999D);
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4, 9999999D);
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = MathUtils.round(precoVenda, 4, 9999999D);
    }

    public void setPrecoDiaSeguinte(double precoDiaSeguinte) {
        this.precoDiaSeguinte = MathUtils.round(precoDiaSeguinte, 2);
    }

    public void setDescontinuado(boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

    public double getTroca() {
        return troca;
    }

    public void setTroca(double troca) {
        this.troca = MathUtils.round(troca, 3, 9999999D);
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

    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }

    public boolean isEmiteEtiqueta() {
        return emiteEtiqueta;
    }

    public void setEmiteEtiqueta(boolean emiteEtiqueta) {
        this.emiteEtiqueta = emiteEtiqueta;
    }

    public int getIdAliquotaCredito() {
        return idAliquotaCredito;
    }

    public void setIdAliquotaCredito(int idAliquotaCredito) {
        this.idAliquotaCredito = idAliquotaCredito;
    }

    public Date getDataPrimeiraAlteracao() {
        return dataPrimeiraAlteracao;
    }

    public void setDataPrimeiraAlteracao(Date dataPrimeiraAlteracao) {
        this.dataPrimeiraAlteracao = dataPrimeiraAlteracao;
    }

}
