package vrimplantacao2.vo.cadastro.fornecedor;

import vrimplantacao.utils.Utils;

public class ProdutoFornecedorCodigoExternoVO {
    
    private int id;
    private ProdutoFornecedorVO produtoFornecedor;
    private String codigoExterno;
    private int qtdEmbalagem = 1;
    private double pesoEmbalagem = 0;
    private double fatorEmbalagem = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProdutoFornecedorVO getProdutoFornecedor() {
        return produtoFornecedor;
    }

    public void setProdutoFornecedor(ProdutoFornecedorVO produtoFornecedor) {
        this.produtoFornecedor = produtoFornecedor;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = Utils.acertarObservacao(codigoExterno, 50);
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public double getPesoEmbalagem() {
        return pesoEmbalagem;
    }

    public void setPesoEmbalagem(double pesoEmbalagem) {
        this.pesoEmbalagem = pesoEmbalagem;
    }

    public double getFatorEmbalagem() {
        return fatorEmbalagem;
    }

    public void setFatorEmbalagem(double fatorEmbalagem) {
        this.fatorEmbalagem = fatorEmbalagem;
    }

    @Override
    public String toString() {
        return "ProdutoFornecedorCodigoExternoVO{" + "id=" + id + ", produtoFornecedor=" + produtoFornecedor + ", codigoExterno=" + codigoExterno + ", qtdEmbalagem=" + qtdEmbalagem + ", pesoEmbalagem=" + pesoEmbalagem + ", fatorEmbalagem=" + fatorEmbalagem + '}';
    }
    
}
