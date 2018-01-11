package vrimplantacao.vo;

public enum TipoBaixaReceita {

    MOVIMENTACAO(0),
    PRODUCAO(1);
    private int id = 0;

    private TipoBaixaReceita(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}
