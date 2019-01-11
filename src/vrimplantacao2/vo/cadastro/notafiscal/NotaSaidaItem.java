package vrimplantacao2.vo.cadastro.notafiscal;

import java.sql.Timestamp;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class NotaSaidaItem {
    
    private int id;// integer NOT NULL DEFAULT nextval('notasaidaitem_id_seq'::regclass),
    private long idNotaSaida;//id_notasaida;// bigint NOT NULL,
    private int idProduto;//id_produto;// integer NOT NULL,
    private double quantidade = 0;// numeric(12,3) NOT NULL,
    private int qtdEmbalagem = 1;// integer NOT NULL,
    private double valor = 0;// numeric(13,4) NOT NULL,
    //private double valorTotal = 0;// numeric(11,2) NOT NULL,
    private double valorIpi = 0;// numeric(13,4) NOT NULL,
    private int idAliquota;//id_aliquota;// integer NOT NULL,
    private double valorBaseCalculo = 0;// numeric(11,2) NOT NULL,
    private double valorIcms = 0;// numeric(13,4) NOT NULL,
    private double valorBaseSubstituicao = 0;// numeric(11,2) NOT NULL,
    private double valorIcmsSubstituicao = 0;// numeric(13,4) NOT NULL,
    private double valorPisCofins = 0;// numeric(11,2) NOT NULL,
    private double valorBaseIpi = 0;// numeric(11,2) NOT NULL,
    private String cfop;// character varying(5),
    private int tipoIva = 0;// integer NOT NULL DEFAULT 0,
    private int idAliquotaPautaFiscal = -1;//id_aliquotapautafiscal;// integer,
    private double valorDesconto = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorIsento = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private double valorOutras = 0;// numeric(11,2) NOT NULL DEFAULT 0,
    private int situacaoTributaria = 0;// integer NOT NULL DEFAULT 0,
    private double valorIcmsDispensado = 0;// numeric(12,3) NOT NULL DEFAULT 0,
    private int idAliquotaDispensado = -1;//id_aliquotadispensado;// integer,
    private int tipoNaturezaReceita = -1;// integer,
    private Timestamp dataDesembaraco;// timestamp without time zone,
    private int idEstadoDesembaraco = -1;//id_estadodesembaraco;// integer,
    private int numeroAdicao = 0;// integer NOT NULL DEFAULT 0,
    private String localDesembaraco = "";// character varying(50) NOT NULL DEFAULT ''::character varying,
    private int idTipoSaida = -1;//id_tiposaida;// integer,
    private int idAliquotaInterestadual = -1;//id_aliquotainterestadual;// integer,
    private int idAliquotaDestino = -1;//id_aliquotadestino;// integer,
    private int idTipoOrigemApuracao = -1;//id_tipoorigemapuracao;// integer,
    private double valorBaseFcp = 0;// numeric(11,2),
    private double valorFcp = 0;// numeric(11,2),
    private double valorBaseFcpSt = 0;// numeric(11,2),
    private double valorFcpSt = 0;// numeric(11,2),
    private double valorIcmsDesonerado = 0;// numeric(11,2),
    private int idMotivoDesoneracao = -1;// integer,
    private double valorBaseCalculoIcmsDesonerado = 0;// numeric(11,2),
    private int idEscritaFundamento = -1;//id_escritafundamento;// integer,
    private int idEscritaCodigoAjuste = -1;//id_escritacodigoajuste;// integer,
    private double valorIcmsDiferido = 0;// numeric(11,2)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdNotaSaida() {
        return idNotaSaida;
    }

    public void setIdNotaSaida(long idNotaSaida) {
        this.idNotaSaida = idNotaSaida;
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
        this.quantidade = quantidade;
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
        this.valor = valor;
    }

    public double getValorTotal() {
        return MathUtils.round((this.quantidade * this.qtdEmbalagem) * this.valor, 2);
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public int getIdAliquota() {
        return idAliquota;
    }

    public void setIdAliquota(int idAliquota) {
        this.idAliquota = idAliquota;
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = valorIcms;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public double getValorPisCofins() {
        return valorPisCofins;
    }

    public void setValorPisCofins(double valorPisCofins) {
        this.valorPisCofins = valorPisCofins;
    }

    public double getValorBaseIpi() {
        return valorBaseIpi;
    }

    public void setValorBaseIpi(double valorBaseIpi) {
        this.valorBaseIpi = valorBaseIpi;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public int getTipoIva() {
        return tipoIva;
    }

    public void setTipoIva(int tipoIva) {
        this.tipoIva = tipoIva;
    }

    public int getIdAliquotaPautaFiscal() {
        return idAliquotaPautaFiscal;
    }

    public void setIdAliquotaPautaFiscal(int idAliquotaPautaFiscal) {
        this.idAliquotaPautaFiscal = idAliquotaPautaFiscal;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorIsento() {
        return valorIsento;
    }

    public void setValorIsento(double valorIsento) {
        this.valorIsento = valorIsento;
    }

    public double getValorOutras() {
        return valorOutras;
    }

    public void setValorOutras(double valorOutras) {
        this.valorOutras = valorOutras;
    }

    public int getSituacaoTributaria() {
        return situacaoTributaria;
    }

    public void setSituacaoTributaria(int situacaoTributaria) {
        this.situacaoTributaria = situacaoTributaria;
    }

    public double getValorIcmsDispensado() {
        return valorIcmsDispensado;
    }

    public void setValorIcmsDispensado(double valorIcmsDispensado) {
        this.valorIcmsDispensado = valorIcmsDispensado;
    }

    public int getIdAliquotaDispensado() {
        return idAliquotaDispensado;
    }

    public void setIdAliquotaDispensado(int idAliquotaDispensado) {
        this.idAliquotaDispensado = idAliquotaDispensado;
    }

    public int getTipoNaturezaReceita() {
        return tipoNaturezaReceita;
    }

    public void setTipoNaturezaReceita(int tipoNaturezaReceita) {
        this.tipoNaturezaReceita = tipoNaturezaReceita;
    }

    public Timestamp getDataDesembaraco() {
        return dataDesembaraco;
    }

    public void setDataDesembaraco(Timestamp dataDesembaraco) {
        this.dataDesembaraco = dataDesembaraco;
    }

    public int getIdEstadoDesembaraco() {
        return idEstadoDesembaraco;
    }

    public void setIdEstadoDesembaraco(int idEstadoDesembaraco) {
        this.idEstadoDesembaraco = idEstadoDesembaraco;
    }

    public int getNumeroAdicao() {
        return numeroAdicao;
    }

    public void setNumeroAdicao(int numeroAdicao) {
        this.numeroAdicao = numeroAdicao;
    }

    public String getLocalDesembaraco() {
        return localDesembaraco;
    }

    public void setLocalDesembaraco(String localDesembaraco) {
        this.localDesembaraco = localDesembaraco;
    }

    public int getIdTipoSaida() {
        return idTipoSaida;
    }

    public void setIdTipoSaida(int idTipoSaida) {
        this.idTipoSaida = idTipoSaida;
    }

    public int getIdAliquotaInterestadual() {
        return idAliquotaInterestadual;
    }

    public void setIdAliquotaInterestadual(int idAliquotaInterestadual) {
        this.idAliquotaInterestadual = idAliquotaInterestadual;
    }

    public int getIdAliquotaDestino() {
        return idAliquotaDestino;
    }

    public void setIdAliquotaDestino(int idAliquotaDestino) {
        this.idAliquotaDestino = idAliquotaDestino;
    }

    public int getIdTipoOrigemApuracao() {
        return idTipoOrigemApuracao;
    }

    public void setIdTipoOrigemApuracao(int idTipoOrigemApuracao) {
        this.idTipoOrigemApuracao = idTipoOrigemApuracao;
    }

    public double getValorBaseFcp() {
        return valorBaseFcp;
    }

    public void setValorBaseFcp(double valorBaseFcp) {
        this.valorBaseFcp = valorBaseFcp;
    }

    public double getValorFcp() {
        return valorFcp;
    }

    public void setValorFcp(double valorFcp) {
        this.valorFcp = valorFcp;
    }

    public double getValorBaseFcpSt() {
        return valorBaseFcpSt;
    }

    public void setValorBaseFcpSt(double valorBaseFcpSt) {
        this.valorBaseFcpSt = valorBaseFcpSt;
    }

    public double getValorFcpSt() {
        return valorFcpSt;
    }

    public void setValorFcpSt(double valorFcpSt) {
        this.valorFcpSt = valorFcpSt;
    }

    public double getValorIcmsDesonerado() {
        return valorIcmsDesonerado;
    }

    public void setValorIcmsDesonerado(double valorIcmsDesonerado) {
        this.valorIcmsDesonerado = valorIcmsDesonerado;
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
        this.valorBaseCalculoIcmsDesonerado = valorBaseCalculoIcmsDesonerado;
    }

    public int getIdEscritaFundamento() {
        return idEscritaFundamento;
    }

    public void setIdEscritaFundamento(int idEscritaFundamento) {
        this.idEscritaFundamento = idEscritaFundamento;
    }

    public int getIdEscritaCodigoAjuste() {
        return idEscritaCodigoAjuste;
    }

    public void setIdEscritaCodigoAjuste(int idEscritaCodigoAjuste) {
        this.idEscritaCodigoAjuste = idEscritaCodigoAjuste;
    }

    public double getValorIcmsDiferido() {
        return valorIcmsDiferido;
    }

    public void setValorIcmsDiferido(double valorIcmsDiferido) {
        this.valorIcmsDiferido = valorIcmsDiferido;
    }
    
}
