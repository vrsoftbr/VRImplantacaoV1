package vrimplantacao.vo.administrativo;

public class AcertoEstoqueVO {

    public int idProduto = 0;
    public int idTipoEntradaSaida = 0;
    public double quantidade = 0;
    public String observacao = "";
    public int idTipoMovimentacao = 0;
    public boolean baixaReceita = false;
    public boolean baixaAssociado = false;
    public int idLoja = 0;
    public int idProdutoAssociado = -1;
    public String data = "";
    public boolean baixaPerda = false;
}