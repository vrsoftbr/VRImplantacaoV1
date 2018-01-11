package vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoReceberCaixa;

/**
 * Classe que representa um registro da tabela recebimentocaixa.
 * @author Leandro
 */
public class RecebimentoCaixaVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('recebercaixa_id_seq'::regclass),
    private int idTipoRecebivel;// integer NOT NULL,
    private Date dataEmissao = new Date();// date NOT NULL,
    private SituacaoReceberCaixa situacaoReceberCaixa = SituacaoReceberCaixa.ABERTO;// integer NOT NULL,
    private double valor;// numeric(11,2) NOT NULL,
    private String observacao;// character varying(500) NOT NULL,
    private int idTipoLocalCobranca;// integer NOT NULL,
    private int idTipoRecebimento;// integer NOT NULL,
    private Date dataVencimento = new Date();// date NOT NULL,
    private int idLoja;// integer NOT NULL,

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTipoRecebivel() {
        return idTipoRecebivel;
    }

    public void setIdTipoRecebivel(int idTipoRecebivel) {
        this.idTipoRecebivel = idTipoRecebivel;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao == null ? new Date() : dataEmissao;
    }

    public SituacaoReceberCaixa getSituacaoReceberCaixa() {
        return situacaoReceberCaixa;
    }

    public void setSituacaoReceberCaixa(SituacaoReceberCaixa situacaoReceberCaixa) {
        this.situacaoReceberCaixa = situacaoReceberCaixa == null ? SituacaoReceberCaixa.ABERTO : situacaoReceberCaixa;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.trunc(valor, 2, 99999999);
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 500);
    }

    public int getIdTipoLocalCobranca() {
        return idTipoLocalCobranca;
    }

    public void setIdTipoLocalCobranca(int idTipoLocalCobranca) {
        this.idTipoLocalCobranca = idTipoLocalCobranca;
    }

    public int getIdTipoRecebimento() {
        return idTipoRecebimento;
    }

    public void setIdTipoRecebimento(int idTipoRecebimento) {
        this.idTipoRecebimento = idTipoRecebimento;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento == null ? this.dataEmissao : dataVencimento;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }
    
    
}
