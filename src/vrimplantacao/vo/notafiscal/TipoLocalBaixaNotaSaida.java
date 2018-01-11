package vrimplantacao.vo.notafiscal;

public enum TipoLocalBaixaNotaSaida {

    LOJA(0),
    TROCA(1),
    CESTA_BASICA(2);
    private int id = 0;

    private TipoLocalBaixaNotaSaida(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}