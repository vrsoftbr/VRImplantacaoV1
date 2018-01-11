package vrimplantacao.vo.notafiscal;

public enum SituacaoReceberDevolucao {

    ABERTO(0),
    BAIXADO(1),
    CANCELADO(2);
    private int id = 0;

    private SituacaoReceberDevolucao(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}