package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;

/**
 * Classe que representa um registro da tabela implantacao.codant_contapagar
 * @author Leandro
 */
public class ContaPagarAnteriorVO {
    
    
    private String sistema;
    private String agrupador;
    private String id;
    private ContaPagarAnteriorTipo tipo = ContaPagarAnteriorTipo.OUTRASDESPESAS;
    private String id_fornecedor;
    private Integer codigoAtual;
    private Date dataEmissao;
    private Date dataVencimento;
    private double valor;
    private String documento;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContaPagarAnteriorTipo getTipo() {
        return tipo;
    }

    public void setTipo(ContaPagarAnteriorTipo tipo) {
        this.tipo = tipo == null ? ContaPagarAnteriorTipo.PAGARFORNECEDOR : tipo;
    }

    public String getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(String id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public Integer getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(Integer codigoAtual) {
        this.codigoAtual = codigoAtual;
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
        this.valor = MathUtils.trunc(valor, 4, 9999999999D);
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

}
