package vrimplantacao2.vo.cadastro.financeiro;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoPagarOutrasDespesas;
import vrimplantacao2.vo.enums.TipoEntrada;

/**
 * Classe que representa o pagar outras despesas no banco de dados.
 * @author Leandro
 */
public class PagarOutrasDespesasVO {
    
    private int id;
    private int idFornecedor;
    private int numeroDocumento;
    private TipoEntrada tipoEntrada = TipoEntrada.OUTRAS;
    private Date dataEmissao;
    private Date dataEntrada;
    private double valor = 0;// numeric(11,2) NOT NULL,
    private SituacaoPagarOutrasDespesas situacaoPagarOutrasDespesas = SituacaoPagarOutrasDespesas.NAO_FINALIZADO;// integer NOT NULL,
    private int id_loja;// integer NOT NULL,
    private String observacao = "IMPORTACAO VR";// character varying(280),
    private int id_tipopiscofins = -1;// integer,
    private Timestamp dataHoraAlteracao;// timestamp without time zone NOT NULL DEFAULT now(),
    private List<PagarOutrasDespesasVencimentoVO> vencimentos = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(int numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        this.tipoEntrada = tipoEntrada != null ? tipoEntrada : TipoEntrada.COMPRA_MERCADORIAS;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
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
        this.dataHoraAlteracao = dataHoraAlteracao;
    }

    public List<PagarOutrasDespesasVencimentoVO> getVencimentos() {
        return vencimentos;
    }    
    
    /**
     * Inclui um vencimento novo na listagem.
     * @param vencimento Data de vencimento da parcela.
     * @param valor Valor da parcala.
     * @return 
     */
    public PagarOutrasDespesasVencimentoVO addVencimento(Date vencimento, double valor) {
        PagarOutrasDespesasVencimentoVO result = new PagarOutrasDespesasVencimentoVO();
        this.getVencimentos().add(result);
        result.setPagarOutrasDespesas(this);
        result.setDataVencimento(vencimento);
        result.setValor(valor);
        return result;
    }
    
}
