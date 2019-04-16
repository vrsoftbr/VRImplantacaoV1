package vrimplantacao2.vo.importacao;

/**
 * Operação da nota fiscal.
 */
public enum NotaOperacao {
    ENTRADA(0), SAIDA(1);

    public static NotaOperacao get(int id) {
        for (NotaOperacao no: values()) {
            if (no.getId() == id) {
                return no;
            }
        }
        return null;
    }
    private final int id;

    private NotaOperacao(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
