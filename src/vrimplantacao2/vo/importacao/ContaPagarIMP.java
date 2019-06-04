package vrimplantacao2.vo.importacao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe utilizada para importar os dados das contas à pagar.
 * @author Leandro
 */
public class ContaPagarIMP {
    
    private String id;
    private String idFornecedor;
    private String numeroDocumento;
    /**
     * Utilize este campo para informar um tipo de entrada especifico para vincular ao pagarfornecedor.
     */
    private Integer idTipoEntradaVR;
    private Date dataEmissao;
    private Date dataEntrada;
    private Timestamp dataHoraAlteracao;
    @Deprecated
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

    @Deprecated
    public double getValor() {
        return valor;
    }

    @Deprecated
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
        ContaPagarVencimentoIMP imp = new ContaPagarVencimentoIMP();
        this.getVencimentos().add(imp);
        imp.setContaPagar(this);
        imp.setVencimento(vencimento);
        imp.setValor(valor);
        imp.setNumeroParcela(getVencimentos().size());
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
    
}
