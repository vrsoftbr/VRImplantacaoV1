package vrimplantacao.vo.notafiscal;

public enum SituacaoNotaSaida {

    NAO_FINALIZADO(0),
    FINALIZADO(1);
    private int id = 0;

    private SituacaoNotaSaida(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return this.id;
    }
}