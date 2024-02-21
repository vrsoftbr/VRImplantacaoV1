package vrimplantacao2.vo.cadastro;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.NormaCompra;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoAtacado;
import vrimplantacao2.vo.enums.TipoProduto;

public class ProdutoComplementoVO {

    /**
     * @return the margem
     */
    public double getMargem() {
        return margem;
    }

    /**
     * @param margem the margem to set
     */
    public void setMargem(double margem) {
        this.margem = margem;
    }

    /**
     * @return the margemminima
     */
    public double getMargemMinima() {
        return margemminima;
    }

    /**
     * @param margemminima the margemminima to set
     */
    public void setMargemMinima(double margemminima) {
        this.margemminima = margemminima;
    }

    /**
     * @return the margemmaxima
     */
    public double getMargemMaxima() {
        return margemmaxima;
    }

    /**
     * @param margemmaxima the margemmaxima to set
     */
    public void setMargemMaxima(double margemmaxima) {
        this.margemmaxima = margemmaxima;
    }

    /**
     * @return the teclaassociada
     */
    public int getTeclaassociada() {
        return teclaassociada;
    }

    /**
     * @param teclaassociada the teclaassociada to set
     */
    public void setTeclaassociada(int teclaassociada) {
        this.teclaassociada = teclaassociada;
    }
    
    private int id = 0;
    private ProdutoVO produto;
    private int idLoja = 1;
    private double estoqueMinimo = 0;
    private double estoqueMaximo = 0;
    private double estoque = 0;
    private double troca = 0;
    private double custoSemImposto = 0;
    private double custoComImposto = 0;
    private double custoAnteriorSemImposto = 0;
    private double custoAnteriorComImposto = 0;
    private double custoMedioComImposto = 0;
    private double custoMedioSemImposto = 0;
    private double precoVenda = 0;
    private double precoDiaSeguinte = 0;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private boolean descontinuado = false;
    private boolean fabricacaoPropria = false;
    private boolean emiteEtiqueta = true;
    private TipoProduto tipoProduto = TipoProduto.MERCADORIA_REVENDA;
    private TipoAtacado tipoAtacado = TipoAtacado.EMBALAGEM;
    private int idAliquotaCredito = 0;
    private Date dataPrimeiraAlteracao;
    private NormaCompra normaReposicao = NormaCompra.CAIXA;
    private String setor = "";
    private String prateleira = "";
    private Date dataMovimento;
    private Timestamp dataHora;
    private int teclaassociada;
    private double margem;
    private double margemminima;
    private double margemmaxima;
    private int operacional = 0;
    private int validade = 0;

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

    public void setCustoAnteriorSemImposto(double custoAnteriorSemImposto) {
        this.custoAnteriorSemImposto = MathUtils.round(custoAnteriorSemImposto, 4, 9999999D);
    }
    
    public void setCustoAnteriorComImposto(double custoAnteriorComImposto) {
        this.custoAnteriorComImposto = MathUtils.round(custoAnteriorComImposto, 4, 9999999D);
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
    
    public double getCustoAnteriorSemImposto() {
        return custoAnteriorSemImposto;
    }
    
    public double getCustoAnteriorComImposto() {
        return custoAnteriorComImposto;
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

    public NormaCompra getNormaReposicao() {
        return normaReposicao;
    }

    public void setNormaReposicao(NormaCompra normaReposicao) {
        this.normaReposicao = normaReposicao == null ? NormaCompra.CAIXA : normaReposicao;
    }

    public String getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(String prateleira) {
        this.prateleira = Utils.acertarTexto(prateleira, 3);
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }   

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    public TipoAtacado getTipoAtacado() {
        return tipoAtacado;
    }

    public void setTipoAtacado(TipoAtacado tipoAtacado) {
        this.tipoAtacado = tipoAtacado;
    }

    public double getCustoMedioComImposto() {
        return custoMedioComImposto;
    }

    public void setCustoMedioComImposto(double custoMedioComImposto) {
        this.custoMedioComImposto = custoMedioComImposto;
    }
    
    public double getCustoMedioSemImposto() {
        return custoMedioSemImposto;
    }

    public void setCustoMedioSemImposto(double custoMedioSemImposto) {
        this.custoMedioSemImposto = custoMedioSemImposto;
    }

    public int getOperacional() {
        return operacional;
    }

    public void setOperacional(int operacional) {
        this.operacional = operacional;
    }
    
    public int getValidade() {
        return validade;
    }
    
    public void setValidade(int validade) {
        this.validade = validade > 0 ? validade : 0;
    }
}
