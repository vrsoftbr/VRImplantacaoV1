package vrimplantacao2.vo.enums;

/**
 * Situação atual do cheque.
 * @author Leandro
 */
public enum SituacaoCheque {
    ABERTO (0),
    BAIXADO (1);

    public static SituacaoCheque getById(int id) {
        for (SituacaoCheque s: values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }
    
    private int id;

    private SituacaoCheque(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
