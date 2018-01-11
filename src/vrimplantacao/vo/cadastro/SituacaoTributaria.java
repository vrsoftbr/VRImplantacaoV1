package vrimplantacao.vo.cadastro;

public enum SituacaoTributaria {

    TRIBUTADO(0, "00 - Tributado integralmente"),
    TRIBUTADO_ICMS_ST(10, "10 - Tributado e com cobrança do ICMS ST"),
    REDUCAO_BASE_CALCULO(20, "20 - Com redução de base de cálculo"),
    ISENTO_ICMS_ST(30, "30 - Isento ou não tributado e com cobrança do ICMS ST"),
    ISENTO(40, "40 - Isento"),
    NAO_TRIBUTADO(41, "41 - Não tributado"),
    SUSPENSAO(50, "50 - Suspensão"),
    DIFERIMENTO(51, "51 - Diferimento"),
    SUBSTITUIDO(60, "60 - Substituido"),
    REDUCAO_BASE_CALCULO_ICMS_ST(70, "70 - Com redução de base de cálculo e cobrança do ICMS ST"),
    OUTRAS(90, "90 - Outras");
    private int id = 0;
    private String descricao = "";

    private SituacaoTributaria(int i_id, String i_descricao) {
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
