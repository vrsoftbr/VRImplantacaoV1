package vrimplantacao2.vo.enums;

/**
 * Estado Cívil de uma pessoa.
 * @author Leandro
 */
public enum TipoEstadoCivil {
    NAO_INFORMADO (0,"NAO INFORMADO"),
    SOLTEIRO (1,"SOLTEIRO"),
    CASADO (2,"CASADO"),
    VIUVO (3,"VIUVO"),
    AMAZIADO (4,"AMAZIADO"),
    OUTROS (5,"OUTROS"),
    DIVORCIADO (6,"DIVORCIADO");

    public static TipoEstadoCivil getById(int id) {
        for (TipoEstadoCivil est: values()) {
            if (id == est.getID()) {
                return est;
            }
        }
        return NAO_INFORMADO;
    }
    
    private final int ID;
    private final String descricao;

    private TipoEstadoCivil(int ID, String descricao) {
        this.ID = ID;
        this.descricao = descricao;
    }

    public int getID() {
        return ID;
    }

    public String getDescricao() {
        return descricao;
    }
}
