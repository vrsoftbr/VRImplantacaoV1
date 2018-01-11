package vrimplantacao.vo.administrativo;

public enum TipoEntradaSaida {

    ENTRADA(0),
    SAIDA(1);
    private int id = 0;

    private TipoEntradaSaida(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}
