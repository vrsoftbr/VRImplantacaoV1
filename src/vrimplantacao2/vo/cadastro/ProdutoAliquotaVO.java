package vrimplantacao2.vo.cadastro;

import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao2.vo.enums.Icms;

public class ProdutoAliquotaVO {
    
    private int id = 0;
    private ProdutoVO produto;
    private EstadoVO estado;
    private Icms aliquotaDebito;
    private Icms aliquotaCredito;
    private Icms aliquotaDebitoForaEstado;
    private Icms aliquotaCreditoForaEstado;
    private Icms aliquotaDebitoForaEstadoNf;
    private Icms aliquotaConsumidor;
    private String aliquotaCreditoFornecedor;
    private int excecao = 0;
    private int beneficio = 0;
    
    public int getId() {
        return id;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public EstadoVO getEstado() {
        return estado;
    }

    public Icms getAliquotaDebito() {
        return aliquotaDebito;
    }

    public Icms getAliquotaCredito() {
        return aliquotaCredito;
    }

    public Icms getAliquotaDebitoForaEstado() {
        return aliquotaDebitoForaEstado;
    }

    public Icms getAliquotaCreditoForaEstado() {
        return aliquotaCreditoForaEstado;
    }

    public Icms getAliquotaDebitoForaEstadoNf() {
        return aliquotaDebitoForaEstadoNf;
    }

    public Icms getAliquotaConsumidor() {
        return aliquotaConsumidor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public void setEstado(EstadoVO estado) {
        this.estado = estado;
    }

    public void setAliquotaDebito(Icms aliquotaDebito) {
        this.aliquotaDebito = aliquotaDebito;
    }

    public void setAliquotaCredito(Icms aliquotaCredito) {
        this.aliquotaCredito = aliquotaCredito;
    }

    public void setAliquotaDebitoForaEstado(Icms aliquotaDebitoForaEstado) {
        this.aliquotaDebitoForaEstado = aliquotaDebitoForaEstado;
    }

    public void setAliquotaCreditoForaEstado(Icms aliquotaCreditoForaEstado) {
        this.aliquotaCreditoForaEstado = aliquotaCreditoForaEstado;
    }

    public void setAliquotaDebitoForaEstadoNf(Icms aliquotaDebitoForaEstadoNf) {
        this.aliquotaDebitoForaEstadoNf = aliquotaDebitoForaEstadoNf;
    }

    public void setAliquotaConsumidor(Icms aliquotaConsumidor) {
        this.aliquotaConsumidor = aliquotaConsumidor;
    }

    public String getAliquotaCreditoFornecedor() {
        return aliquotaCreditoFornecedor;
    }

    public void setAliquotaCreditoFornecedor(String aliquotaCreditoFornecedor) {
        this.aliquotaCreditoFornecedor = aliquotaCreditoFornecedor;
    }

    public int getExcecao() {
        return excecao;
    }

    public void setExcecao(int excecao) {
        this.excecao = excecao;
    }

    public int getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(int beneficio) {
        this.beneficio = beneficio;
    }
}
