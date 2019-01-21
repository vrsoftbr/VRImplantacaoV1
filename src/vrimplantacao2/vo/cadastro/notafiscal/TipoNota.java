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

    private int id;

    private TipoNota(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
