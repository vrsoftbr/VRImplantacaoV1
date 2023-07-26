package vrimplantacao2_5.Financeiro.VO;

public class FinalizadoraConfiguracaoVO {

    private Integer id;
    private Integer id_loja;
    private Integer id_finalizadora;
    private boolean aceitaTroco;
    private boolean aceitaRetirada;
    private boolean aceitaAbastecimento;
    private boolean aceitaRecebimento;
    private boolean utilizaContraVale;
    private boolean retiradaTotal;
    private Integer valorMaximoTroco;
    private Integer juros;
    private Integer tipoMaximoTroco;
    private boolean aceitaRetiradaCf;
    private boolean retiradaTotalCf;
    private boolean utilizado;
    private boolean avisaRetirada;

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
     * @return the aceitaTroco
     */
    public boolean isAceitaTroco() {
        return aceitaTroco;
    }

    /**
     * @param aceitaTroco the aceitaTroco to set
     */
    public void setAceitaTroco(boolean aceitaTroco) {
        this.aceitaTroco = aceitaTroco;
    }

    /**
     * @return the aceitaRetiradad
     */
    public boolean isAceitaRetirada() {
        return aceitaRetirada;
    }

    /**
     * @param aceitaRetirada
     */
    public void setAceitaRetirada(boolean aceitaRetirada) {
        this.aceitaRetirada = aceitaRetirada;
    }

    /**
     * @return the aceitaAbastecimento
     */
    public boolean isAceitaAbastecimento() {
        return aceitaAbastecimento;
    }

    /**
     * @param aceitaAbastecimento the aceitaAbastecimento to set
     */
    public void setAceitaAbastecimento(boolean aceitaAbastecimento) {
        this.aceitaAbastecimento = aceitaAbastecimento;
    }

    /**
     * @return the utilizaContraVale
     */
    public boolean isUtilizaContraVale() {
        return utilizaContraVale;
    }

    /**
     * @param utilizaContraVale the utilizaContraVale to set
     */
    public void setUtilizaContraVale(boolean utilizaContraVale) {
        this.utilizaContraVale = utilizaContraVale;
    }

    /**
     * @return the retiradaTotal
     */
    public boolean isRetiradaTotal() {
        return retiradaTotal;
    }

    /**
     * @param retiradaTotal the retiradaTotal to set
     */
    public void setRetiradaTotal(boolean retiradaTotal) {
        this.retiradaTotal = retiradaTotal;
    }

    /**
     * @return the valorMaximoTroco
     */
    public Integer getValorMaximoTroco() {
        return valorMaximoTroco;
    }

    /**
     * @param valorMaximoTroco the valorMaximoTroco to set
     */
    public void setValorMaximoTroco(Integer valorMaximoTroco) {
        this.valorMaximoTroco = valorMaximoTroco;
    }

    /**
     * @return the juros
     */
    public Integer getJuros() {
        return juros;
    }

    /**
     * @param juros the juros to set
     */
    public void setJuros(Integer juros) {
        this.juros = juros;
    }

    /**
     * @return the tipoMaximoTroco
     */
    public Integer getTipoMaximoTroco() {
        return tipoMaximoTroco;
    }

    /**
     * @param tipoMaximoTroco the tipoMaximoTroco to set
     */
    public void setTipoMaximoTroco(Integer tipoMaximoTroco) {
        this.tipoMaximoTroco = tipoMaximoTroco;
    }

    /**
     * @return the aceitaRetiradaCf
     */
    public boolean isAceitaRetiradaCf() {
        return aceitaRetiradaCf;
    }

    /**
     * @param aceitaRetiradaCf the aceitaRetiradaCf to set
     */
    public void setAceitaRetiradaCf(boolean aceitaRetiradaCf) {
        this.aceitaRetiradaCf = aceitaRetiradaCf;
    }

    /**
     * @return the retiradaTotalCf
     */
    public boolean isRetiradaTotalCf() {
        return retiradaTotalCf;
    }

    /**
     * @param retiradaTotalCf the retiradaTotalCf to set
     */
    public void setRetiradaTotalCf(boolean retiradaTotalCf) {
        this.retiradaTotalCf = retiradaTotalCf;
    }

    /**
     * @return the utilizado
     */
    public boolean isUtilizado() {
        return utilizado;
    }

    /**
     * @param utilizado the utilizado to set
     */
    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }

    /**
     * @return the avisaRetirada
     */
    public boolean isAvisaRetirada() {
        return avisaRetirada;
    }

    /**
     * @param avisaRetirada the avisaRetirada to set
     */
    public void setAvisaRetirada(boolean avisaRetirada) {
        this.avisaRetirada = avisaRetirada;
    }

    /**
     * @return the aceitaRecebimento
     */
    public boolean isAceitaRecebimento() {
        return aceitaRecebimento;
    }

    /**
     * @param aceitaRecebimento the aceitaRecebimento to set
     */
    public void setAceitaRecebimento(boolean aceitaRecebimento) {
        this.aceitaRecebimento = aceitaRecebimento;
    }

}
