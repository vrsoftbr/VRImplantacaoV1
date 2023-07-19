package vrimplantacao2.vo.cadastro;

import vrimplantacao2.vo.enums.SituacaoCadastro;

public class HistoricoPadraoVO {

    private Integer id;
    private String descricao;
    private SituacaoCadastro id_situacaoCadastro;


    /**
     * @return the id_situacaoCadastro
     */
    public SituacaoCadastro getId_situacaoCadastro() {
        return id_situacaoCadastro;
    }

    /**
     * @param id_situacaoCadastro the id_situacaoCadastro to set
     */
    public void setId_situacaoCadastro(SituacaoCadastro id_situacaoCadastro) {
        this.id_situacaoCadastro = id_situacaoCadastro;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
