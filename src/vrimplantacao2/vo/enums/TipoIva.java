package vrimplantacao2.vo.enums;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public enum TipoIva {
    PERCENTUAL (0),
    VALOR (1);

    public static TipoIva getByTipo(String tipoIva) {
        switch (Utils.acertarTexto(tipoIva, 1)) {
            case "V": return VALOR;
            case "1": return VALOR;
            default : return PERCENTUAL;
        }
    }
    
    private int id;

    private TipoIva(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
