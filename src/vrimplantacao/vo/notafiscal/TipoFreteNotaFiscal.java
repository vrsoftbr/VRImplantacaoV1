package vrimplantacao.vo.notafiscal;

public enum TipoFreteNotaFiscal {

    TERCEIRO(0),
    EMITENTE(1),
    DESTINATARIO(2),
    SEM_COBRANCA(3);
    private int id;

    private TipoFreteNotaFiscal(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return this.id;
    }
}