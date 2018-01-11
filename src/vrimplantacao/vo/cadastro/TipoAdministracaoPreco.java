package vrimplantacao.vo.cadastro;

public enum TipoAdministracaoPreco {

    ENTRADA(1),
    PEDIDO(2),
    ASSOCIADO(3),
    RECEITA(4),
    PEPS(5);
    private int id = 0;

    private TipoAdministracaoPreco(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return this.id;
    }
}
