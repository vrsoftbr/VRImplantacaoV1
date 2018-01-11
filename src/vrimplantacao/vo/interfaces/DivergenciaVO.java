package vrimplantacao.vo.interfaces;

public class DivergenciaVO {

    public int tipo = 0;
    public String descricao = "";

    public DivergenciaVO(String i_descricao, int i_tipo) {
        this.descricao = i_descricao;
        this.tipo = i_tipo;
    }
}
