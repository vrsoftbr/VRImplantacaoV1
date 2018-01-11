package vrimplantacao.vo.notafiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class NotaEntradaVO implements Serializable {

    public long id = 0;
    public int idLoja = 0;
    public String loja = "";
    public int numeroNota = 0;
    public int idFornecedor = 0;
    public String fornecedor = "";
    public long cnpjDestinatario = 0;
    public String dataEntrada = "";
    public int idTipoEntrada = 0;
    public String tipoEntrada = "";
    public String dataEmissao = "";
    public String dataHoraLancamento = "";
    public double valorIpi = 0;
    public double valorFrete = 0;
    public double valorDesconto = 0;
    public double valorOutrasDespesas = 0;
    public double valorDespesaAdicional = 0;
    public double valorMercadoria = 0;
    public double valorTotal = 0;
    public double valorIcms = 0;
    public double valorIcmsSubstituicao = 0;
    public int idUsuario = 0;
    public String usuario = "";
    public boolean impressao = false;
    public boolean produtorRural = false;
    public int aplicaAliquota = -1;
    public boolean aplicaCustoDesconto = true;
    public boolean aplicaIcmsDesconto = true;
    public boolean aplicaCustoEncargo = true;
    public boolean aplicaIcmsEncargo = true;
    public boolean aplicaDespesaAdicional = false;
    public int idSituacaoNotaEntrada = 0;
    public String chaveNfe = "";
    public int idTipoFreteNotaFiscal = 0;
    public String situacaoNotaEntrada = "";
    public String serie = "";
    public String modelo = "";
    public double valorGuiaSubstituicao = 0;
    public double valorBaseCalculo = 0;
    public List<NotaEntradaVencimentoVO> vVencimento = new ArrayList();
    public List<NotaEntradaItemVO> vItem = new ArrayList();
    public int idEstado = 0;
    public double valorBaseSubstituicao = 0;
    public double valorFunRural = 0;
    public List<NotaEntradaItemDesmembramentoVO> vDesmembramento = new ArrayList();
    public int idTipoPagamento = 0;
    public double valorDescontoBoleto = 0;
    public List<NotaEntradaPedidoVO> vPedido = new ArrayList();
    public boolean conferido = false;
    public List<NotaEntradaNfeDivergenciaVO> vDivergenciaNfe = new ArrayList();
    public boolean divergenciaImposto = false;
    public boolean divergenciaProduto = false;
    public String observacao = "";
    public List<NotaEntradaBonificacaoVO> vBonificacao = new ArrayList();
    public long idNotaSaida = 0;
    public String dataHoraRecebimento = "";
    public String protocoloRecebimento = "";
    public int idTipoNota = 0;
    public boolean liberadoPedido = false;
    public List<NotaEntradaDivergenciaVO> vDivergenciaPedido = new ArrayList();
    public List<NotaEntradaDivergenciaVO> vDivergenciaColetor = new ArrayList();
    public String cfop = "";
    public int tipoEmissaoNfe = 0;
    public boolean importadoXml = false;
    public long idNotaSaidaProdutor = -1;
    public boolean aplicaIcmsIpi = false;

    public boolean isAplicaIcmsIpi() {
        return aplicaIcmsIpi;
    }

    public void setAplicaIcmsIpi(boolean aplicaIcmsIpi) {
        this.aplicaIcmsIpi = aplicaIcmsIpi;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public int getIdTipoEntrada() {
        return idTipoEntrada;
    }

    public void setIdTipoEntrada(int idTipoEntrada) {
        this.idTipoEntrada = idTipoEntrada;
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataHoraLancamento() {
        return dataHoraLancamento;
    }

    public void setDataHoraLancamento(String dataHoraLancamento) {
        this.dataHoraLancamento = dataHoraLancamento;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorOutraDespesa() {
        return valorOutrasDespesas;
    }

    public void setValorOutraDespesa(double valorOutraDespesa) {
        this.valorOutrasDespesas = valorOutraDespesa;
    }

    public double getValorDespesaAdicional() {
        return valorDespesaAdicional;
    }

    public void setValorDespesaAdicional(double valorDespesaAdicional) {
        this.valorDespesaAdicional = valorDespesaAdicional;
    }

    public double getValorMercadoria() {
        return valorMercadoria;
    }

    public void setValorMercadoria(double valorMercadoria) {
        this.valorMercadoria = valorMercadoria;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isImpressao() {
        return impressao;
    }

    public void setImpressao(boolean impressao) {
        this.impressao = impressao;
    }

    public boolean isProdutorRural() {
        return produtorRural;
    }

    public void setProdutorRural(boolean produtorRural) {
        this.produtorRural = produtorRural;
    }

    public int getAplicaAliquota() {
        return aplicaAliquota;
    }

    public void setAplicaAliquota(int aplicaAliquota) {
        this.aplicaAliquota = aplicaAliquota;
    }

    public boolean isAplicaCustoDesconto() {
        return aplicaCustoDesconto;
    }

    public void setAplicaCustoDesconto(boolean aplicaCustoDesconto) {
        this.aplicaCustoDesconto = aplicaCustoDesconto;
    }

    public boolean isAplicaIcmsDesconto() {
        return aplicaIcmsDesconto;
    }

    public void setAplicaIcmsDesconto(boolean aplicaIcmsDesconto) {
        this.aplicaIcmsDesconto = aplicaIcmsDesconto;
    }

    public boolean isAplicaCustoEncargo() {
        return aplicaCustoEncargo;
    }

    public void setAplicaCustoEncargo(boolean aplicaCustoEncargo) {
        this.aplicaCustoEncargo = aplicaCustoEncargo;
    }

    public boolean isAplicaIcmsEncargo() {
        return aplicaIcmsEncargo;
    }

    public void setAplicaIcmsEncargo(boolean aplicaIcmsEncargo) {
        this.aplicaIcmsEncargo = aplicaIcmsEncargo;
    }

    public boolean isAplicaDespesaAdicional() {
        return aplicaDespesaAdicional;
    }

    public void setAplicaDespesaAdicional(boolean aplicaDespesaAdicional) {
        this.aplicaDespesaAdicional = aplicaDespesaAdicional;
    }

    public int getIdSituacaoNotaEntrada() {
        return idSituacaoNotaEntrada;
    }

    public void setIdSituacaoNotaEntrada(int idSituacaoNotaEntrada) {
        this.idSituacaoNotaEntrada = idSituacaoNotaEntrada;
    }

    public String getChaveNfe() {
        return chaveNfe;
    }

    public void setChaveNfe(String chaveNfe) {
        this.chaveNfe = chaveNfe;
    }

    public String getSituacaoNotaEntrada() {
        return situacaoNotaEntrada;
    }

    public void setSituacaoNotaEntrada(String situacaoNotaEntrada) {
        this.situacaoNotaEntrada = situacaoNotaEntrada;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getValorGuiaSubstituicao() {
        return valorGuiaSubstituicao;
    }

    public void setValorGuiaSubstituicao(double valorGuiaSubstituicao) {
        this.valorGuiaSubstituicao = valorGuiaSubstituicao;
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public List<NotaEntradaVencimentoVO> getvVencimento() {
        return vVencimento;
    }

    public void setvVencimento(List<NotaEntradaVencimentoVO> vVencimento) {
        this.vVencimento = vVencimento;
    }

    public List<NotaEntradaItemVO> getvItem() {
        return vItem;
    }

    public void setvItem(List<NotaEntradaItemVO> vItem) {
        this.vItem = vItem;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public double getValorFunRural() {
        return valorFunRural;
    }

    public void setValorFunRural(double valorFunRural) {
        this.valorFunRural = valorFunRural;
    }

    public List<NotaEntradaItemDesmembramentoVO> getvDesmembramento() {
        return vDesmembramento;
    }

    public void setvDesmembramento(List<NotaEntradaItemDesmembramentoVO> vDesmembramento) {
        this.vDesmembramento = vDesmembramento;
    }

    public int getIdTipoPagamento() {
        return idTipoPagamento;
    }

    public void setIdTipoPagamento(int idTipoPagamento) {
        this.idTipoPagamento = idTipoPagamento;
    }

    public double getValorDescontoBoleto() {
        return valorDescontoBoleto;
    }

    public void setValorDescontoBoleto(double valorDescontoBoleto) {
        this.valorDescontoBoleto = valorDescontoBoleto;
    }

    public List<NotaEntradaPedidoVO> getvPedido() {
        return vPedido;
    }

    public void setvPedido(List<NotaEntradaPedidoVO> vPedido) {
        this.vPedido = vPedido;
    }

    public boolean isConferido() {
        return conferido;
    }

    public void setConferido(boolean conferido) {
        this.conferido = conferido;
    }

    public int getIdTipoFreteNotaFiscal() {
        return idTipoFreteNotaFiscal;
    }

    public void setIdTipoFreteNotaFiscal(int idTipoFreteNotaFiscal) {
        this.idTipoFreteNotaFiscal = idTipoFreteNotaFiscal;
    }

    public List<NotaEntradaNfeDivergenciaVO> getvDivergenciaNfe() {
        return vDivergenciaNfe;
    }

    public void setvDivergenciaNfe(List<NotaEntradaNfeDivergenciaVO> vDivergenciaNfe) {
        this.vDivergenciaNfe = vDivergenciaNfe;
    }

    public NotaEntradaVO getCopia() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bos);

        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

        return (NotaEntradaVO) in.readObject();
    }

    public String getDataHoraRecebimento() {
        return dataHoraRecebimento;
    }

    public void setDataHoraRecebimento(String dataHoraRecebimento) {
        this.dataHoraRecebimento = dataHoraRecebimento;
    }

    public String getProtocoloRecebimento() {
        return protocoloRecebimento;
    }

    public void setProtocoloRecebimento(String protocoloRecebimento) {
        this.protocoloRecebimento = protocoloRecebimento;
    }
}
