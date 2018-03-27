package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.Date;

/**
 * Classe que representa o código anterior do contas a receber.
 * @author Leandro
 */
public class ContaReceberAnteriorVO {
    
    private String sistema;
    private String loja;
    private String id;
    private int codigoAtual;
    private String idFornecedor;
    private String idClienteEventual;
    private Date data;
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

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(String idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(String idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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
        this.valor = valor;
    }
        
}
