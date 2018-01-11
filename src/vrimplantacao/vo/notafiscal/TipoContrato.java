package vrimplantacao.vo.notafiscal;

public enum TipoContrato {

    ENTRADA(1),
    DEVOLUCAO_FORNECEDOR(2),
    ENTRADA_PRODUTOR(3);
    private int id = 0;

    private TipoContrato(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}