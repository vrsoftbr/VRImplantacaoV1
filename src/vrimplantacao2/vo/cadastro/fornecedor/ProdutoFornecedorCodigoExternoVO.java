package vrimplantacao2.vo.cadastro.fornecedor;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class ProdutoFornecedorCodigoExternoVO {
    private int id;
    private ProdutoFornecedorVO produtoFornecedor;
    private String codigoExterno;

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
        this.codigoExterno = Utils.acertarTexto(codigoExterno, 50);
    }
}
