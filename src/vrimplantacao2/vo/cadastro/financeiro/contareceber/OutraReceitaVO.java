package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao2.vo.enums.TipoLocalCobranca;
import vrimplantacao2.vo.enums.TipoReceita;

/**
 *
 * @author Leandro
 */
public class OutraReceitaVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('receberoutrasreceitas_id_seq'::regclass),
    private int idLoja;//id_loja integer NOT NULL,
    private int idClienteEventual;//id_clienteeventual integer,
    private Date dataEmissao;// date NOT NULL,
    private Date dataVencimento;// date NOT NULL,
    private double valor = 0;// numeric(11,2) NOT NULL,
    private String observacao;// character varying(500) NOT NULL,
    private SituacaoReceberOutrasReceitas situacao = SituacaoReceberOutrasReceitas.ABERTO;// id_situacaoreceberoutrasreceitas integer NOT NULL,
    private TipoLocalCobranca tipoLocalCobranca = TipoLocalCobranca.CARTEIRA;// id_tipolocalcobranca integer NOT NULL,
    private TipoReceita tipoReceita = TipoReceita.CR_OUTRAS_UNIDADES;// id_tiporeceita integer NOT NULL,
    private int idFornecedor;//id_fornecedor integer,
    private int idBoleto;//id_boleto bigint,
    private Timestamp dataHoraAlteracao;// timestamp without time zone NOT NULL DEFAULT now(),
    private boolean exportado = false;// boolean NOT NULL DEFAULT false,
    private int idNotaServico;//id_notaservico integer,
    private Date dataexportacao;// date

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(int idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public SituacaoReceberOutrasReceitas getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoReceberOutrasReceitas situacao) {
        this.situacao = situacao;
    }

    public TipoLocalCobranca getTipoLocalCobranca() {
        return tipoLocalCobranca;
    }

    public void setTipoLocalCobranca(TipoLocalCobranca tipoLocalCobranca) {
        this.tipoLocalCobranca = tipoLocalCobranca;
    }

    public TipoReceita getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceita tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public boolean isExportado() {
        return exportado;
    }

    public void setExportado(boolean exportado) {
        this.exportado = exportado;
    }

    public int getIdNotaServico() {
        return idNotaServico;
    }

    public void setIdNotaServico(int idNotaServico) {
        this.idNotaServico = idNotaServico;
    }

    public Date getDataexportacao() {
        return dataexportacao;
    }

    public void setDataexportacao(Date dataexportacao) {
        this.dataexportacao = dataexportacao;
    }
    
    
    
}
