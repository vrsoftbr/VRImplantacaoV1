package vrimplantacao.dao.cadastro;

public enum SituacaoCadastro {

    EXCLUIDO(0, "EXCLUIDO"),
    ATIVO(1, "ATIVO");
    private int id = 0;
    private String descricao = "";

    private SituacaoCadastro(int i_id, String i_descricao) {
        this.id = i_id;
        this.descricao = i_descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
