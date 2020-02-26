package vrimplantacao2.vo.cadastro.cliente.rotativo;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoAnteriorVO {
    private String sistema;
    private String loja;
    private String id;
    private CreditoRotativoVO codigoAtual;
    private String idCliente;
    private Date vencimento;
    private double valor;
    private boolean pago;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(CreditoRotativoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public void setValor(double valor) {
        this.valor = MathUtils.round(valor, 4, 999999D);;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
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

    public CreditoRotativoVO getCodigoAtual() {
        return codigoAtual;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public double getValor() {
        return valor;
    }

    public boolean isPago() {
        return pago;
    }
    
    
    
}
