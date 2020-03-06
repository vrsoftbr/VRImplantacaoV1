package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

public enum TipoPercentualValor {

    PERCENTUAL(0, "PERCENTUAL"),
    VALOR(1, "VALOR");
    private int id = 0;
    private String descricao = "";

    private TipoPercentualValor(int i_id, String i_descricao) {
        this.id = i_id;
        this.descricao = i_descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
