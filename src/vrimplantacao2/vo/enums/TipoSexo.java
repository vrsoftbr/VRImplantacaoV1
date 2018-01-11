package vrimplantacao2.vo.enums;

/**
 * Sexo de um pessoa.
 * @author Leandro
 */
public enum TipoSexo {
    FEMININO (0),
    MASCULINO (1);
    
    private final int id;

    private TipoSexo(int id) {
        this.id = id;
    }
    
    public int getID(){
        return this.id;
    }
}
