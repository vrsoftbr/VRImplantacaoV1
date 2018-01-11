package vrimplantacao.vo.notafiscal;

public enum TipoCrt {

    SIMPLES_NACIONAL(1),
    SIMPLES_NACIONAL_EXCESSO_SUBLIMITE(2),
    REGIME_NORMAL(3);
    private int id = 0;

    private TipoCrt(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}