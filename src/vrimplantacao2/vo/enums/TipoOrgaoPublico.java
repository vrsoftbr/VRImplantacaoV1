package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public enum TipoOrgaoPublico {
    NENHUM,
    ESTADUAL,
    FEDERAL;
    
    public int getIndex() {
        return this.ordinal();
    }
}
