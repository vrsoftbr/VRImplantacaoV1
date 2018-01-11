package vrimplantacao.vo.cadastro;

public enum TipoPercentualValor {

    PERCENTUAL(0),
    VALOR(1);
    private int id = 0;

    private TipoPercentualValor(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}