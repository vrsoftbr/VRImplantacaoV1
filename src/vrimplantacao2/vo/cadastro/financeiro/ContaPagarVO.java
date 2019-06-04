package vrimplantacao2.vo.cadastro.financeiro;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoPagarOutrasDespesas;

/**
 *
 * @author Importacao
 */
public class ContaPagarVO {
    private int id;
    private int idOutrasDespesas;
    private FornecedorVO fornecedor;
    private int numeroDocumento;
    private int idTipoEntrada = 210;
    private Date dataEmissao = new Date();
    private Date dataEntrada = new Date();
    private Date vencimento = new Date();
    private double valor = 0;// numeric(11,2) NOT NULL,
    private SituacaoPagarOutrasDespesas situacaoPagarOutrasDespesas = SituacaoPagarOutrasDespesas.NAO_FINALIZADO;// integer NOT NULL,
    private int id_loja;// integer NOT NULL,
    private String observacao = "IMPORTACAO VR";// character varying(280),
    private int id_tipopiscofins = -1;// integer,
    private Timestamp dataHoraAlteracao = new Timestamp(new Date().getTime());// timestamp without time zone NOT NULL DEFAULT now(),

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOutrasDespesas() {
        return idOutrasDespesas;
    }

    public void setIdOutrasDespesas(int idOutrasDespesas) {
        this.idOutrasDespesas = idOutrasDespesas;
    }
    
    public FornecedorVO getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(FornecedorVO fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(int numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public int getIdTipoEntrada() {
        return idTipoEntrada;
    }

    public void setIdTipoEntrada(int idTipoEntrada) {
        this.idTipoEntrada = idTipoEntrada;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao != null ? dataEmissao : new Date();
        if (this.dataHoraAlteracao == null) {
            this.dataHoraAlteracao = new Timestamp(this.dataEmissao.getTime());
        }
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada != null ? dataEntrada : new Date();
        if (this.dataHoraAlteracao == null) {
            this.dataHoraAlteracao = new Timestamp(this.dataEntrada.getTime());
        }
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 2, 999999999D);
    }

    public SituacaoPagarOutrasDespesas getSituacaoPagarOutrasDespesas() {
        return situacaoPagarOutrasDespesas;
    }

    public void setSituacaoPagarOutrasDespesas(SituacaoPagarOutrasDespesas situacaoPagarOutrasDespesas) {
        this.situacaoPagarOutrasDespesas = situacaoPagarOutrasDespesas != null ? situacaoPagarOutrasDespesas : SituacaoPagarOutrasDespesas.NAO_FINALIZADO;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 280);
    }

    public int getId_tipopiscofins() {
        return id_tipopiscofins;
    }

    public void setId_tipopiscofins(int id_tipopiscofins) {
        this.id_tipopiscofins = id_tipopiscofins;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao != null ? dataHoraAlteracao : new Timestamp(new Date().getTime());
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }
}
