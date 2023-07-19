package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Bruno
 */
public class HistoricoPadraoIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String descricao;
    private SituacaoCadastro id_situacaoCadastro;

    /**
     * @return the importSistema
     */
    public String getImportSistema() {
        return importSistema;
    }

    /**
     * @param importSistema the importSistema to set
     */
    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    /**
     * @return the importLoja
     */
    public String getImportLoja() {
        return importLoja;
    }

    /**
     * @param importLoja the importLoja to set
     */
    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
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

   
}
