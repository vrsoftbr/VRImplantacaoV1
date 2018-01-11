package vrimplantacao.vo.notafiscal;

public enum TipoNota {

    NORMAL(0),
    COMPLEMENTO(1),
    LANCAMENTO_ICMS(2);
    private int id;

    private TipoNota(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return this.id;
    }
}