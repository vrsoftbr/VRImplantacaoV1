package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao2.vo.enums.TipoRecebimento;

/**
 *
 * @author Leandro
 */
public class RecebimentoCaixaIMP {
    private String id;
    private String idTipoRecebivel;
    private Date dataEmissao;
    private double valor;
    private String observacao;
    private TipoRecebimento tipoRecebimento = TipoRecebimento.DEPOSITO_BANCARIO;
    private Date dataVencimento = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTipoRecebivel() {
        return idTipoRecebivel;
    }

    public void setIdTipoRecebivel(String idTipoRecebivel) {
        this.idTipoRecebivel = idTipoRecebivel;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
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

    public TipoRecebimento getTipoRecebimento() {
        return tipoRecebimento;
    }

    public void setTipoRecebimento(TipoRecebimento tipoRecebimento) {
        this.tipoRecebimento = tipoRecebimento;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
    
}
