package vrimplantacao2.vo.enums;

/**
 * Representa os valores da tabela norma de compra.
 * @author leandro
 */
public enum NormaCompra {
    
    CAIXA (1),
    CAMADA (2),
    PALETE (3);
    
    private int id;

    private NormaCompra(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
}
