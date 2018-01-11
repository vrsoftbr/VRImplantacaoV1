package vrimplantacao2.vo.cadastro.cliente.cheque;

import java.util.Date;
import vrimplantacao.utils.Utils;

/**
 * Classe que representa o cheque anterior no banco de dados
 * @author Leandro
 */
public class ChequeAnteriorVO {
    private String sistema;
    private String loja;
    private String id;
    private ChequeVO codigoatual;
    private Date data;
    private int banco;
    private String agencia;
    private String conta;
    private String cheque;
    private double valor = 0;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoatual(ChequeVO codigoatual) {
        this.codigoatual = codigoatual;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setBanco(int banco) {
        this.banco = banco;
    }

    public void setConta(String conta) {
        this.conta = Utils.formataNumero(conta);
    }

    public void setAgencia(String agencia) {
        this.agencia = Utils.formataNumero(agencia);
    }

    public void setCheque(String cheque) {
        this.cheque = Utils.formataNumero(cheque);
    }

    public void setValor(double valor) {
        this.valor = valor;
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

    public ChequeVO getCodigoatual() {
        return codigoatual;
    }

    public Date getData() {
        return data;
    }

    public int getBanco() {
        return banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getConta() {
        return conta;
    }

    public String getCheque() {
        return cheque;
    }

    public double getValor() {
        return valor;
    }
}
