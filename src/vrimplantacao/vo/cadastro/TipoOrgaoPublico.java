package vrimplantacao.vo.cadastro;

public enum TipoOrgaoPublico {

    NENHUM(0),
    ESTADUAL(1),
    FEDERAL(2);
    private int id = 0;

    private TipoOrgaoPublico(int i_id) {
        this.id = i_id;
    }

    public int getId() {
        return id;
    }
}