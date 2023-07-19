package vrimplantacao2_5.tipoRecebivel.IMP;

import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Bruno
 */
public class TipoEntradaIMP {

    private String importSistema;
    private String importLoja;

    private Integer id;
    private String descricao;
    private SituacaoCadastro id_situacaoCadastro;
    private String tipo;
    private boolean atualizaCusto;
    private boolean atualizaEstoque;
    private boolean atualizaPedido;
    private boolean imprimeGuiaCega;
    private boolean imprimeDivergencia;
    private boolean atualizaPerda;
    private boolean notaProdutor;
    private boolean geraContrato;
    private boolean atualizaDataEntrada;
    private boolean utilizaCustoTabela;
    private boolean bonificacao;
    private boolean atualizaDivergenciaCusto;
    private boolean atualizaAdministracao;
    private boolean atualizaFiscal;
    private boolean atualizaPagar;
    private boolean atualizaTroca;
    private String serie;
    private String especie;
    private boolean atualizaEscrita;
    private Integer id_contaContabilFiscalDebito;
    private Integer id_historicoPadrao;
    private Integer id_contaContabilFiscalCredito;
    private boolean substituicao;
    private boolean foraEstado;
    private boolean verificaPedido;
    private Integer id_produto;
    private Integer planoConta1;
    private Integer planoConta2;
    private boolean geraVerba;
    private boolean contabilidadePadrao;
    private boolean contabiliza;
    private boolean creditaPisCofins;
    private Integer id_tipoBaseCalculoCredito;
    private boolean naoCreditaIcms;
    private boolean descargaPalete;
    private boolean ativoImobilizado;
    private Integer id_ativoGrupo;
    private boolean utilizaCentroCusto;
    private Integer id_aliquota;
    private boolean notaMei;
    private boolean contabilizaController360;
    private boolean utilizaCustoOrigem;
    
    

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

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the atualizaCusto
     */
    public boolean isAtualizaCusto() {
        return atualizaCusto;
    }

    /**
     * @param atualizaCusto the atualizaCusto to set
     */
    public void setAtualizaCusto(boolean atualizaCusto) {
        this.atualizaCusto = atualizaCusto;
    }

    /**
     * @return the atualizaEstoque
     */
    public boolean isAtualizaEstoque() {
        return atualizaEstoque;
    }

    /**
     * @param atualizaEstoque the atualizaEstoque to set
     */
    public void setAtualizaEstoque(boolean atualizaEstoque) {
        this.atualizaEstoque = atualizaEstoque;
    }

    /**
     * @return the atualizaPedido
     */
    public boolean isAtualizaPedido() {
        return atualizaPedido;
    }

    /**
     * @param atualizaPedido the atualizaPedido to set
     */
    public void setAtualizaPedido(boolean atualizaPedido) {
        this.atualizaPedido = atualizaPedido;
    }

    /**
     * @return the imprimeGuiaCega
     */
    public boolean isImprimeGuiaCega() {
        return imprimeGuiaCega;
    }

    /**
     * @param imprimeGuiaCega the imprimeGuiaCega to set
     */
    public void setImprimeGuiaCega(boolean imprimeGuiaCega) {
        this.imprimeGuiaCega = imprimeGuiaCega;
    }

    /**
     * @return the imprimeDivergencia
     */
    public boolean isImprimeDivergencia() {
        return imprimeDivergencia;
    }

    /**
     * @param imprimeDivergencia the imprimeDivergencia to set
     */
    public void setImprimeDivergencia(boolean imprimeDivergencia) {
        this.imprimeDivergencia = imprimeDivergencia;
    }

    /**
     * @return the atualizaPerda
     */
    public boolean isAtualizaPerda() {
        return atualizaPerda;
    }

    /**
     * @param atualizaPerda the atualizaPerda to set
     */
    public void setAtualizaPerda(boolean atualizaPerda) {
        this.atualizaPerda = atualizaPerda;
    }

    /**
     * @return the notaProdutor
     */
    public boolean isNotaProdutor() {
        return notaProdutor;
    }

    /**
     * @param notaProdutor the notaProdutor to set
     */
    public void setNotaProdutor(boolean notaProdutor) {
        this.notaProdutor = notaProdutor;
    }

    /**
     * @return the geraContrato
     */
    public boolean isGeraContrato() {
        return geraContrato;
    }

    /**
     * @param geraContrato the geraContrato to set
     */
    public void setGeraContrato(boolean geraContrato) {
        this.geraContrato = geraContrato;
    }

    /**
     * @return the atualizaDataEntrada
     */
    public boolean isAtualizaDataEntrada() {
        return atualizaDataEntrada;
    }

    /**
     * @param atualizaDataEntrada the atualizaDataEntrada to set
     */
    public void setAtualizaDataEntrada(boolean atualizaDataEntrada) {
        this.atualizaDataEntrada = atualizaDataEntrada;
    }

    /**
     * @return the utilizaCustoTabela
     */
    public boolean isUtilizaCustoTabela() {
        return utilizaCustoTabela;
    }

