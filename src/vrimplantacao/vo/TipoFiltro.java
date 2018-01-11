package vrimplantacao.vo;

public enum TipoFiltro {

    TEXTO(1),
    NUMERO(2),
    CONSULTA_MERCADOLOGICO(3),
    CONSULTA_NCM(4),
    COMBOBOX(5),
    CONSULTA_CAMPO(6),
    CHECKBOX(7),
    DECIMAL2(8),
    TIPO_SAIDA_LANCAMENTO(9),
    TIPO_ENTRADA_LANCAMENTO(10),
    CONSULTA_CFOP(11),
    DATA(12),
    TIME(13),
    TEXTO_ILIKE(14),
    NUMERO_TEXTO(15),
    FALTA_SOBRA(16);
    private int id = 0;

    private TipoFiltro(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}
