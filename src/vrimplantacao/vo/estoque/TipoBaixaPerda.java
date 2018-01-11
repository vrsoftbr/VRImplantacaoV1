package vrimplantacao.vo.estoque;

public enum TipoBaixaPerda {

    NENHUM(0),
    ENTRADA(1),
    SAIDA(2);
    private int id = 0;

    private TipoBaixaPerda(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}