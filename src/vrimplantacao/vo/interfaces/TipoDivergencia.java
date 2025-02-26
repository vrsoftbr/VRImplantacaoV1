package vrimplantacao.vo.interfaces;

public enum TipoDivergencia {

    ERRO(0),
    ALERTA(1);
    private int id = 0;

    private TipoDivergencia(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
    
    public static TipoDivergencia getById(int id) {
        for (TipoDivergencia div: values()) {
            if (id == div.id) {
                return div;
            }
        }
        return null;
    }
    
}
