package vrimplantacao2_5.Financeiro.VO;

public class TipoSaidaVO {

    private Integer id;
    private String descricao;
    private Integer id_situacaoCadastro;

    private boolean baixaEstoque;
    private boolean geraDevolucao;
    private String especie;
    private boolean transportadorProprio;
    private boolean destinatarioCliente;
    private boolean subsituicao;
    private boolean foraEstado;
    private boolean consultaPedido;
    private boolean imprimeBoleto;
    private boolean atualizaEscrita;
    private boolean utilizaIcmsCredito;
    private boolean naoCreditaIcms;
    private boolean desabilitaValor;
    private boolean adicionaVenda;
    private boolean geraReceber;
    private boolean utilizaPrecoVenda;
    private boolean notaProdutor;
    private boolean transferencia;
    private Integer id_tipoEntrada;
    private String tipo;
    private boolean calculaIva;
    private boolean UtilizaIcmsEntrada;
    private Integer id_contaContabilFiscalCredito;
    private Integer id_contaContabilFiscalDebito;
    private Integer id_historicoPadrao;
    private boolean entraEstoque;
    private boolean vendaIndustria;
    private Integer id_notaSaidaMensagem;
    private boolean geraContrato;
    private boolean contabilidadePadrao;
    private boolean contabiliza;
    private boolean atualizaTroca;
    private boolean creditaPisCofins;
    private boolean consumidorFinal;
    private Integer id_tipoPisCofins;
    private Integer id_aliquota;
    private boolean fabricacaoPropria;
    private boolean utilizaPrecoCusto;
    private boolean converterTodasAliquotas;
    private Integer id_tipoSaida;
    private boolean geraExportacao;
    private Integer id_produto;
    private boolean utilizaTributoCadastroDebito;
    private boolean converterAliquota;
    private Integer planoConta1;
    private Integer planoConta2;
    private boolean notaMei;
    private boolean utilizaCustoMedio;

   

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the baixaEstoque
     */
    public boolean isBaixaEstoque() {
        return baixaEstoque;
    }

    /**
     * @param baixaEstoque the baixaEstoque to set
     */
    public void setBaixaEstoque(boolean baixaEstoque) {
        this.baixaEstoque = baixaEstoque;
    }

    /**
     * @return the geraDevolucao
     */
    public boolean isGeraDevolucao() {
        return geraDevolucao;
    }

    /**
     * @param geraDevolucao the geraDevolucao to set
     */
    public void setGeraDevolucao(boolean geraDevolucao) {
        this.geraDevolucao = geraDevolucao;
    }

    /**
     * @return the especie
     */
    public String getEspecie() {
        return especie;
    }

    /**
     * @param especie the especie to set
     */
    public void setEspecie(String especie) {
        this.especie = especie;
    }

    /**
     * @return the transportadorProprio
     */
    public boolean isTransportadorProprio() {
        return transportadorProprio;
    }

    /**
     * @param transportadorProprio the transportadorProprio to set
     */
    public void setTransportadorProprio(boolean transportadorProprio) {
        this.transportadorProprio = transportadorProprio;
    }

    /**
     * @return the destinatarioCliente
     */
    public boolean isDestinatarioCliente() {
        return destinatarioCliente;
    }

    /**
     * @param destinatarioCliente the destinatarioCliente to set
     */
    public void setDestinatarioCliente(boolean destinatarioCliente) {
        this.destinatarioCliente = destinatarioCliente;
    }

    /**
     * @return the subsituicao
     */
    public boolean isSubsituicao() {
        return subsituicao;
    }

    /**
     * @param subsituicao the subsituicao to set
     */
    public void setSubsituicao(boolean subsituicao) {
        this.subsituicao = subsituicao;
    }

    /**
     * @return the foraEstado
     */
    public boolean isForaEstado() {
        return foraEstado;
    }

    /**
     * @param foraEstado the foraEstado to set
     */
    public void setForaEstado(boolean foraEstado) {
        this.foraEstado = foraEstado;
    }

    /**
     * @return the consultaPedido
     */
    public boolean isConsultaPedido() {
        return consultaPedido;
    }

    /**
     * @param consultaPedido the consultaPedido to set
     */
    public void setConsultaPedido(boolean consultaPedido) {
        this.consultaPedido = consultaPedido;
    }

    /**
     * @return the imprimeBoleto
     */
    public boolean isImprimeBoleto() {
        return imprimeBoleto;
    }

