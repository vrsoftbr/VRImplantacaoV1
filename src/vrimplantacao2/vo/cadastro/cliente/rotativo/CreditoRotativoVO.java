package vrimplantacao2.vo.cadastro.cliente.rotativo;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCreditoRotativo;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoVO {
    private int id;// integer NOT NULL DEFAULT nextval('recebercreditorotativo_id_seq'::regclass),
    private int id_loja;// integer NOT NULL,
    private Date dataEmissao;// date NOT NULL,
    private int numeroCupom;// integer NOT NULL,
    private int ecf;// integer,
    private double valor = 0;// numeric(11,2) NOT NULL,
    private boolean lancamentoManual = false;// boolean NOT NULL,
    private String observacao = "";// text NOT NULL,
    private SituacaoCreditoRotativo situacaoCreditoRotativo = SituacaoCreditoRotativo.ABERTO;//id_situacaorecebercreditorotativo integer NOT NULL,
    private int id_clientePreferencial;// integer NOT NULL,
    private Date dataVencimento;// date NOT NULL,
    private int matricula = 500001;// integer,
    private int parcela = 1;// integer NOT NULL,
    private double valorJuros = 0D;// numeric(11,2) NOT NULL DEFAULT 0,
    private int id_boleto = 0;// bigint,
    private int id_tipoLocalCobranca = 0;// integer NOT NULL DEFAULT 0,
    private double valorMulta = 0D;// numeric(11,2) NOT NULL DEFAULT 0,
    private String justificativa = "";// text NOT NULL DEFAULT ''::character varying,
    private boolean exportado = false;// boolean NOT NULL DEFAULT false,
    private Timestamp dataHoraAlteracao = new Timestamp(new Date().getTime());// timestamp without time zone NOT NULL DEFAULT now(),
    private String nomeDependente = "";// character varying(40),
    private long cpfDependente = 0;// numeric(14,0),
    private Date dataExportacao = null;// date,

    public void setId(int id) {
        this.id = id;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public void setNumeroCupom(int numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor < 0 ? 0 : valor, 2, 99999999999D);
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao);
    }

    public void setSituacaoCreditoRotativo(SituacaoCreditoRotativo situacaoCreditoRotativo) {
        this.situacaoCreditoRotativo = situacaoCreditoRotativo != null ? situacaoCreditoRotativo : SituacaoCreditoRotativo.ABERTO;
    }

    public void setId_clientePreferencial(int id_clientePreferencial) {
        this.id_clientePreferencial = id_clientePreferencial;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public void setParcela(int parcela) {
        this.parcela = parcela < 1 ? 1 : parcela;
    }

    public void setValorJuros(double valorJuros) {
        this.valorJuros = MathUtils.round(valorJuros < 0 ? 0 : valorJuros, 2, 99999999999D);
    }

    public void setId_boleto(int id_boleto) {
        this.id_boleto = id_boleto;
    }

    public void setId_tipoLocalCobranca(int id_tipoLocalCobranca) {
        this.id_tipoLocalCobranca = id_tipoLocalCobranca;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = MathUtils.round(valorMulta < 0 ? 0 : valorMulta, 2, 99999999999D);
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = Utils.acertarTexto(justificativa);
    }

    public void setExportado(boolean exportado) {
        this.exportado = exportado;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public void setNomeDependente(String nomeDependente) {
        this.nomeDependente = Utils.acertarTexto(nomeDependente, 40);
    }

    public void setCpfDependente(long cpfDependente) {
        this.cpfDependente = cpfDependente <= 99999999999999L ? cpfDependente : 0;
    }

    public void setDataExportacao(Date dataExportacao) {
        this.dataExportacao = dataExportacao;
    }

    public int getId() {
        return id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public int getNumeroCupom() {
        return numeroCupom;
    }

    public int getEcf() {
        return ecf;
    }

    public double getValor() {
        return valor;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public String getObservacao() {
        return observacao;
    }

    public SituacaoCreditoRotativo getSituacaoCreditoRotativo() {
        return situacaoCreditoRotativo;
    }

    public int getId_clientePreferencial() {
        return id_clientePreferencial;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public int getMatricula() {
        return matricula;
    }

    public int getParcela() {
        return parcela;
    }

    public double getValorJuros() {
        return valorJuros;
    }

    public int getId_boleto() {
        return id_boleto;
    }

    public int getId_tipoLocalCobranca() {
        return id_tipoLocalCobranca;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public boolean isExportado() {
        return exportado;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public String getNomeDependente() {
        return nomeDependente;
    }

    public long getCpfDependente() {
        return cpfDependente;
    }

    public Date getDataExportacao() {
        return dataExportacao;
    }
    
    public double getTotal() {
        return getValor() + getValorJuros() + getValorMulta();
    }

}
