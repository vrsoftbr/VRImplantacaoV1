package vrimplantacao.vo.notafiscal;

public enum SituacaoReposicao {

    DIGITANDO(0),
    DIGITADO(1),
    PREPARANDO(2),
    FINALIZADO(3);
    private int id = 0;

    private SituacaoReposicao(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}