    /**
     * @param utilizaCustoTabela the utilizaCustoTabela to set
     */
    public void setUtilizaCustoTabela(boolean utilizaCustoTabela) {
        this.utilizaCustoTabela = utilizaCustoTabela;
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
     * @return the atualizaDivergenciaCusto
     */
    public boolean isAtualizaDivergenciaCusto() {
        return atualizaDivergenciaCusto;
    }

    /**
     * @param atualizaDivergenciaCusto the atualizaDivergenciaCusto to set
     */
    public void setAtualizaDivergenciaCusto(boolean atualizaDivergenciaCusto) {
        this.atualizaDivergenciaCusto = atualizaDivergenciaCusto;
    }

    /**
     * @return the atualizaAdministracao
     */
    public boolean isAtualizaAdministracao() {
        return atualizaAdministracao;
    }

    /**
     * @param atualizaAdministracao the atualizaAdministracao to set
     */
    public void setAtualizaAdministracao(boolean atualizaAdministracao) {
        this.atualizaAdministracao = atualizaAdministracao;
    }

    /**
     * @return the atualizaFiscal
     */
    public boolean isAtualizaFiscal() {
        return atualizaFiscal;
    }

    /**
     * @param atualizaFiscal the atualizaFiscal to set
     */
    public void setAtualizaFiscal(boolean atualizaFiscal) {
        this.atualizaFiscal = atualizaFiscal;
    }

    /**
     * @return the atualizaPagar
     */
    public boolean isAtualizaPagar() {
        return atualizaPagar;
    }

    /**
     * @param atualizaPagar the atualizaPagar to set
     */
    public void setAtualizaPagar(boolean atualizaPagar) {
        this.atualizaPagar = atualizaPagar;
    }

    /**
     * @return the atualizaTroca
     */
    public boolean isAtualizaTroca() {
        return atualizaTroca;
    }

    /**
     * @param atualizaTroca the atualizaTroca to set
     */
    public void setAtualizaTroca(boolean atualizaTroca) {
        this.atualizaTroca = atualizaTroca;
    }

    /**
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the especie
     */
    public String getEspecie() {
        return especie;
    }

    /**
     * @param especie the especie to set
     */
    public void setEspecie(String especie) {
        this.especie = especie;
    }

    /**
     * @return the atualizaEscrita
     */
    public boolean isAtualizaEscrita() {
        return atualizaEscrita;
    }

    /**
     * @param atualizaEscrita the atualizaEscrita to set
     */
    public void setAtualizaEscrita(boolean atualizaEscrita) {
        this.atualizaEscrita = atualizaEscrita;
    }

    /**
     * @return the id_contaContabilFiscalDebito
     */
    public Integer getId_contaContabilFiscalDebito() {
        return id_contaContabilFiscalDebito;
    }

    /**
     * @param id_contaContabilFiscalDebito the id_contaContabilFiscalDebito to set
     */
    public void setId_contaContabilFiscalDebito(Integer id_contaContabilFiscalDebito) {
        this.id_contaContabilFiscalDebito = id_contaContabilFiscalDebito;
    }

    /**
     * @return the id_historicoPadrao
     */
    public Integer getId_historicoPadrao() {
        return id_historicoPadrao;
    }

    /**
     * @param id_historicoPadrao the id_historicoPadrao to set
     */
    public void setId_historicoPadrao(Integer id_historicoPadrao) {
        this.id_historicoPadrao = id_historicoPadrao;
    }

    /**
     * @return the id_contaContabilFiscalCredito
     */
    public Integer getId_contaContabilFiscalCredito() {
        return id_contaContabilFiscalCredito;
    }

    /**
     * @param id_contaContabilFiscalCredito the id_contaContabilFiscalCredito to set
     */
    public void setId_contaContabilFiscalCredito(Integer id_contaContabilFiscalCredito) {
        this.id_contaContabilFiscalCredito = id_contaContabilFiscalCredito;
    }

    /**
     * @return the substituicao
     */
    public boolean isSubstituicao() {
        return substituicao;
    }

    /**
     * @param substituicao the substituicao to set
     */
    public void setSubstituicao(boolean substituicao) {
        this.substituicao = substituicao;
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
     * @return the verificaPedido
     */
    public boolean isVerificaPedido() {
        return verificaPedido;
    }

    /**
     * @param verificaPedido the verificaPedido to set
     */
    public void setVerificaPedido(boolean verificaPedido) {
        this.verificaPedido = verificaPedido;
    }

    /**
     * @return the id_produto
     */
    public Integer getId_produto() {
        return id_produto;
    }

    /**
     * @param id_produto the id_produto to set
     */
    public void setId_produto(Integer id_produto) {
        this.id_produto = id_produto;
    }

    /**
     * @return the planoConta1
     */
    public Integer getPlanoConta1() {
        return planoConta1;
    }

    /**
     * @param planoConta1 the planoConta1 to set
     */
    public void setPlanoConta1(Integer planoConta1) {
        this.planoConta1 = planoConta1;
    }

    /**
     * @return the planoConta2
     */
    public Integer getPlanoConta2() {
        return planoConta2;
    }

    /**
     * @param planoConta2 the planoConta2 to set
     */
    public void setPlanoConta2(Integer planoConta2) {
        this.planoConta2 = planoConta2;
    }

    /**
     * @return the geraVerba
     */
    public boolean isGeraVerba() {
        return geraVerba;
    }

    /**
     * @param geraVerba the geraVerba to set
     */
    public void setGeraVerba(boolean geraVerba) {
        this.geraVerba = geraVerba;
    }

    /**
     * @return the contabilidadePadrao
     */
    public boolean isContabilidadePadrao() {
        return contabilidadePadrao;
    }

    /**
     * @param contabilidadePadrao the contabilidadePadrao to set
     */
    public void setContabilidadePadrao(boolean contabilidadePadrao) {
        this.contabilidadePadrao = contabilidadePadrao;
    }

    /**
     * @return the contabiliza
     */
    public boolean isContabiliza() {
        return contabiliza;
    }

    /**
     * @param contabiliza the contabiliza to set
     */
    public void setContabiliza(boolean contabiliza) {
        this.contabiliza = contabiliza;
    }

    /**
     * @return the creditaPisCofins
     */
    public boolean isCreditaPisCofins() {
        return creditaPisCofins;
    }

    /**
     * @param creditaPisCofins the creditaPisCofins to set
     */
    public void setCreditaPisCofins(boolean creditaPisCofins) {
        this.creditaPisCofins = creditaPisCofins;
    }

    /**
     * @return the id_tipoBaseCalculoCredito
     */
    public Integer getId_tipoBaseCalculoCredito() {
        return id_tipoBaseCalculoCredito;
    }

    /**
     * @param id_tipoBaseCalculoCredito the id_tipoBaseCalculoCredito to set
     */
    public void setId_tipoBaseCalculoCredito(Integer id_tipoBaseCalculoCredito) {
        this.id_tipoBaseCalculoCredito = id_tipoBaseCalculoCredito;
    }

    /**
     * @return the naoCreditaIcms
     */
    public boolean isNaoCreditaIcms() {
        return naoCreditaIcms;
    }

    /**
     * @param naoCreditaIcms the naoCreditaIcms to set
     */
    public void setNaoCreditaIcms(boolean naoCreditaIcms) {
        this.naoCreditaIcms = naoCreditaIcms;
    }

    /**
     * @return the descargaPalete
     */
    public boolean isDescargaPalete() {
        return descargaPalete;
    }

    /**
     * @param descargaPalete the descargaPalete to set
     */
    public void setDescargaPalete(boolean descargaPalete) {
        this.descargaPalete = descargaPalete;
    }

    /**
     * @return the ativoImobilizado
     */
    public boolean isAtivoImobilizado() {
        return ativoImobilizado;
    }

    /**
     * @param ativoImobilizado the ativoImobilizado to set
     */
    public void setAtivoImobilizado(boolean ativoImobilizado) {
        this.ativoImobilizado = ativoImobilizado;
    }

    /**
     * @return the id_ativoGrupo
     */
    public Integer getId_ativoGrupo() {
        return id_ativoGrupo;
    }

    /**
     * @param id_ativoGrupo the id_ativoGrupo to set
     */
    public void setId_ativoGrupo(Integer id_ativoGrupo) {
        this.id_ativoGrupo = id_ativoGrupo;
    }

    /**
     * @return the utilizaCentroCusto
     */
    public boolean isUtilizaCentroCusto() {
        return utilizaCentroCusto;
    }

    /**
     * @param utilizaCentroCusto the utilizaCentroCusto to set
     */
    public void setUtilizaCentroCusto(boolean utilizaCentroCusto) {
        this.utilizaCentroCusto = utilizaCentroCusto;
    }

    /**
     * @return the id_aliquota
     */
    public Integer getId_aliquota() {
        return id_aliquota;
    }

    /**
     * @param id_aliquota the id_aliquota to set
     */
    public void setId_aliquota(Integer id_aliquota) {
        this.id_aliquota = id_aliquota;
    }

    /**
     * @return the notaMei
     */
    public boolean isNotaMei() {
        return notaMei;
    }

    /**
     * @param notaMei the notaMei to set
     */
    public void setNotaMei(boolean notaMei) {
        this.notaMei = notaMei;
    }

    /**
     * @return the contabilizaController360
     */
    public boolean isContabilizaController360() {
        return contabilizaController360;
    }

    /**
     * @param contabilizaController360 the contabilizaController360 to set
     */
    public void setContabilizaController360(boolean contabilizaController360) {
        this.contabilizaController360 = contabilizaController360;
    }

    /**
     * @return the utilizaCustoOrigem
     */
    public boolean isUtilizaCustoOrigem() {
        return utilizaCustoOrigem;
    }

    /**
     * @param utilizaCustoOrigem the utilizaCustoOrigem to set
     */
    public void setUtilizaCustoOrigem(boolean utilizaCustoOrigem) {
        this.utilizaCustoOrigem = utilizaCustoOrigem;
    }

   
}
