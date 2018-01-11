package vrimplantacao2.vo.cadastro.convenio.transacao;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.cadastro.convenio.conveniado.TipoServicoConvenio;

/**
 *
 * @author Leandro
 */
public class ConvenioTransacaoVO {
    private int id;// integer NOT NULL DEFAULT nextval('conveniadotransacao_id_seq'::regclass),
    private int id_conveniado;// integer NOT NULL,
    private int ecf;// integer NOT NULL,
    private int numeroCupom;// integer NOT NULL,
    private Timestamp dataHora = new Timestamp(new Date().getTime());// timestamp without time zone NOT NULL,
    private int id_loja;// integer NOT NULL,
    private double valor = 0;// numeric(11,2) NOT NULL,
    private SituacaoTransacaoConveniado situacaoTransacaoConveniado = SituacaoTransacaoConveniado.PENDENTE;//id_situacaotransacaoconveniado integer NOT NULL,
    private boolean lancamentoManual = false;// boolean NOT NULL,
    private int matricula = 500001;// integer NOT NULL,
    private Date dataMovimento;// date NOT NULL,
    private boolean finalizado = false;// boolean NOT NULL,
    private TipoServicoConvenio tipoServicoConvenio = TipoServicoConvenio.CONVENIO;// id_tiposervicoconvenio integer NOT NULL,
    private String observacao = "";// character varying(100),

    public void setId(int id) {
        this.id = id;
    }

    public void setId_conveniado(int id_conveniado) {
        this.id_conveniado = id_conveniado;
    }

    public void setEcf(int ecf) {
        this.ecf = ecf;
    }

    public void setNumeroCupom(int numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setSituacaoTransacaoConveniado(SituacaoTransacaoConveniado situacaoTransacaoConveniado) {
        this.situacaoTransacaoConveniado = situacaoTransacaoConveniado != null ? situacaoTransacaoConveniado : SituacaoTransacaoConveniado.OK;
    }

    public void setLancamentoManual(boolean lancamentoManual) {
        this.lancamentoManual = lancamentoManual;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public void setTipoServicoConvenio(TipoServicoConvenio tipoServicoConvenio) {
        this.tipoServicoConvenio = tipoServicoConvenio != null ? tipoServicoConvenio : TipoServicoConvenio.CONVENIO;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 100);
    }

    public int getId() {
        return id;
    }

    public int getId_conveniado() {
        return id_conveniado;
    }

    public int getEcf() {
        return ecf;
    }

    public int getNumeroCupom() {
        return numeroCupom;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }

    public int getId_loja() {
        return id_loja;
    }

    public double getValor() {
        return valor;
    }

    public SituacaoTransacaoConveniado getSituacaoTransacaoConveniado() {
        return situacaoTransacaoConveniado;
    }

    public boolean isLancamentoManual() {
        return lancamentoManual;
    }

    public int getMatricula() {
        return matricula;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public TipoServicoConvenio getTipoServicoConvenio() {
        return tipoServicoConvenio;
    }

    public String getObservacao() {
        return observacao;
    }

}
