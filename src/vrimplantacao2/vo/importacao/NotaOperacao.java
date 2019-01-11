package vrimplantacao2.vo.importacao;

/**
 * Operação da nota fiscal.
 */
public enum NotaOperacao {
    ENTRADA(0), SAIDA(1);
    private final int id;

    private NotaOperacao(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
