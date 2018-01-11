package vrimplantacao2.vo.importacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoIMP {
    private static final Date DT_ATUAL = new Date();
    
    private String id;
    private Date dataEmissao = DT_ATUAL;
    private String numeroCupom;
    private String ecf;
    private double valor = 0F;
    private String observacao;
    private String idCliente;
    private Date dataVencimento = DT_ATUAL;
    private int parcela = 1;
    private double juros = 0F;
    private double multa = 0F;
    private String cnpjCliente;
    private List<CreditoRotativoItemIMP> pagamentos = new ArrayList<>();
    
    public CreditoRotativoItemIMP addPagamento(
            String id, double valor, double desconto, double multa, 
            Date dataPagamento, String observacao
    ) {
        CreditoRotativoItemIMP item = new CreditoRotativoItemIMP();
        pagamentos.add(item);
        item.setCreditoRotativo(this);
        item.setId(id);
        item.setValor(valor);
        item.setDesconto(desconto);
        item.setMulta(multa);
        item.setDataPagamento(dataPagamento);
        item.setObservacao(observacao);
        return item;
    }

    public String getId() {
        return id;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public String getEcf() {
        return ecf;
    }

    public double getValor() {
        return valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public int getParcela() {
        return parcela;
    }

    public double getJuros() {
        return juros;
    }

    public double getMulta() {
        return multa;
    }
    
    public String getCnpjCliente() {
        return cnpjCliente;
    }

    public List<CreditoRotativoItemIMP> getPagamentos() {
        return pagamentos;
    }
    
    public double getValorTotal() {
        return valor + multa + juros;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao == null ? DT_ATUAL : dataEmissao;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public void setEcf(String ecf) {
        this.ecf = ecf;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento == null ? DT_ATUAL : dataVencimento;
    }

    public void setParcela(int parcela) {
        this.parcela = parcela;
    }

    public void setJuros(double juros) {
        this.juros = juros;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }
    
    public void setCnpjCliente(String cnpjCliente) {
        this.cnpjCliente = cnpjCliente;
    }
}
