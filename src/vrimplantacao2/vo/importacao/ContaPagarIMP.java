package vrimplantacao2.vo.importacao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao2.vo.enums.TipoPagamento;

/**
 * Classe utilizada para importar os dados das contas à pagar.
 * @author Leandro
 */
public class ContaPagarIMP {
    
    private String id;
    private String idFornecedor;
    private String cnpj;
    private String numeroDocumento;
    /**
     * Utilize este campo para informar um tipo de entrada especifico para vincular ao pagarfornecedor.
     */
    private Integer idTipoEntradaVR;
    private Date dataEmissao;
    private Date dataEntrada;
    private Timestamp dataHoraAlteracao;
    private double valor = 0;
    private String observacao;
    private List<ContaPagarVencimentoIMP> vencimentos = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(String idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Timestamp getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao;
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

    public List<ContaPagarVencimentoIMP> getVencimentos() {
        return vencimentos;
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor) {
        return addVencimento(vencimento, valor, TipoPagamento.BOLETO_BANCARIO);
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor, int numeroParcela) {
        return addVencimento(vencimento, valor, TipoPagamento.BOLETO_BANCARIO, numeroParcela);
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor, Date pagamento) {
        return addVencimento(vencimento, valor, TipoPagamento.BOLETO_BANCARIO, pagamento);
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor, String observacao) {
        return addVencimento(vencimento, valor, TipoPagamento.BOLETO_BANCARIO, observacao);
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor, TipoPagamento tipoPagamento) {
        ContaPagarVencimentoIMP imp = new ContaPagarVencimentoIMP();
        this.getVencimentos().add(imp);
        imp.setContaPagar(this);
        imp.setVencimento(vencimento);
        imp.setValor(valor);
        imp.setNumeroParcela(getVencimentos().size());
        imp.setTipoPagamento(tipoPagamento);
        
        return imp;
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor, TipoPagamento tipoPagamento, int numeroParcela) {
        ContaPagarVencimentoIMP imp = new ContaPagarVencimentoIMP();
        this.getVencimentos().add(imp);
        imp.setContaPagar(this);
        imp.setVencimento(vencimento);
        imp.setValor(valor);
        imp.setNumeroParcela(numeroParcela);
        imp.setTipoPagamento(tipoPagamento);
        return imp;
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, double valor, TipoPagamento tipoPagamento, Date pagamento) {
        ContaPagarVencimentoIMP imp = new ContaPagarVencimentoIMP();
        
        this.getVencimentos().add(imp);
        imp.setContaPagar(this);
        imp.setVencimento(vencimento);
        imp.setDataPagamento(pagamento);
        imp.setValor(valor);
        imp.setNumeroParcela(getVencimentos().size());
        imp.setTipoPagamento(tipoPagamento);
        imp.setPago(pagamento != null);
        
        return imp;
    }
    
    public ContaPagarVencimentoIMP addVencimento(Date vencimento, 
            double valor, 
            TipoPagamento tipoPagamento, 
            String observacao) {
        ContaPagarVencimentoIMP imp = new ContaPagarVencimentoIMP();
        
        this.getVencimentos().add(imp);
        imp.setContaPagar(this);
        imp.setVencimento(vencimento);
        imp.setValor(valor);
        imp.setNumeroParcela(getVencimentos().size());
        imp.setTipoPagamento(tipoPagamento);
        imp.setObservacao(observacao);
        
        return imp;
    }

    public ContaPagarVencimentoIMP setVencimento(Date vencimento) {
        this.vencimentos.clear();
        return this.addVencimento(vencimento, this.valor);
    }

    public Integer getIdTipoEntradaVR() {
        return idTipoEntradaVR;
    }

    public void setIdTipoEntradaVR(Integer idTipoEntradaVR) {
        this.idTipoEntradaVR = idTipoEntradaVR;
    }
    
    /**
     * @return the cnpj
     */
    public String getCnpj() {
        return cnpj;
    }

    /**
     * @param cnpj the cnpj to set
     */
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    
}
