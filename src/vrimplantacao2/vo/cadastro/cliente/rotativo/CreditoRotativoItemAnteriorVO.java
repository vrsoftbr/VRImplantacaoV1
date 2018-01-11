package vrimplantacao2.vo.cadastro.cliente.rotativo;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoItemAnteriorVO {
    private String sistema;
    private String loja;
    private String idCreditoRotativo;
    private String id;
    private int codigoAtual;
    private double valor = 0F;
    private double valorDesconto = 0F;
    private double valorMulta = 0F;
    private Date dataPagamento;

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getIdCreditoRotativo() {
        return idCreditoRotativo;
    }

    public String getId() {
        return id;
    }

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public double getValor() {
        return valor;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setIdCreditoRotativo(String idCreditoRotativo) {
        this.idCreditoRotativo = idCreditoRotativo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 4, 99999999D);
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = MathUtils.round(valorDesconto, 4, 99999999D);
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = MathUtils.round(valorMulta, 4, 99999999D);
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
        
}
