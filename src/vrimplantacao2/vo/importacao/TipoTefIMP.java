package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Implantacao
 */
public class TipoTefIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String descricao;
    private Integer tipocomunicao;
    private String bandeira;
    private boolean imprimeCupom;
    private SituacaoCadastro situacaoCadastro;
    private Integer numeroParcela;
    private Integer id_autorizadora;
    
    

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
     * @return the situacaoCadastro
     */
    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    /**
     * @param situacaoCadastro the situacaoCadastro to set
     */
    public void setSituacaoCadastro(int situacaoCadastro) {
        this.situacaoCadastro = SituacaoCadastro.getById(situacaoCadastro);
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
     * @return the tipocomunicao
     */
    public Integer getTipocomunicao() {
        return tipocomunicao;
    }

    /**
     * @param tipocomunicao the tipocomunicao to set
     */
    public void setTipocomunicao(Integer tipocomunicao) {
        this.tipocomunicao = tipocomunicao;
    }

    /**
     * @return the bandeira
     */
    public String getBandeira() {
        return bandeira;
    }

    /**
     * @param bandeira the bandeira to set
     */
    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    /**
     * @return the imprimeCupom
     */
    public boolean isImprimeCupom() {
        return imprimeCupom;
    }

    /**
     * @param imprimeCupom the imprimeCupom to set
     */
    public void setImprimeCupom(boolean imprimeCupom) {
        this.imprimeCupom = imprimeCupom;
    }

    /**
     * @return the numeroParcela
     */
    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    /**
     * @param numeroParcela the numeroParcela to set
     */
    public void setNumeroParcela(Integer numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    /**
     * @return the id_autorizadora
     */
    public Integer getId_autorizadora() {
        return id_autorizadora;
    }

    /**
     * @param id_autorizadora the id_autorizadora to set
     */
    public void setId_autorizadora(Integer id_autorizadora) {
        this.id_autorizadora = id_autorizadora;
    }

}