    /**
     * @param imprimeBoleto the imprimeBoleto to set
     */
    public void setImprimeBoleto(boolean imprimeBoleto) {
        this.imprimeBoleto = imprimeBoleto;
    }

    /**
     * @return the atualizaEscrita
     */
    public boolean isAtualizaEscrita() {
        return atualizaEscrita;
    }

    /**
     * @param atualizaEscrita the atualizaEscrita to set
     */
    public void setAtualizaEscrita(boolean atualizaEscrita) {
        this.atualizaEscrita = atualizaEscrita;
    }

    /**
     * @return the utilizaIcmsCredito
     */
    public boolean isUtilizaIcmsCredito() {
        return utilizaIcmsCredito;
    }

    /**
     * @param utilizaIcmsCredito the utilizaIcmsCredito to set
     */
    public void setUtilizaIcmsCredito(boolean utilizaIcmsCredito) {
        this.utilizaIcmsCredito = utilizaIcmsCredito;
    }

    /**
     * @return the naoCreditaIcms
     */
    public boolean isNaoCreditaIcms() {
        return naoCreditaIcms;
    }

    /**
     * @param naoCreditaIcms the naoCreditaIcms to set
     */
    public void setNaoCreditaIcms(boolean naoCreditaIcms) {
        this.naoCreditaIcms = naoCreditaIcms;
    }

    /**
     * @return the desabilitaValor
     */
    public boolean isDesabilitaValor() {
        return desabilitaValor;
    }

    /**
     * @param desabilitaValor the desabilitaValor to set
     */
    public void setDesabilitaValor(boolean desabilitaValor) {
        this.desabilitaValor = desabilitaValor;
    }

    /**
     * @return the adicionaVenda
     */
    public boolean isAdicionaVenda() {
        return adicionaVenda;
    }

    /**
     * @param adicionaVenda the adicionaVenda to set
     */
    public void setAdicionaVenda(boolean adicionaVenda) {
        this.adicionaVenda = adicionaVenda;
    }

    /**
     * @return the geraReceber
     */
    public boolean isGeraReceber() {
        return geraReceber;
    }

    /**
     * @param geraReceber the geraReceber to set
     */
    public void setGeraReceber(boolean geraReceber) {
        this.geraReceber = geraReceber;
    }

    /**
     * @return the utilizaPrecoVenda
     */
    public boolean isUtilizaPrecoVenda() {
        return utilizaPrecoVenda;
    }

    /**
     * @param utilizaPrecoVenda the utilizaPrecoVenda to set
     */
    public void setUtilizaPrecoVenda(boolean utilizaPrecoVenda) {
        this.utilizaPrecoVenda = utilizaPrecoVenda;
    }

    /**
     * @return the notaProdutor
     */
    public boolean isNotaProdutor() {
        return notaProdutor;
    }

    /**
     * @param notaProdutor the notaProdutor to set
     */
    public void setNotaProdutor(boolean notaProdutor) {
        this.notaProdutor = notaProdutor;
    }

    /**
     * @return the transferencia
     */
    public boolean isTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the transferencia to set
     */
    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    /**
     * @return the id_tipoEntrada
     */
    public Integer getId_tipoEntrada() {
        return id_tipoEntrada;
    }

    /**
     * @param id_tipoEntrada the id_tipoEntrada to set
     */
    public void setId_tipoEntrada(Integer id_tipoEntrada) {
        this.id_tipoEntrada = id_tipoEntrada;
    }

    /**
     * @return the calculaIva
     */
    public boolean isCalculaIva() {
        return calculaIva;
    }

    /**
     * @param calculaIva the calculaIva to set
     */
    public void setCalculaIva(boolean calculaIva) {
        this.calculaIva = calculaIva;
    }

    /**
     * @return the UtilizaIcmsEntrada
     */
    public boolean isUtilizaIcmsEntrada() {
        return UtilizaIcmsEntrada;
    }

    /**
     * @param UtilizaIcmsEntrada the UtilizaIcmsEntrada to set
     */
    public void setUtilizaIcmsEntrada(boolean UtilizaIcmsEntrada) {
        this.UtilizaIcmsEntrada = UtilizaIcmsEntrada;
    }

    /**
     * @return the id_contaContabilFiscalCredito
     */
    public Integer getId_contaContabilFiscalCredito() {
        return id_contaContabilFiscalCredito;
    }

