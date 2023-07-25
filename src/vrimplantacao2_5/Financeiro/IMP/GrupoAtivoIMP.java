package vrimplantacao2_5.Financeiro.IMP;

/**
 *
 * @author Bruno
 */
public class GrupoAtivoIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String descricao;
    private Integer id_contaContabilAtivo;
    private Integer id_contaContabilDepreciacao;
    private Integer id_contaCOntabilDespesaDepreciacao;
    private Integer id_contaContabilCustoDepreciacao;

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
     * @return the id_contaContabilAtivo
     */
    public Integer getId_contaContabilAtivo() {
        return id_contaContabilAtivo;
    }

    /**
     * @param id_contaContabilAtivo the id_contaContabilAtivo to set
     */
    public void setId_contaContabilAtivo(Integer id_contaContabilAtivo) {
        this.id_contaContabilAtivo = id_contaContabilAtivo;
    }

    /**
     * @return the id_contaContabilDepreciacao
     */
    public Integer getId_contaContabilDepreciacao() {
        return id_contaContabilDepreciacao;
    }

    /**
     * @param id_contaContabilDepreciacao the id_contaContabilDepreciacao to set
     */
    public void setId_contaContabilDepreciacao(Integer id_contaContabilDepreciacao) {
        this.id_contaContabilDepreciacao = id_contaContabilDepreciacao;
    }

    /**
     * @return the id_contaCOntabilDespesaDepreciacao
     */
    public Integer getId_contaCOntabilDespesaDepreciacao() {
        return id_contaCOntabilDespesaDepreciacao;
    }

    /**
     * @param id_contaCOntabilDespesaDepreciacao the
     * id_contaCOntabilDespesaDepreciacao to set
     */
    public void setId_contaCOntabilDespesaDepreciacao(Integer id_contaCOntabilDespesaDepreciacao) {
        this.id_contaCOntabilDespesaDepreciacao = id_contaCOntabilDespesaDepreciacao;
    }

    /**
     * @return the id_contaContabilCustoDepreciacao
     */
    public Integer getId_contaContabilCustoDepreciacao() {
        return id_contaContabilCustoDepreciacao;
    }

    /**
     * @param id_contaContabilCustoDepreciacao the
     * id_contaContabilCustoDepreciacao to set
     */
    public void setId_contaContabilCustoDepreciacao(Integer id_contaContabilCustoDepreciacao) {
        this.id_contaContabilCustoDepreciacao = id_contaContabilCustoDepreciacao;
    }

}
