package vrimplantacao2.vo.cadastro.convenio.empresa;

/**
 *
 * @author Leandro
 */
public enum TipoTerminoRenovacao {
    NENHUM (-1),
    FIXO (0),
    FIM_MES (1);
    
    private int id;

    private TipoTerminoRenovacao(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
