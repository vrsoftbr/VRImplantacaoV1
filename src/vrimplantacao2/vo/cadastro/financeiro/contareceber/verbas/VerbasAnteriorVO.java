package vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Wesley
 */
public class VerbasAnteriorVO {
    private String sistema;
    private String loja;
    private String id;
    private VerbasVO codigoAtual;
    private String idFornecedor;
    private Date emissao;
    private Date vencimento;
    private double valor;
    private int codigoAtualVencimento;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(VerbasVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }
    
    public String getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(String idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 4, 999999D);;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getId() {
        return id;
    }

    public VerbasVO getCodigoAtual() {
        return codigoAtual;
    }
    
    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public double getValor() {
        return valor;
    }

    public int getCodigoAtualVencimento() {
        return codigoAtualVencimento;
    }

    public void setCodigoAtualVencimento(int codigoAtualVencimento) {
        this.codigoAtualVencimento = codigoAtualVencimento;
    }
}
