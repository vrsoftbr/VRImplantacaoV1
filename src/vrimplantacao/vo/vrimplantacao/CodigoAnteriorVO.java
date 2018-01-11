package vrimplantacao.vo.vrimplantacao;

import vrimplantacao.utils.Utils;

public class CodigoAnteriorVO {  
    private String codigoAnteriorStr = null;
    public double codigoanterior = 0;
    public double codigoatual = 0;
    public long barras = -1;
    public int naturezareceita = -1;
    public int piscofinscredito = -1;
    public int piscofinsdebito = -1;
    public String ref_icmsdebito = "";
    public boolean e_balanca = false;
    public int codigobalanca = -1;
    public double custosemimposto = -1;
    public double custocomimposto = -1;
    public double margem = -1;
    public double precovenda = 0;
    public int referencia = -1;
    public String ncm = "";
    public double estoque = -1;
    public int id_loja = 0;
    private String cest = "";
    private String codigoAuxiliar = "";
    private String codAnterior = "";
    
    /** campos pra acertar produtos do Freitas **/
    public double codigo_rms = 0;
    public long codigobarras_rms = 0;
    public String descricao_rms = "";

    public String getCodigoAnteriorStr() {
        return codigoAnteriorStr;
    }

    public void setCodigoAnteriorStr(String codigoAnteriorStr) {
        this.codigoAnteriorStr = codigoAnteriorStr;
    } 
    
    public double getCodigoanterior() {
        return codigoanterior;
    }

    public void setCodigoanterior(double codigoanterior) {
        this.codigoanterior = codigoanterior;
    }

    public double getCodigoatual() {
        return codigoatual;
    }

    public void setCodigoatual(double codigoatual) {
        this.codigoatual = codigoatual;
    }

    public long getBarras() {
        return barras;
    }

    public void setBarras(long barras) {
        this.barras = barras;
    }

    public int getNaturezareceita() {
        return naturezareceita;
    }

    public void setNaturezareceita(int naturezareceita) {
        this.naturezareceita = naturezareceita;
    }

    public int getPiscofinscredito() {
        return piscofinscredito;
    }

    public void setPiscofinscredito(int piscofinscredito) {
        this.piscofinscredito = piscofinscredito;
    }

    public int getPiscofinsdebito() {
        return piscofinsdebito;
    }

    public void setPiscofinsdebito(int piscofinsdebito) {
        this.piscofinsdebito = piscofinsdebito;
    }

    public String getRef_icmsdebito() {
        return ref_icmsdebito;
    }

    public void setRef_icmsdebito(String ref_icmsdebito) {
        this.ref_icmsdebito = ref_icmsdebito != null ? ref_icmsdebito.trim() : "";
    }

    public boolean isE_balanca() {
        return e_balanca;
    }

    public void setE_balanca(boolean e_balanca) {
        this.e_balanca = e_balanca;
    }

    public int getCodigobalanca() {
        return codigobalanca;
    }

    public void setCodigobalanca(int codigobalanca) {
        this.codigobalanca = codigobalanca;
    }

    public double getCustosemimposto() {
        return custosemimposto;
    }

    public void setCustosemimposto(double custosemimposto) {
        this.custosemimposto = custosemimposto;
    }

    public double getCustocomimposto() {
        return custocomimposto;
    }

    public void setCustocomimposto(double custocomimposto) {
        this.custocomimposto = custocomimposto;
    }

    public double getMargem() {
        return margem;
    }

    public void setMargem(double margem) {
        this.margem = margem;
    }

    public double getPrecovenda() {
        return precovenda;
    }

    public void setPrecovenda(double precovenda) {
        this.precovenda = precovenda;
    }

    public int getReferencia() {
        return referencia;
    }

    public void setReferencia(int referencia) {
        this.referencia = referencia;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = Utils.formataNumero(ncm, 8, "");
    }

    public double getEstoque() {
        return estoque;
    }

    public void setEstoque(double estoque) {
        this.estoque = Utils.arredondar(estoque, 3);
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public double getCodigo_rms() {
        return codigo_rms;
    }

    public void setCodigo_rms(double codigo_rms) {
        this.codigo_rms = codigo_rms;
    }

    public long getCodigobarras_rms() {
        return codigobarras_rms;
    }

    public void setCodigobarras_rms(long codigobarras_rms) {
        this.codigobarras_rms = codigobarras_rms;
    }

    public String getDescricao_rms() {
        return descricao_rms;
    }

    public void setDescricao_rms(String descricao_rms) {
        this.descricao_rms = descricao_rms;
    }

    public String getCest() {
        return cest;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    /**
     * @return the codAnterior
     */
    public String getCodAnterior() {
        return codAnterior;
    }

    /**
     * @param codAnterior the codAnterior to set
     */
    public void setCodAnterior(String codAnterior) {
        this.codAnterior = codAnterior;
    }
}