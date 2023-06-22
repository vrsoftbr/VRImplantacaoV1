package vrimplantacao2_5.vo.enums;

import vrimplantacao.utils.Utils;

/**
 *
 * @author WagnerSales
 */
public enum TipoCompra {

    NENHUM(0),
    CROSSDOCKING(1),
    CENTRALIZADO(2),
    DEPOSITO(3),
    LOJA(4);

    private final int id;

    private TipoCompra(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
