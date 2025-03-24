package vrimplantacao2.vo.enums;

/**
 * Regime fiscal de uma empresa.
 *
 * @author Leandro
 */
public enum TipoEmpresa {

    LUCRO_PRESUMIDO(1, "LUCRO PRESUMIDO"),
    PRODUTOR_RURAL_JURIDICO(2, "PRODUTOR RURAL PES. JURIDICA"),
    LUCRO_REAL(3, "LUCRO REAL"),
    LUCRO_REAL_ESTIMADO(4, "LUCRO REAL ESTIMADO"),
    LUCRO_REAL_ESTIMADO_OP(5, "LUCRO REAL ESTIMADO OP."),
    SOCIEDADE_CIVIL(6, "SOCIEDADE CIVIL"),
    PESSOA_FISICA(7, "PESSOA FISICA"),
    ME_SIMPLES(8, "ME - SIMPLES"),
    EPP_SIMPLES(9, "EPP - SIMPLES"),
    EIRELI(10, "EIRELI"),
    MEI(11, "MEI"),
    PRODUTOR_RURAL_FISICA(12, "PRODUTOR RURAL PES. FISICA");

    public static TipoEmpresa getByDescricao(String descricao) {
        for (TipoEmpresa tipoEmpresa : values()) {
            if (tipoEmpresa.descricao.equalsIgnoreCase(descricao)) {
                return tipoEmpresa;
            }
        }
        return LUCRO_REAL;
    }

    private int id;
    private String descricao;

    private TipoEmpresa(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }

}
