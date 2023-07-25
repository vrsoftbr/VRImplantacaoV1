package vrimplantacao2_5.Financeiro.IMP;

/**
 *
 * @author Bruno
 */
public class CfopIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String cfop;
    private String descricao;
    private boolean foraEstado;
    private boolean substituido;
    private Integer TipoEntradaSaida;
    private boolean geraIcms;
    private boolean bonificacao;
    private boolean devolucao;
    private boolean vendaEcf;
    private boolean devolucaoCliente;
    private boolean servico;
    private boolean fabricacaoPropria;
    private boolean exportacao = false;

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
     * @return the foraEstado
     */
    public boolean isForaEstado() {
        return foraEstado;
    }

    /**
     * @param foraEstado the foraEstado to set
     */
    public void setForaEstado(boolean foraEstado) {
        this.foraEstado = foraEstado;
    }

    /**
     * @return the substituido
     */
    public boolean isSubstituido() {
        return substituido;
    }

    /**
     * @param substituido the substituido to set
     */
    public void setSubstituido(boolean substituido) {
        this.substituido = substituido;
    }

    /**
     * @return the geraIcms
     */
    public boolean isGeraIcms() {
        return geraIcms;
    }

    /**
     * @param geraIcms the geraIcms to set
     */
    public void setGeraIcms(boolean geraIcms) {
        this.geraIcms = geraIcms;
    }

    /**
     * @return the bonificacao
     */
    public boolean isBonificacao() {
        return bonificacao;
    }

    /**
     * @param bonificacao the bonificacao to set
     */
    public void setBonificacao(boolean bonificacao) {
        this.bonificacao = bonificacao;
    }

    /**
     * @return the devolucao
     */
    public boolean isDevolucao() {
        return devolucao;
    }

    /**
     * @param devolucao the devolucao to set
     */
    public void setDevolucao(boolean devolucao) {
        this.devolucao = devolucao;
    }

    /**
     * @return the vendaEcf
     */
    public boolean isVendaEcf() {
        return vendaEcf;
    }

    /**
     * @param vendaEcf the vendaEcf to set
     */
    public void setVendaEcf(boolean vendaEcf) {
        this.vendaEcf = vendaEcf;
    }

    /**
     * @return the devolucaoCliente
     */
    public boolean isDevolucaoCliente() {
        return devolucaoCliente;
    }

    /**
     * @param devolucaoCliente the devolucaoCliente to set
     */
    public void setDevolucaoCliente(boolean devolucaoCliente) {
        this.devolucaoCliente = devolucaoCliente;
    }

    /**
     * @return the servico
     */
    public boolean isServico() {
        return servico;
    }

    /**
     * @param servico the servico to set
     */
    public void setServico(boolean servico) {
        this.servico = servico;
    }

    /**
     * @return the fabricacaoPropria
     */
    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    /**
     * @param fabricacaoPropria the fabricacaoPropria to set
     */
    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }

    /**
     * @return the exportacao
     */
    public boolean isExportacao() {
        return exportacao;
    }

    /**
     * @param exportacao the exportacao to set
     */
    public void setExportacao(boolean exportacao) {
        this.exportacao = exportacao;
    }

    /**
     * @return the TipoEntradaSaida
     */
    public Integer getTipoEntradaSaida() {
        return TipoEntradaSaida;
    }

    /**
     * @param TipoEntradaSaida the TipoEntradaSaida to set
     */
    public void setTipoEntradaSaida(Integer TipoEntradaSaida) {
        this.TipoEntradaSaida = TipoEntradaSaida;
    }

    /**
     * @return the cfop
     */
    public String getCfop() {
        return cfop;
    }

    /**
     * @param cfop the cfop to set
     */
    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

}
