package vrimplantacao2.vo.cadastro.fornecedor;

/**
 *
 * @author Leandro
 */
public class PrazoPedidoVO {
    private int id;
    private int idFornecedor;
    private int idLoja;
    private int diasEntregaPedido;
    private int diasAtualizaPedidoParcial;

    public PrazoPedidoVO() {
    }

    public PrazoPedidoVO(int idFornecedor, int idLoja, int diasEntregaPedido, int diasAtualizaPedidoParcial) {
        this.idFornecedor = idFornecedor;
        this.idLoja = idLoja;
        this.diasEntregaPedido = diasEntregaPedido;
        this.diasAtualizaPedidoParcial = diasAtualizaPedidoParcial;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public void setDiasEntregaPedido(int diasEntregaPedido) {
        this.diasEntregaPedido = diasEntregaPedido < 0 ? 0 : diasEntregaPedido;
    }

    public void setDiasAtualizaPedidoParcial(int diasAtualizaPedidoParcial) {
        this.diasAtualizaPedidoParcial = diasAtualizaPedidoParcial < 0 ? 0 : diasAtualizaPedidoParcial;
    }

    public int getId() {
        return id;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public int getDiasEntregaPedido() {
        return diasEntregaPedido;
    }

    public int getDiasAtualizaPedidoParcial() {
        return diasAtualizaPedidoParcial;
    }
    
    
    
}
