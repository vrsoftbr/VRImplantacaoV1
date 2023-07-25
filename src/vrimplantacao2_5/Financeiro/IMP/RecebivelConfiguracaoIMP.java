package vrimplantacao2_5.Financeiro.IMP;

import java.util.Date;

/**
 *
 * @author Bruno
 */
public class RecebivelConfiguracaoIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private Integer id_loja;
    private Integer id_banco;
    private Integer id_tipoRecebivel;
    private Integer id_tipoVencimentoRecebivel;
    private Integer taxa;
    private boolean utilizaRegra;
    private boolean utilizaTabela;
    private boolean utilizaDataCorte;
    private String agencia;
    private String conta;
    private Integer quantidadeDiaFixo;
    private Integer diaSemanaCorte;
    private Date dataInicioCorte;
    private Integer outrasTaxas;
    private boolean diasUteis;
    private boolean proximoDiaUtil;
    private Integer periodoCorte;
    private Integer id_finalizadora;

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
     * @return the id_tipoRecebivel
     */
    public Integer getId_tipoRecebivel() {
        return id_tipoRecebivel;
    }

    /**
     * @param id_tipoRecebivel the id_tipoRecebivel to set
     */
    public void setId_tipoRecebivel(Integer id_tipoRecebivel) {
        this.id_tipoRecebivel = id_tipoRecebivel;
    }

    /**
     * @return the id_finalizadora
     */
    public Integer getId_finalizadora() {
        return id_finalizadora;
    }

    /**
     * @param id_finalizadora the id_finalizadora to set
     */
    public void setId_finalizadora(Integer id_finalizadora) {
        this.id_finalizadora = id_finalizadora;
    }

    /**
     * @return the id_loja
     */
    public Integer getId_loja() {
        return id_loja;
    }

    /**
     * @param id_loja the id_loja to set
     */
    public void setId_loja(Integer id_loja) {
        this.id_loja = id_loja;
    }

    /**
     * @return the id_banco
     */
    public Integer getId_banco() {
        return id_banco;
    }

    /**
     * @param id_banco the id_banco to set
     */
    public void setId_banco(Integer id_banco) {
        this.id_banco = id_banco;
    }

    /**
     * @return the id_tipoVencimentoRecebivel
     */
    public Integer getId_tipoVencimentoRecebivel() {
        return id_tipoVencimentoRecebivel;
    }

    /**
     * @param id_tipoVencimentoRecebivel the id_tipoVencimentoRecebivel to set
     */
    public void setId_tipoVencimentoRecebivel(Integer id_tipoVencimentoRecebivel) {
        this.id_tipoVencimentoRecebivel = id_tipoVencimentoRecebivel;
    }

    /**
     * @return the taxa
     */
    public Integer getTaxa() {
        return taxa;
    }

    /**
     * @param taxa the taxa to set
     */
    public void setTaxa(Integer taxa) {
        this.taxa = taxa;
    }

    /**
     * @return the utilizaRegra
     */
    public boolean isUtilizaRegra() {
        return utilizaRegra;
    }

    /**
     * @param utilizaRegra the utilizaRegra to set
     */
    public void setUtilizaRegra(boolean utilizaRegra) {
        this.utilizaRegra = utilizaRegra;
    }

    /**
     * @return the utilizaTabela
     */
    public boolean isUtilizaTabela() {
        return utilizaTabela;
    }

    /**
     * @param utilizaTabela the utilizaTabela to set
     */
    public void setUtilizaTabela(boolean utilizaTabela) {
        this.utilizaTabela = utilizaTabela;
    }

    /**
     * @return the utilizaDataCorte
     */
    public boolean isUtilizaDataCorte() {
        return utilizaDataCorte;
    }

    /**
     * @param utilizaDataCorte the utilizaDataCorte to set
     */
    public void setUtilizaDataCorte(boolean utilizaDataCorte) {
        this.utilizaDataCorte = utilizaDataCorte;
    }

    /**
     * @return the agencia
     */
    public String getAgencia() {
        return agencia;
    }

    /**
     * @param agencia the agencia to set
     */
    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    /**
     * @return the conta
     */
    public String getConta() {
        return conta;
    }

    /**
     * @param conta the conta to set
     */
    public void setConta(String conta) {
        this.conta = conta;
    }

    /**
     * @return the quantidadeDiaFixo
     */
    public Integer getQuantidadeDiaFixo() {
        return quantidadeDiaFixo;
    }

    /**
     * @param quantidadeDiaFixo the quantidadeDiaFixo to set
     */
    public void setQuantidadeDiaFixo(Integer quantidadeDiaFixo) {
        this.quantidadeDiaFixo = quantidadeDiaFixo;
    }

    /**
     * @return the diaSemanaCorte
     */
    public Integer getDiaSemanaCorte() {
        return diaSemanaCorte;
    }

    /**
     * @param diaSemanaCorte the diaSemanaCorte to set
     */
    public void setDiaSemanaCorte(Integer diaSemanaCorte) {
        this.diaSemanaCorte = diaSemanaCorte;
    }

    /**
     * @return the dataInicioCorte
     */
    public Date getDataInicioCorte() {
        return dataInicioCorte;
    }

    /**
     * @param dataInicioCorte the dataInicioCorte to set
     */
    public void setDataInicioCorte(Date dataInicioCorte) {
        this.dataInicioCorte = dataInicioCorte;
    }

    /**
     * @return the outrasTaxas
     */
    public Integer getOutrasTaxas() {
        return outrasTaxas;
    }

    /**
     * @param outrasTaxas the outrasTaxas to set
     */
    public void setOutrasTaxas(Integer outrasTaxas) {
        this.outrasTaxas = outrasTaxas;
    }

    /**
     * @return the diasUteis
     */
    public boolean isDiasUteis() {
        return diasUteis;
    }

    /**
     * @param diasUteis the diasUteis to set
     */
    public void setDiasUteis(boolean diasUteis) {
        this.diasUteis = diasUteis;
    }

    /**
     * @return the proximoDiaUtil
     */
    public boolean isProximoDiaUtil() {
        return proximoDiaUtil;
    }

    /**
     * @param proximoDiaUtil the proximoDiaUtil to set
     */
    public void setProximoDiaUtil(boolean proximoDiaUtil) {
        this.proximoDiaUtil = proximoDiaUtil;
    }

    /**
     * @return the periodoCorte
     */
    public Integer getPeriodoCorte() {
        return periodoCorte;
    }

    /**
     * @param periodoCorte the periodoCorte to set
     */
    public void setPeriodoCorte(Integer periodoCorte) {
        this.periodoCorte = periodoCorte;
    }

}
