package vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 * Classe que representa um registro na tabela implantacao.codant_recebimentocaixa.
 * @author Leandro
 */
public class RecebimentoCaixaAnteriorVO {
    
    private String sistema;
    private String agrupador;
    private String id;
    private RecebimentoCaixaVO codigoAtual;
    private String idTipoRecebivel;
    private Date dataEmissao;
    private Date vencimento;
    private double valor;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecebimentoCaixaVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(RecebimentoCaixaVO codigoAtual) {
        this.codigoAtual = codigoAtual;
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

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.trunc(valor, 2, 99999999);
    }
    
}