    /**
     * @param id_contaContabilFiscalCredito the id_contaContabilFiscalCredito to set
     */
    public void setId_contaContabilFiscalCredito(Integer id_contaContabilFiscalCredito) {
        this.id_contaContabilFiscalCredito = id_contaContabilFiscalCredito;
    }

    /**
     * @return the id_contaContabilFiscalDebito
     */
    public Integer getId_contaContabilFiscalDebito() {
        return id_contaContabilFiscalDebito;
    }

    /**
     * @param id_contaContabilFiscalDebito the id_contaContabilFiscalDebito to set
     */
    public void setId_contaContabilFiscalDebito(Integer id_contaContabilFiscalDebito) {
        this.id_contaContabilFiscalDebito = id_contaContabilFiscalDebito;
    }

    /**
     * @return the id_historicoPadrao
     */
    public Integer getId_historicoPadrao() {
        return id_historicoPadrao;
    }

    /**
     * @param id_historicoPadrao the id_historicoPadrao to set
     */
    public void setId_historicoPadrao(Integer id_historicoPadrao) {
        this.id_historicoPadrao = id_historicoPadrao;
    }

    /**
     * @return the entraEstoque
     */
    public boolean isEntraEstoque() {
        return entraEstoque;
    }

    /**
     * @param entraEstoque the entraEstoque to set
     */
    public void setEntraEstoque(boolean entraEstoque) {
        this.entraEstoque = entraEstoque;
    }

    /**
     * @return the vendaIndustria
     */
    public boolean isVendaIndustria() {
        return vendaIndustria;
    }

    /**
     * @param vendaIndustria the vendaIndustria to set
     */
    public void setVendaIndustria(boolean vendaIndustria) {
        this.vendaIndustria = vendaIndustria;
    }

    /**
     * @return the id_notaSaidaMensagem
     */
    public Integer getId_notaSaidaMensagem() {
        return id_notaSaidaMensagem;
    }

    /**
     * @param id_notaSaidaMensagem the id_notaSaidaMensagem to set
     */
    public void setId_notaSaidaMensagem(Integer id_notaSaidaMensagem) {
        this.id_notaSaidaMensagem = id_notaSaidaMensagem;
    }

    /**
     * @return the geraContrato
     */
    public boolean isGeraContrato() {
        return geraContrato;
    }

    /**
     * @param geraContrato the geraContrato to set
     */
    public void setGeraContrato(boolean geraContrato) {
        this.geraContrato = geraContrato;
    }

    /**
     * @return the contabilidadePadrao
     */
    public boolean isContabilidadePadrao() {
        return contabilidadePadrao;
    }

    /**
     * @param contabilidadePadrao the contabilidadePadrao to set
     */
    public void setContabilidadePadrao(boolean contabilidadePadrao) {
        this.contabilidadePadrao = contabilidadePadrao;
    }

    /**
     * @return the contabiliza
     */
    public boolean isContabiliza() {
        return contabiliza;
    }

    /**
     * @param contabiliza the contabiliza to set
     */
    public void setContabiliza(boolean contabiliza) {
        this.contabiliza = contabiliza;
    }

    /**
     * @return the atualizaTroca
     */
    public boolean isAtualizaTroca() {
        return atualizaTroca;
    }

    /**
     * @param atualizaTroca the atualizaTroca to set
     */
    public void setAtualizaTroca(boolean atualizaTroca) {
        this.atualizaTroca = atualizaTroca;
    }

    /**
     * @return the creditaPisCofins
     */
    public boolean isCreditaPisCofins() {
        return creditaPisCofins;
    }

    /**
     * @param creditaPisCofins the creditaPisCofins to set
     */
    public void setCreditaPisCofins(boolean creditaPisCofins) {
        this.creditaPisCofins = creditaPisCofins;
    }

    /**
     * @return the consumidorFinal
     */
    public boolean isConsumidorFinal() {
        return consumidorFinal;
    }

    /**
     * @param consumidorFinal the consumidorFinal to set
     */
    public void setConsumidorFinal(boolean consumidorFinal) {
        this.consumidorFinal = consumidorFinal;
    }

    /**
     * @return the id_tipoPisCofins
     */
    public Integer getId_tipoPisCofins() {
        return id_tipoPisCofins;
    }

    /**
     * @param id_tipoPisCofins the id_tipoPisCofins to set
     */
    public void setId_tipoPisCofins(Integer id_tipoPisCofins) {
        this.id_tipoPisCofins = id_tipoPisCofins;
    }

    /**
     * @return the id_aliquota
     */
    public Integer getId_aliquota() {
        return id_aliquota;
    }

