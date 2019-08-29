package vrimplantacao2.vo.cadastro.notafiscal;

import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class NotaEntradaItem {

    private int id;// integer NOT NULL DEFAULT nextval('notaentradaitem_id_seq'::regclass),
    private long idNotaEntrada;//id_notaentrada;// bigint NOT NULL,
    private int idProduto;//id_produto;// integer NOT NULL,
    private double quantidade = 0;// numeric(12,3) NOT NULL,
    private int qtdEmbalagem = 1;// integer NOT NULL,
    private double valor = 0;// numeric(13,4) NOT NULL,
    
    private double valorIpi = 0;// numeric(11,2) NOT NULL,
    private int idAliquota;//id_aliquota;// integer NOT NULL,
    private double custoComImposto = 0;// numeric(13,4) NOT NULL,
    
    private double valorBaseCalculo = 0;// numeric(11,2) NOT NULL,
    private double valorIcms = 0;// numeric(13,4) NOT NULL,
    private double valorIcmsSubstituicao = 0;// numeric(13,4) NOT NULL,
    private double custoComImpostoAnterior = 0;// numeric(13,4) NOT NULL,
    private double valorBonificacao = 0;// numeric(11,2) NOT NULL,
    private double valorVerba = 0;// numeric(11,2) NOT NULL,
    private double quantidadeDevolvida = 0;// numeric(12,3) NOT NULL,
    private double valorPisCofins = 0;// numeric(11,2) NOT NULL,
    private boolean contabilizaValor = true;// boolean NOT NULL,
    private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
    private double valorEmbalagem = 0;// numeric(13,4) NOT NULL,
    private String cfop;// character varying(5),
    private double valorIcmsSubstituicaoXml = 0;// numeric(11,2) NOT NULL,
    private double valorIsento = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorOutras = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private int situacaoTributaria = 0;// integer NOT NULL DEFAULT 0,
    private double valorFrete = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorOutrasDespesas = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private int idTipoPisCofins = 0;//id_tipopiscofins;// integer NOT NULL DEFAULT 0,
    private int idAliquotaCreditoForaEstado = 0;//id_aliquotacreditoforaestado;// integer NOT NULL,
    private int idAliquotaPautaFiscal = -1;//id_aliquotapautafiscal = -1;// integer,
    private int idTipoEntrada = -1;//id_tipoentrada = -1;// integer,
    private double valorOutrasSubstituicao = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double quantidadeBonificacao = 0;// numeric(11,2),
    private double valorSubstituicaoEstadual = 0;// numeric(11,2) DEFAULT 0,
    private String descricaoXml;// character varying(120),
    private double valorDespesaFrete = 0;// numeric(11,4),
    private String cfopNota;// character varying(5),
    private double valorBaseFcp = 0;// numeric(11,2),
    private double valorFcp = 0;// numeric(11,2),
    private double valorBaseFcpSt = 0;// numeric(11,2),
    private double valorFcpSt = 0;// numeric(11,2),
    private double valorIcmsDesonerado = 0;// numeric(11,2),
    private int idMotivoDesoneracao = -1;// integer,
    private double valorBaseCalculoIcmsDesonerado = 0;// numeric(11,2),
    private double valorIcmsDiferido = 0;// numeric(11,2)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdNotaEntrada() {
        return idNotaEntrada;
    }

    public void setIdNotaEntrada(long idNotaEntrada) {
        this.idNotaEntrada = idNotaEntrada;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = MathUtils.round(quantidade, 3, 99999999.99D);
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 4, 99999999.99D);
    }

    public double getValorTotal() {
        return MathUtils.round((this.quantidade * this.qtdEmbalagem) * this.valor, 2, 99999999.99D);
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = MathUtils.round(valorIpi, 2, 99999999.99D);
    }

    public int getIdAliquota() {
        return idAliquota;
    }

    public void setIdAliquota(int idAliquota) {
        this.idAliquota = idAliquota;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = MathUtils.round(custoComImposto, 4, 99999999.99D);
    }

    public double getValorTotalFinal() {
        return getValorTotal();
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = MathUtils.round(valorBaseCalculo, 2, 99999999.99D);
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = MathUtils.round(valorIcms, 4, 99999999.99D);
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = MathUtils.round(valorIcmsSubstituicao, 4, 99999999.99D);
    }

    public double getCustoComImpostoAnterior() {
        return custoComImpostoAnterior;
    }

    public void setCustoComImpostoAnterior(double custoComImpostoAnterior) {
        this.custoComImpostoAnterior = MathUtils.round(custoComImpostoAnterior, 4, 99999999.99D);
    }

    public double getValorBonificacao() {
        return valorBonificacao;
    }

    public void setValorBonificacao(double valorBonificacao) {
        this.valorBonificacao = MathUtils.round(valorBonificacao, 2, 99999999.99D);
    }

    public double getValorVerba() {
        return valorVerba;
    }

    public void setValorVerba(double valorVerba) {
        this.valorVerba = MathUtils.round(valorVerba, 2, 99999999.99D);
    }

    public double getQuantidadeDevolvida() {
        return quantidadeDevolvida;
    }

    public void setQuantidadeDevolvida(double quantidadeDevolvida) {
        this.quantidadeDevolvida = MathUtils.round(quantidadeDevolvida, 3, 99999999.99D);
    }

    public double getValorPisCofins() {
        return valorPisCofins;
    }

    public void setValorPisCofins(double valorPisCofins) {
        this.valorPisCofins = MathUtils.round(valorPisCofins, 2, 99999999.99D);
    }

    public boolean isContabilizaValor() {
        return contabilizaValor;
    }

    public void setContabilizaValor(boolean contabilizaValor) {
        this.contabilizaValor = contabilizaValor;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = MathUtils.round(valorBaseSubstituicao, 2, 99999999.99D);
    }

    public double getValorEmbalagem() {
        return valorEmbalagem;
    }

    public void setValorEmbalagem(double valorEmbalagem) {
        this.valorEmbalagem = MathUtils.round(valorEmbalagem, 4, 99999999.99D);
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = String.format("%,d", Utils.stringToInt(cfop));
    }

    public double getValorIcmsSubstituicaoXml() {
        return valorIcmsSubstituicaoXml;
    }

    public void setValorIcmsSubstituicaoXml(double valorIcmsSubstituicaoXml) {
        this.valorIcmsSubstituicaoXml = MathUtils.round(valorIcmsSubstituicaoXml, 2, 99999999.99D);
    }

    public double getValorIsento() {
        return valorIsento;
    }

    public void setValorIsento(double valorIsento) {
        this.valorIsento = MathUtils.round(valorIsento, 2, 99999999.99D);
    }

    public double getValorOutras() {
        return valorOutras;
    }

    public void setValorOutras(double valorOutras) {
        this.valorOutras = MathUtils.round(valorOutras, 2, 99999999.99D);
    }

    public int getSituacaoTributaria() {
        return situacaoTributaria;
    }

    public void setSituacaoTributaria(int situacaoTributaria) {
        this.situacaoTributaria = situacaoTributaria;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = MathUtils.round(valorFrete, 2, 99999999.99D);
    }

    public double getValorOutrasDespesas() {
        return valorOutrasDespesas;
    }

    public void setValorOutrasDespesas(double valorOutrasDespesas) {
        this.valorOutrasDespesas = MathUtils.round(valorOutrasDespesas, 2, 99999999.99D);
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = MathUtils.round(valorDesconto, 2, 99999999.99D);
    }

    public int getIdTipoPisCofins() {
        return idTipoPisCofins;
    }

    public void setIdTipoPisCofins(int idTipoPisCofins) {
        this.idTipoPisCofins = idTipoPisCofins;
    }

    public int getIdAliquotaCreditoForaEstado() {
        return idAliquotaCreditoForaEstado;
    }

    public void setIdAliquotaCreditoForaEstado(int idAliquotaCreditoForaEstado) {
        this.idAliquotaCreditoForaEstado = idAliquotaCreditoForaEstado;
    }

    public int getIdAliquotaPautaFiscal() {
        return idAliquotaPautaFiscal;
    }

    public void setIdAliquotaPautaFiscal(int idAliquotaPautaFiscal) {
        this.idAliquotaPautaFiscal = idAliquotaPautaFiscal;
    }

    public int getIdTipoEntrada() {
        return idTipoEntrada;
    }

    public void setIdTipoEntrada(int idTipoEntrada) {
        this.idTipoEntrada = idTipoEntrada;
    }

    public double getValorOutrasSubstituicao() {
        return valorOutrasSubstituicao;
    }

    public void setValorOutrasSubstituicao(double valorOutrasSubstituicao) {
        this.valorOutrasSubstituicao = MathUtils.round(valorOutrasSubstituicao, 2, 99999999.99D);
    }

    public double getQuantidadeBonificacao() {
        return quantidadeBonificacao;
    }

    public void setQuantidadeBonificacao(double quantidadeBonificacao) {
        this.quantidadeBonificacao = MathUtils.round(quantidadeBonificacao, 2, 99999999.99D);
    }

    public double getValorSubstituicaoEstadual() {
        return valorSubstituicaoEstadual;
    }

    public void setValorSubstituicaoEstadual(double valorSubstituicaoEstadual) {
        this.valorSubstituicaoEstadual = MathUtils.round(valorSubstituicaoEstadual, 2, 99999999.99D);
    }

    public String getDescricaoXml() {
        return descricaoXml;
    }

    public void setDescricaoXml(String descricaoXml) {
        this.descricaoXml = descricaoXml;
    }

    public double getValorDespesaFrete() {
        return valorDespesaFrete;
    }

    public void setValorDespesaFrete(double valorDespesaFrete) {
        this.valorDespesaFrete = MathUtils.round(valorDespesaFrete, 4, 99999999.99D);
    }

    public String getCfopNota() {
        return cfopNota;
    }

    public void setCfopNota(String cfopNota) {
        this.cfopNota = String.format("%,d", Utils.stringToInt(cfopNota));
    }

    public double getValorBaseFcp() {
        return valorBaseFcp;
    }

    public void setValorBaseFcp(double valorBaseFcp) {
        this.valorBaseFcp = MathUtils.round(valorBaseFcp, 2, 99999999.99D);
    }

    public double getValorFcp() {
        return valorFcp;
    }

    public void setValorFcp(double valorFcp) {
        this.valorFcp = MathUtils.round(valorFcp, 2, 99999999.99D);
    }

    public double getValorBaseFcpSt() {
        return valorBaseFcpSt;
    }

    public void setValorBaseFcpSt(double valorBaseFcpSt) {
        this.valorBaseFcpSt = MathUtils.round(valorBaseFcpSt, 2, 99999999.99D);
    }

    public double getValorFcpSt() {
        return valorFcpSt;
    }

    public void setValorFcpSt(double valorFcpSt) {
        this.valorFcpSt = MathUtils.round(valorFcpSt, 2, 99999999.99D);
    }

    public double getValorIcmsDesonerado() {
        return valorIcmsDesonerado;
    }

    public void setValorIcmsDesonerado(double valorIcmsDesonerado) {
        this.valorIcmsDesonerado = MathUtils.round(valorIcmsDesonerado, 2, 99999999.99D);
    }

    public int getIdMotivoDesoneracao() {
        return idMotivoDesoneracao;
    }

    public void setIdMotivoDesoneracao(int idMotivoDesoneracao) {
        this.idMotivoDesoneracao = idMotivoDesoneracao;
    }

    public double getValorBaseCalculoIcmsDesonerado() {
        return valorBaseCalculoIcmsDesonerado;
    }

    public void setValorBaseCalculoIcmsDesonerado(double valorBaseCalculoIcmsDesonerado) {
        this.valorBaseCalculoIcmsDesonerado = MathUtils.round(valorBaseCalculoIcmsDesonerado, 2, 99999999.99D);
    }

    public double getValorIcmsDiferido() {
        return valorIcmsDiferido;
    }

    public void setValorIcmsDiferido(double valorIcmsDiferido) {
        this.valorIcmsDiferido = MathUtils.round(valorIcmsDiferido, 2, 99999999.99D);
    }

}
