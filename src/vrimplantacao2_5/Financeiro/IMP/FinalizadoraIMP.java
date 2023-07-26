package vrimplantacao2_5.Financeiro.IMP;

/**
 *
 * @author Bruno
 */
public class FinalizadoraIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String descricao;
    private Integer id_funcao;
    private boolean consultaCheque;
    private boolean consultaCartao;
    private boolean consultaTef;
    private boolean consultaTicket;
    private boolean consultaConvenio;
    private boolean verificaPlano;
    private boolean consultaCreditoRotativo;
    private boolean consultaNotaFiscal;

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
     * @return the id_funcao
     */
    public Integer getId_funcao() {
        return id_funcao;
    }

    /**
     * @param id_funcao the id_funcao to set
     */
    public void setId_funcao(Integer id_funcao) {
        this.id_funcao = id_funcao;
    }

    /**
     * @return the consultaCheque
     */
    public boolean isConsultaCheque() {
        return consultaCheque;
    }

    /**
     * @param consultaCheque the consultaCheque to set
     */
    public void setConsultaCheque(boolean consultaCheque) {
        this.consultaCheque = consultaCheque;
    }

    /**
     * @return the consultaCartao
     */
    public boolean isConsultaCartao() {
        return consultaCartao;
    }

    /**
     * @param consultaCartao the consultaCartao to set
     */
    public void setConsultaCartao(boolean consultaCartao) {
        this.consultaCartao = consultaCartao;
    }

    /**
     * @return the consultaTef
     */
    public boolean isConsultaTef() {
        return consultaTef;
    }

    /**
     * @param consultaTef the consultaTef to set
     */
    public void setConsultaTef(boolean consultaTef) {
        this.consultaTef = consultaTef;
    }

    /**
     * @return the consultaTicket
     */
    public boolean isConsultaTicket() {
        return consultaTicket;
    }

    /**
     * @param consultaTicket the consultaTicket to set
     */
    public void setConsultaTicket(boolean consultaTicket) {
        this.consultaTicket = consultaTicket;
    }

    /**
     * @return the consultaConvenio
     */
    public boolean isConsultaConvenio() {
        return consultaConvenio;
    }

    /**
     * @param consultaConvenio the consultaConvenio to set
     */
    public void setConsultaConvenio(boolean consultaConvenio) {
        this.consultaConvenio = consultaConvenio;
    }

    /**
     * @return the verificaPlano
     */
    public boolean isVerificaPlano() {
        return verificaPlano;
    }

    /**
     * @param verificaPlano the verificaPlano to set
     */
    public void setVerificaPlano(boolean verificaPlano) {
        this.verificaPlano = verificaPlano;
    }

    /**
     * @return the consultaCreditoRotativo
     */
    public boolean isConsultaCreditoRotativo() {
        return consultaCreditoRotativo;
    }

    /**
     * @param consultaCreditoRotativo the consultaCreditoRotativo to set
     */
    public void setConsultaCreditoRotativo(boolean consultaCreditoRotativo) {
        this.consultaCreditoRotativo = consultaCreditoRotativo;
    }

    /**
     * @return the consultaNotaFiscal
     */
    public boolean isConsultaNotaFiscal() {
        return consultaNotaFiscal;
    }

    /**
     * @param consultaNotaFiscal the consultaNotaFiscal to set
     */
    public void setConsultaNotaFiscal(boolean consultaNotaFiscal) {
        this.consultaNotaFiscal = consultaNotaFiscal;
    }

}
