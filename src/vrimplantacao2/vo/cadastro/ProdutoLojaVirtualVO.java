package vrimplantacao2.vo.cadastro;

public class ProdutoLojaVirtualVO {

    private int id;
    private String descricao;
    private int idProduto;
    private int idTipoOrigemImagem;
    private String imagem;    

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getIdTipoOrigemImagem() {
        return idTipoOrigemImagem;
    }

    public void setIdTipoOrigemImagem(int idTipoOrigemImagem) {
        this.idTipoOrigemImagem = idTipoOrigemImagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
