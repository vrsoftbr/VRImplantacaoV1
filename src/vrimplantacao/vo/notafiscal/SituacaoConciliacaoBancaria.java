package vrimplantacao.vo.notafiscal;

public enum SituacaoConciliacaoBancaria {

    NAO_FINALIZADO(0),
    FINALIZADO(1);
    private int id = 0;

    private SituacaoConciliacaoBancaria(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}