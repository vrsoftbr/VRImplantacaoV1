package vrimplantacao.vo.vrimplantacao;

public class EstoqueTerceiroVO {
  private double id_produto;
  private double quantidade;
  private int id_loja;
  private int id_lojaterceiro;
  private long codigoBarras;
  private String descProduto;

    public double getId_produto() {
        return id_produto;
    }

    public void setId_produto(double id_produto) {
        this.id_produto = id_produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getId_lojaterceiro() {
        return id_lojaterceiro;
    }

    public void setId_lojaterceiro(int id_lojaterceiro) {
        this.id_lojaterceiro = id_lojaterceiro;
    }


    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getDescProduto() {
        return descProduto;
    }

    public void setDescProduto(String descProduto) {
        this.descProduto = descProduto;
    }
}