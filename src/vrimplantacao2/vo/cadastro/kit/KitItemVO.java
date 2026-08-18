package vrimplantacao2.vo.cadastro.kit;

/**
 * Classe que representa a tabela kit item.
 * @author Wesley
 */
public class KitItemVO {
    
    private int id;
    private KitVO kit;
    private int idProduto;
    private double precoVenda;
    private double quantidade;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public KitVO getKit() {
        return kit;
    }

    public void setKit(KitVO kit) {
        this.kit = kit;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
}
