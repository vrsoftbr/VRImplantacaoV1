package vrimplantacao2.vo.enums;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public enum TipoAtacado {
    
    EMBALAGEM(1),
    QTDE_TOTAL(2),
    QTDE_EMBALAGEM(3);
    
    private final int id;

    private TipoAtacado(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public static TipoAtacado getById(int id) {
        for (TipoAtacado v: values()) {
            if (id == v.id) {
                return v;
            }
        }
        return null;
    }
    
    public static TipoAtacado getById(String id) {
        return getById(Utils.stringToInt(id));
    }
}
