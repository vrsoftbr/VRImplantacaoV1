package vrimplantacao.vo.notafiscal;

public enum SituacaoNfe {

    NAO_TRANSMITIDA(0),
    AUTORIZADA(1),
    REJEITADA(2),
    CANCELADA(3),
    INUTILIZADA(4),
    DENEGADA(5);
    private int id = 0;

    private SituacaoNfe(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return this.id;
    }
}