    /**
     * @param id_aliquota the id_aliquota to set
     */
    public void setId_aliquota(Integer id_aliquota) {
        this.id_aliquota = id_aliquota;
    }

    /**
     * @return the fabricacaoPropria
     */
    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    /**
     * @param fabricacaoPropria the fabricacaoPropria to set
     */
    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }

    /**
     * @return the utilizaPrecoCusto
     */
    public boolean isUtilizaPrecoCusto() {
        return utilizaPrecoCusto;
    }

    /**
     * @param utilizaPrecoCusto the utilizaPrecoCusto to set
     */
    public void setUtilizaPrecoCusto(boolean utilizaPrecoCusto) {
        this.utilizaPrecoCusto = utilizaPrecoCusto;
    }

    /**
     * @return the converterTodasAliquotas
     */
    public boolean isConverterTodasAliquotas() {
        return converterTodasAliquotas;
    }

    /**
     * @param converterTodasAliquotas the converterTodasAliquotas to set
     */
    public void setConverterTodasAliquotas(boolean converterTodasAliquotas) {
        this.converterTodasAliquotas = converterTodasAliquotas;
    }

    /**
     * @return the id_tipoSaida
     */
    public Integer getId_tipoSaida() {
        return id_tipoSaida;
    }

    /**
     * @param id_tipoSaida the id_tipoSaida to set
     */
    public void setId_tipoSaida(Integer id_tipoSaida) {
        this.id_tipoSaida = id_tipoSaida;
    }

    /**
     * @return the geraExportacao
     */
    public boolean isGeraExportacao() {
        return geraExportacao;
    }

    /**
     * @param geraExportacao the geraExportacao to set
     */
    public void setGeraExportacao(boolean geraExportacao) {
        this.geraExportacao = geraExportacao;
    }

    /**
     * @return the id_produto
     */
    public Integer getId_produto() {
        return id_produto;
    }

    /**
     * @param id_produto the id_produto to set
     */
    public void setId_produto(Integer id_produto) {
        this.id_produto = id_produto;
    }

    /**
     * @return the utilizaTributoCadastroDebito
     */
    public boolean isUtilizaTributoCadastroDebito() {
        return utilizaTributoCadastroDebito;
    }

    /**
     * @param utilizaTributoCadastroDebito the utilizaTributoCadastroDebito to set
     */
    public void setUtilizaTributoCadastroDebito(boolean utilizaTributoCadastroDebito) {
        this.utilizaTributoCadastroDebito = utilizaTributoCadastroDebito;
    }

    /**
     * @return the converterAliquota
     */
    public boolean isConverterAliquota() {
        return converterAliquota;
    }

    /**
     * @param converterAliquota the converterAliquota to set
     */
    public void setConverterAliquota(boolean converterAliquota) {
        this.converterAliquota = converterAliquota;
    }

    /**
     * @return the planoConta1
     */
    public Integer getPlanoConta1() {
        return planoConta1;
    }

    /**
     * @param planoConta1 the planoConta1 to set
     */
    public void setPlanoConta1(Integer planoConta1) {
        this.planoConta1 = planoConta1;
    }

    /**
     * @return the planoConta2
     */
    public Integer getPlanoConta2() {
        return planoConta2;
    }

    /**
     * @param planoConta2 the planoConta2 to set
     */
    public void setPlanoConta2(Integer planoConta2) {
        this.planoConta2 = planoConta2;
    }

    /**
     * @return the notaMei
     */
    public boolean isNotaMei() {
        return notaMei;
    }

    /**
     * @param notaMei the notaMei to set
     */
    public void setNotaMei(boolean notaMei) {
        this.notaMei = notaMei;
    }

    /**
     * @return the utilizaCustoMedio
     */
    public boolean isUtilizaCustoMedio() {
        return utilizaCustoMedio;
    }

    /**
     * @param utilizaCustoMedio the utilizaCustoMedio to set
     */
    public void setUtilizaCustoMedio(boolean utilizaCustoMedio) {
        this.utilizaCustoMedio = utilizaCustoMedio;
    }

    /**
     * @return the id_situacaoCadastro
     */
    public Integer getId_situacaoCadastro() {
        return id_situacaoCadastro;
    }

    /**
     * @param id_situacaoCadastro the id_situacaoCadastro to set
     */
    public void setId_situacaoCadastro(Integer id_situacaoCadastro) {
        this.id_situacaoCadastro = id_situacaoCadastro;
    }

   
}
