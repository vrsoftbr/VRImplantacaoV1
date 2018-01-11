package vrimplantacao.vo.vrimplantacao;

import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author handerson
 */
public class ProdutoComplementoVO {

    public long id = 0;
    public double estoqueMinimo = 0;
    public double estoqueMaximo = 0;
    public int teclaAssociada = 0;
    public int idAliquotaCredito = 0;
    public int idSituacaoCadastro = 1;
    public int idTipoPisCofinsCredito = 0;
    public String dataUltimoPreco = "";
    public String dataUltimaEntrada = "";
    public String dataUltimaVenda = "";
    public String dataUltimaEntradaAnterior = "";
    public String prateleira = "";
    public String secao = "";
    public boolean emiteEtiqueta = true;
    public boolean descontinuado = false;
    public boolean centralizado = false;
    public double estoque = 0;
    public int idLoja = 0;
    public double operacional = 0;
    public double custoSemImposto = 0;
    public double custoComImposto = 0;
    public double custoSemImpostoAnterior = 0;
    public double custoComImpostoAnterior = 0;
    public double custoSemPerdaSemImposto = 0;
    public double custoSemPerdaSemImpostoAnterior = 0;
    public double custoMedioComImposto = 0;
    public double custoMedioSemImposto = 0;
    public double custoMedioComImpostoAnterior = 0;
    public double custoMedioSemImpostoAnterior = 0;
    public double precoVenda = 0;
    public double quantidadeUltimaEntrada = 0;
    public double valorIcmsSubstituicao = 0;
    public double valorOutrasSubstituicao = 0;
    public double precoVendaAnterior = 0;
    public double precoDiaSeguinte = 0;
    public double valorIpi = 0;
    public double cestaBasica = 0;
    public double troca = 0;
    public long codigoBarras = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(double estoqueMinimo) {
        this.estoqueMinimo = MathUtils.trunc(estoqueMinimo, 3);
    }

    public double getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public void setEstoqueMaximo(double estoqueMaximo) {
        this.estoqueMaximo = MathUtils.trunc(estoqueMaximo, 3 );
    }

    public int getTeclaAssociada() {
        return teclaAssociada;
    }

    public void setTeclaAssociada(int teclaAssociada) {
        this.teclaAssociada = teclaAssociada;
    }

    public int getIdAliquotaCredito() {
        return idAliquotaCredito;
    }

    public void setIdAliquotaCredito(int idAliquotaCredito) {
        this.idAliquotaCredito = idAliquotaCredito;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }

    public int getIdTipoPisCofinsCredito() {
        return idTipoPisCofinsCredito;
    }

    public void setIdTipoPisCofinsCredito(int idTipoPisCofinsCredito) {
        this.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
    }

    public String getDataUltimoPreco() {
        return dataUltimoPreco;
    }

    public void setDataUltimoPreco(String dataUltimoPreco) {
        this.dataUltimoPreco = dataUltimoPreco;
    }

    public String getDataUltimaEntrada() {
        return dataUltimaEntrada;
    }

    public void setDataUltimaEntrada(String dataUltimaEntrada) {
        this.dataUltimaEntrada = dataUltimaEntrada;
    }

    public String getDataUltimaVenda() {
        return dataUltimaVenda;
    }

    public void setDataUltimaVenda(String dataUltimaVenda) {
        this.dataUltimaVenda = dataUltimaVenda;
    }

    public String getDataUltimaEntradaAnterior() {
        return dataUltimaEntradaAnterior;
    }

    public void setDataUltimaEntradaAnterior(String dataUltimaEntradaAnterior) {
        this.dataUltimaEntradaAnterior = dataUltimaEntradaAnterior;
    }

    public String getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(String prateleira) {
        this.prateleira = prateleira;
    }

    public String getSecao() {
        return secao;
    }

    public void setSecao(String secao) {
        this.secao = secao;
    }

    public boolean isEmiteEtiqueta() {
        return emiteEtiqueta;
    }

    public void setEmiteEtiqueta(boolean emiteEtiqueta) {
        this.emiteEtiqueta = emiteEtiqueta;
    }

    public boolean isDescontinuado() {
        return descontinuado;
    }

    public void setDescontinuado(boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

    public boolean isCentralizado() {
        return centralizado;
    }

    public void setCentralizado(boolean centralizado) {
        this.centralizado = centralizado;
    }

    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = MathUtils.trunc(estoque, 3);
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public double getOperacional() {
        return operacional;
    }

    public void setOperacional(double operacional) {
        this.operacional = operacional;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = MathUtils.round(custoSemImposto, 4);
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4);
    }

    public double getCustoSemImpostoAnterior() {
        return custoSemImpostoAnterior;
    }

    public void setCustoSemImpostoAnterior(double custoSemImpostoAnterior) {
        this.custoSemImpostoAnterior = custoSemImpostoAnterior;
    }

    public double getCustoComImpostoAnterior() {
        return custoComImpostoAnterior;
    }

    public void setCustoComImpostoAnterior(double custoComImpostoAnterior) {
        this.custoComImpostoAnterior = custoComImpostoAnterior;
    }

    public double getCustoSemPerdaSemImposto() {
        return custoSemPerdaSemImposto;
    }

    public void setCustoSemPerdaSemImposto(double custoSemPerdaSemImposto) {
        this.custoSemPerdaSemImposto = custoSemPerdaSemImposto;
    }

    public double getCustoSemPerdaSemImpostoAnterior() {
        return custoSemPerdaSemImpostoAnterior;
    }

    public void setCustoSemPerdaSemImpostoAnterior(double custoSemPerdaSemImpostoAnterior) {
        this.custoSemPerdaSemImpostoAnterior = custoSemPerdaSemImpostoAnterior;
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

    public double getCustoMedioComImpostoAnterior() {
        return custoMedioComImpostoAnterior;
    }

    public void setCustoMedioComImpostoAnterior(double custoMedioComImpostoAnterior) {
        this.custoMedioComImpostoAnterior = custoMedioComImpostoAnterior;
    }

    public double getCustoMedioSemImpostoAnterior() {
        return custoMedioSemImpostoAnterior;
    }

    public void setCustoMedioSemImpostoAnterior(double custoMedioSemImpostoAnterior) {
        this.custoMedioSemImpostoAnterior = custoMedioSemImpostoAnterior;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getQuantidadeUltimaEntrada() {
        return quantidadeUltimaEntrada;
    }

    public void setQuantidadeUltimaEntrada(double quantidadeUltimaEntrada) {
        this.quantidadeUltimaEntrada = quantidadeUltimaEntrada;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public double getValorOutrasSubstituicao() {
        return valorOutrasSubstituicao;
    }

    public void setValorOutrasSubstituicao(double valorOutrasSubstituicao) {
        this.valorOutrasSubstituicao = valorOutrasSubstituicao;
    }

    public double getPrecoVendaAnterior() {
        return precoVendaAnterior;
    }

    public void setPrecoVendaAnterior(double precoVendaAnterior) {
        this.precoVendaAnterior = precoVendaAnterior;
    }

    public double getPrecoDiaSeguinte() {
        return precoDiaSeguinte;
    }

    public void setPrecoDiaSeguinte(double precoDiaSeguinte) {
        this.precoDiaSeguinte = precoDiaSeguinte;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public double getCestaBasica() {
        return cestaBasica;
    }

    public void setCestaBasica(double cestaBasica) {
        this.cestaBasica = cestaBasica;
    }

    public double getTroca() {
        return troca;
    }

    public void setTroca(double troca) {
        this.troca = troca;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
    
    
}
