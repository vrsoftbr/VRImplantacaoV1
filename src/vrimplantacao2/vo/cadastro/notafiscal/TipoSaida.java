package vrimplantacao2.vo.cadastro.notafiscal;

/**
 *
 * @author Leandro
 */
public enum TipoSaida {
    VENDA_MERCADORIA (0);
    
    private int id;

    private TipoSaida(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
