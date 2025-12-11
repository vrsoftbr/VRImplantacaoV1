package vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Wesley
 */
public class DevolucaoAnteriorVO {

    private String sistema;
    private String loja;
    private String id;
    private DevolucaoVO codigoAtual;
    private String numeroNota;
    private String idFornecedor;
    private Date emissao;
    private Date vencimento;
    private double valor;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DevolucaoVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(DevolucaoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(String idFornecedor) {
        this.idFornecedor = idFornecedor;
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

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 4, 999999D);;
    }

    public double getValor() {
        return valor;
    }
}
