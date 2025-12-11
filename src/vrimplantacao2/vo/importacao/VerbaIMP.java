package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 * Classe utilizada para importar os dados das verbas.
 * @author Wesley
 */
public class VerbaIMP {
    
    private String id;
    private String idFornecedor;
    private String documento;
    private Integer idLoja;
    private Date dataEmissao;
    private Date dataVencimento;
    private double valor = 0;
    private String observacao;
    private int mercadologico1;

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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Integer getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(Integer idLoja) {
        this.idLoja = idLoja;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
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

    public int getMercadologico1() {
        return mercadologico1;
    }

    public void setMercadologico1(int mercadologico1) {
        this.mercadologico1 = mercadologico1;
    }
}
