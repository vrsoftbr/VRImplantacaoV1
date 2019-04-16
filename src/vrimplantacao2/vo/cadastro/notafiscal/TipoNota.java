package vrimplantacao2.vo.cadastro.notafiscal;

/**
 *
 * @author Leandro
 */
public enum TipoNota {
    
    NORMAL (0),
    COMPLEMENTO (1),
    NFE_AJUSTE (2),
    EXTEMPORANEA (3);

    public static TipoNota get(int id) {
        for (TipoNota tn: values()) {
            if (tn.getId() == id) {
                return tn;
            }
        }
        return null;
    }

    private int id;

    private TipoNota(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
