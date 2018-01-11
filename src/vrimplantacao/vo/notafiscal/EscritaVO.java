package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;

public class EscritaVO {

    public long id = 0;
    public int idLoja = 0;
    public String loja = "";
    public String data = "";
    public String dataEmissao = "";
    public int idFornecedor = 0;
    public String fornecedor = "";
    public int idClienteEventual = 0;
    public int idEstado = 0;
    public String estado = "";
    public int idTipoEntradaSaida = 0;
    public int idTipoEntrada = 0;
    public String tipoEntrada = "";
    public int idTipoSaida = 0;
    public String serie = "";
    public String especie = "";
    public String modelo = "";
    public int idContaContabilFiscalCredito = 0;
    public int idContaContabilFiscalDebito = 0;
    public int idHistoricoPadrao = 0;
    public int ecf = 0;
    public String observacao = "";
    public String chaveNfe = "";
    public int idTipoFreteNotaFiscal = 0;
    public double valorIpi = 0;
    public double valorIcms = 0;
    public double valorIcmsSubstituicao = 0;
    public double valorBaseCalculo = 0;
    public double valorBaseSubstituicao = 0;
    public double valorCancelamento = 0;
    public double valorFrete = 0;
    public double valorAcrescimo = 0;
    public double valorDesconto = 0;
    public double valorContabil = 0;
    public long cnpj = 0;
    public int numeroNota = 0;
    public int idTipoInscricao = 0;
    public long idNotaEntrada = 0;
    public long idNotaDespesa = 0;
    public long idNotaSaida = 0;
    public boolean complemento = false;
    public boolean cupomFiscal = false;
    public boolean conferido = false;
    public int idFornecedorProdutorRural = 0;
    public int idTipoNota = 0;
    public boolean cancelado = false;
    public double valorOutrasDespesas = 0;
    public double valorIsento = 0;
    public int idSituacaoNfe = 0;
    public String situacaoNfe = "";
    public String cfop = "";
    public String informacaoComplementar = "";
    public List<EscritaItemVO> vItem = new ArrayList();
    public boolean aplicaIcmsIpi = false;

    public boolean isAplicaIcmsIpi() {
        return aplicaIcmsIpi;
    }

    public void setAplicaIcmsIpi(boolean aplicaIcmsIpi) {
        this.aplicaIcmsIpi = aplicaIcmsIpi;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public String getChaveNfe() {
        return chaveNfe;
    }

    public void setChaveNfe(String chaveNfe) {
        this.chaveNfe = chaveNfe;
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public boolean isComplemento() {
        return complemento;
    }

    public void setComplemento(boolean complemento) {
        this.complemento = complemento;
    }

    public boolean isConferido() {
        return conferido;
    }

    public void setConferido(boolean conferido) {
        this.conferido = conferido;
    }

    public boolean isCupomFiscal() {
        return cupomFiscal;
    }

    public void setCupomFiscal(boolean cupomFiscal) {
        this.cupomFiscal = cupomFiscal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public int getEcf() {
        return ecf;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getHistoricoPadrao() {
        return idHistoricoPadrao;
    }

    public void setHistoricoPadrao(int historicoPadrao) {
        this.idHistoricoPadrao = historicoPadrao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(int idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public int getIdContaContabilFiscalCredito() {
        return idContaContabilFiscalCredito;
    }

    public void setIdContaContabilFiscalCredito(int idContaContabilFiscalCredito) {
        this.idContaContabilFiscalCredito = idContaContabilFiscalCredito;
    }

    public int getIdContaContabilFiscalDebito() {
        return idContaContabilFiscalDebito;
    }

    public void setIdContaContabilFiscalDebito(int idContaContabilFiscalDebito) {
        this.idContaContabilFiscalDebito = idContaContabilFiscalDebito;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getIdFornecedorProdutorRural() {
        return idFornecedorProdutorRural;
    }

    public void setIdFornecedorProdutorRural(int idFornecedorProdutorRural) {
        this.idFornecedorProdutorRural = idFornecedorProdutorRural;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public long getIdNotaDespesa() {
        return idNotaDespesa;
    }

    public void setIdNotaDespesa(long idNotaDespesa) {
        this.idNotaDespesa = idNotaDespesa;
    }

    public long getIdNotaEntrada() {
        return idNotaEntrada;
    }

    public void setIdNotaEntrada(long idNotaEntrada) {
        this.idNotaEntrada = idNotaEntrada;
    }

    public long getIdNotaSaida() {
        return idNotaSaida;
    }

    public void setIdNotaSaida(long idNotaSaida) {
        this.idNotaSaida = idNotaSaida;
    }

    public int getIdSituacaoNfe() {
        return idSituacaoNfe;
    }

    public void setIdSituacaoNfe(int idSituacaoNfe) {
        this.idSituacaoNfe = idSituacaoNfe;
    }

    public int getIdTipoEntrada() {
        return idTipoEntrada;
    }

    public void setIdTipoEntrada(int idTipoEntrada) {
        this.idTipoEntrada = idTipoEntrada;
    }

    public int getIdTipoEntradaSaida() {
        return idTipoEntradaSaida;
    }

    public void setIdTipoEntradaSaida(int idTipoEntradaSaida) {
        this.idTipoEntradaSaida = idTipoEntradaSaida;
    }

    public int getIdTipoFreteNotaFiscal() {
        return idTipoFreteNotaFiscal;
    }

    public void setIdTipoFreteNotaFiscal(int idTipoFreteNotaFiscal) {
        this.idTipoFreteNotaFiscal = idTipoFreteNotaFiscal;
    }

    public int getIdTipoInscricao() {
        return idTipoInscricao;
    }

    public void setIdTipoInscricao(int idTipoInscricao) {
        this.idTipoInscricao = idTipoInscricao;
    }

    public int getIdTipoNota() {
        return idTipoNota;
    }

    public void setIdTipoNota(int idTipoNota) {
        this.idTipoNota = idTipoNota;
    }

    public int getIdTipoSaida() {
        return idTipoSaida;
    }

    public void setIdTipoSaida(int idTipoSaida) {
        this.idTipoSaida = idTipoSaida;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getSituacaoNfe() {
        return situacaoNfe;
    }

    public void setSituacaoNfe(String situacaoNfe) {
        this.situacaoNfe = situacaoNfe;
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public List<EscritaItemVO> getvItem() {
        return vItem;
    }

    public void setvItem(List<EscritaItemVO> vItem) {
        this.vItem = vItem;
    }

    public double getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(double valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public double getValorCancelamento() {
        return valorCancelamento;
    }

    public void setValorCancelamento(double valorCancelamento) {
        this.valorCancelamento = valorCancelamento;
    }

    public double getValorContabil() {
        return valorContabil;
    }

    public void setValorContabil(double valorContabil) {
        this.valorContabil = valorContabil;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = valorIcms;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public double getValorIsento() {
        return valorIsento;
    }

    public void setValorIsento(double valorIsento) {
        this.valorIsento = valorIsento;
    }

    public double getValorOutrasDespesas() {
        return valorOutrasDespesas;
    }

    public void setValorOutrasDespesas(double valorOutrasDespesas) {
        this.valorOutrasDespesas = valorOutrasDespesas;
    }

    public String getInformacaoComplementar() {
        return informacaoComplementar;
    }

    public void setInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar = informacaoComplementar;
    }
}
