package vrimplantacao.vo.notafiscal;


public enum SituacaoReceberVendaPrazo {

    ABERTO(0),
    BAIXADO(1),
    CANCELADO(2);
    private int id = 0;

    private SituacaoReceberVendaPrazo(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}