package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.Date;

/**
 *
 * @author Leandro
 */
public class ContaReceberItemAnteriorVO {
    
    private String sistema;
    private String loja;
    private String idContaReceber;
    private String id;
    private int codigoAtual;
    private Date data;
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

    public String getIdContaReceber() {
        return idContaReceber;
    }

    public void setIdContaReceber(String idContaReceber) {
        this.idContaReceber = idContaReceber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
    
}
