package vrimplantacao2_5.Financeiro.IMP;

import java.util.Date;

/**
 *
 * @author Bruno
 */
public class EcfIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private Integer id_loja;
    private Integer ecf;
    private String descricao;
    private Integer id_tipoMarca;
    private Integer id_tipoModelo;
    private Integer id_situacaoCadastro;
    private String numeroSerie;
    private String mfAdicional;
    private Integer numeroUsuario;
    private String tipoEcf;
    private String versaoSb;
    private Date datHoraGravacaoSb;
    private Date dataHoraCadastro;
    private boolean incidenciaDesconto;
    private Integer versaoBiblioteca;
    private boolean geraNfPaulista;
    private Integer id_tipoEstado;
    private String versao;
    private Date dataMovimento;
    private boolean cargaGData;
    private boolean cargaParam;
    private boolean cargaLayout;
    private boolean cargaImagem;
    private Integer id_tipoLayoutNotaPaulista;
    private boolean touch;
    private boolean alteradoPaf;
    private Date horaMovimento = null;
    private Integer id_tipoEmissor;
    private Integer id_modeloPdv;

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
     * @return the ecf
     */
    public Integer getEcf() {
        return ecf;
    }

    /**
     * @param ecf the ecf to set
     */
    public void setEcf(Integer ecf) {
        this.ecf = ecf;
    }

    /**
     * @return the id_tipoMarca
     */
    public Integer getId_tipoMarca() {
        return id_tipoMarca;
    }

    /**
     * @param id_tipoMarca the id_tipoMarca to set
     */
    public void setId_tipoMarca(Integer id_tipoMarca) {
        this.id_tipoMarca = id_tipoMarca;
    }

    /**
     * @return the id_tipoModelo
     */
    public Integer getId_tipoModelo() {
        return id_tipoModelo;
    }

    /**
     * @param id_tipoModelo the id_tipoModelo to set
     */
    public void setId_tipoModelo(Integer id_tipoModelo) {
        this.id_tipoModelo = id_tipoModelo;
    }

    /**
     * @return the id_situacaoCadastro
     */
    public Integer getId_situacaoCadastro() {
        return id_situacaoCadastro;
    }

    /**
     * @param id_situacaoCadastro the id_situacaoCadastro to set
     */
    public void setId_situacaoCadastro(Integer id_situacaoCadastro) {
        this.id_situacaoCadastro = id_situacaoCadastro;
    }

    /**
     * @return the numeroSerie
     */
    public String getNumeroSerie() {
        return numeroSerie;
    }

    /**
     * @param numeroSerie the numeroSerie to set
     */
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    /**
     * @return the mfAdicional
     */
    public String getMfAdicional() {
        return mfAdicional;
    }

    /**
     * @param mfAdicional the mfAdicional to set
     */
    public void setMfAdicional(String mfAdicional) {
        this.mfAdicional = mfAdicional;
    }

    /**
     * @return the numeroUsuario
     */
    public Integer getNumeroUsuario() {
        return numeroUsuario;
    }

    /**
     * @param numeroUsuario the numeroUsuario to set
     */
    public void setNumeroUsuario(Integer numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    /**
     * @return the tipoEcf
     */
    public String getTipoEcf() {
        return tipoEcf;
    }

    /**
     * @param tipoEcf the tipoEcf to set
     */
    public void setTipoEcf(String tipoEcf) {
        this.tipoEcf = tipoEcf;
    }

    /**
     * @return the versaoSb
     */
    public String getVersaoSb() {
        return versaoSb;
    }

    /**
     * @param versaoSb the versaoSb to set
     */
    public void setVersaoSb(String versaoSb) {
        this.versaoSb = versaoSb;
    }

    /**
     * @return the datHoraGravacaoSb
     */
    public Date getDatHoraGravacaoSb() {
        return datHoraGravacaoSb;
    }

    /**
     * @param datHoraGravacaoSb the datHoraGravacaoSb to set
     */
    public void setDatHoraGravacaoSb(Date datHoraGravacaoSb) {
        this.datHoraGravacaoSb = datHoraGravacaoSb;
    }

    /**
     * @return the dataHoraCadastro
     */
    public Date getDataHoraCadastro() {
        return dataHoraCadastro;
    }

    /**
     * @param dataHoraCadastro the dataHoraCadastro to set
     */
    public void setDataHoraCadastro(Date dataHoraCadastro) {
        this.dataHoraCadastro = dataHoraCadastro;
    }

    /**
     * @return the incidenciaDesconto
     */
    public boolean isIncidenciaDesconto() {
        return incidenciaDesconto;
    }

    /**
     * @param incidenciaDesconto the incidenciaDesconto to set
     */
    public void setIncidenciaDesconto(boolean incidenciaDesconto) {
        this.incidenciaDesconto = incidenciaDesconto;
    }

    /**
     * @return the versaoBiblioteca
     */
    public Integer getVersaoBiblioteca() {
        return versaoBiblioteca;
    }

    /**
     * @param versaoBiblioteca the versaoBiblioteca to set
     */
    public void setVersaoBiblioteca(Integer versaoBiblioteca) {
        this.versaoBiblioteca = versaoBiblioteca;
    }

    /**
     * @return the geraNfPaulista
     */
    public boolean isGeraNfPaulista() {
        return geraNfPaulista;
    }

    /**
     * @param geraNfPaulista the geraNfPaulista to set
     */
    public void setGeraNfPaulista(boolean geraNfPaulista) {
        this.geraNfPaulista = geraNfPaulista;
    }

    /**
     * @return the id_tipoEstado
     */
    public Integer getId_tipoEstado() {
        return id_tipoEstado;
    }

    /**
     * @param id_tipoEstado the id_tipoEstado to set
     */
    public void setId_tipoEstado(Integer id_tipoEstado) {
        this.id_tipoEstado = id_tipoEstado;
    }

    /**
     * @return the versao
     */
    public String getVersao() {
        return versao;
    }

    /**
     * @param versao the versao to set
     */
    public void setVersao(String versao) {
        this.versao = versao;
    }

    /**
     * @return the dataMovimento
     */
    public Date getDataMovimento() {
        return dataMovimento;
    }

    /**
     * @param dataMovimento the dataMovimento to set
     */
    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    /**
     * @return the cargaGData
     */
    public boolean isCargaGData() {
        return cargaGData;
    }

    /**
     * @param cargaGData the cargaGData to set
     */
    public void setCargaGData(boolean cargaGData) {
        this.cargaGData = cargaGData;
    }

    /**
     * @return the cargaParam
     */
    public boolean isCargaParam() {
        return cargaParam;
    }

    /**
     * @param cargaParam the cargaParam to set
     */
    public void setCargaParam(boolean cargaParam) {
        this.cargaParam = cargaParam;
    }

    /**
     * @return the cargaLayout
     */
    public boolean isCargaLayout() {
        return cargaLayout;
    }

    /**
     * @param cargaLayout the cargaLayout to set
     */
    public void setCargaLayout(boolean cargaLayout) {
        this.cargaLayout = cargaLayout;
    }

    /**
     * @return the cargaImagem
     */
    public boolean isCargaImagem() {
        return cargaImagem;
    }

    /**
     * @param cargaImagem the cargaImagem to set
     */
    public void setCargaImagem(boolean cargaImagem) {
        this.cargaImagem = cargaImagem;
    }

    /**
     * @return the id_tipoLayoutNotaPaulista
     */
    public Integer getId_tipoLayoutNotaPaulista() {
        return id_tipoLayoutNotaPaulista;
    }

    /**
     * @param id_tipoLayoutNotaPaulista the id_tipoLayoutNotaPaulista to set
     */
    public void setId_tipoLayoutNotaPaulista(Integer id_tipoLayoutNotaPaulista) {
        this.id_tipoLayoutNotaPaulista = id_tipoLayoutNotaPaulista;
    }

    /**
     * @return the touch
     */
    public boolean isTouch() {
        return touch;
    }

    /**
     * @param touch the touch to set
     */
    public void setTouch(boolean touch) {
        this.touch = touch;
    }

    /**
     * @return the alteradoPaf
     */
    public boolean isAlteradoPaf() {
        return alteradoPaf;
    }

    /**
     * @param alteradoPaf the alteradoPaf to set
     */
    public void setAlteradoPaf(boolean alteradoPaf) {
        this.alteradoPaf = alteradoPaf;
    }

    /**
     * @return the horaMovimento
     */
    public Date getHoraMovimento() {
        return horaMovimento;
    }

    /**
     * @param horaMovimento the horaMovimento to set
     */
    public void setHoraMovimento(Date horaMovimento) {
        this.horaMovimento = horaMovimento;
    }

    /**
     * @return the id_tipoEmissor
     */
    public Integer getId_tipoEmissor() {
        return id_tipoEmissor;
    }

    /**
     * @param id_tipoEmissor the id_tipoEmissor to set
     */
    public void setId_tipoEmissor(Integer id_tipoEmissor) {
        this.id_tipoEmissor = id_tipoEmissor;
    }

    /**
     * @return the id_modeloPdv
     */
    public Integer getId_modeloPdv() {
        return id_modeloPdv;
    }

    /**
     * @param id_modeloPdv the id_modeloPdv to set
     */
    public void setId_modeloPdv(Integer id_modeloPdv) {
        this.id_modeloPdv = id_modeloPdv;
    }

}
