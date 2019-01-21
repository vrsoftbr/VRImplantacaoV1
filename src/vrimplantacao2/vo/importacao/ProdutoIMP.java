package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoProduto;

public class ProdutoIMP {
    
    private String importSistema;
    private String importLoja;
    private String importId;
    
    private Date dataCadastro = new Date();
    private Date dataAlteracao = new Date();
    private String ean = "-2";
    private int qtdEmbalagemCotacao = 1;
    private int qtdEmbalagem = 1;
    private String tipoEmbalagem = "UN";
    private boolean eBalanca = false;
    private int validade = 0;
    
    private String descricaoCompleta = "SEM DESCRICAO";
    private String descricaoReduzida = "SEM DESCRICAO";
    private String descricaoGondola = "SEM DESCRICAO";
    
    private String codMercadologico1 = "";
    private String codMercadologico2 = "";
    private String codMercadologico3 = "";
    private String codMercadologico4 = "";
    private String codMercadologico5 = "";
    private String idFamiliaProduto = "";
    
    private double pesoBruto = 0;
    private double pesoLiquido = 0;
    private double estoqueMaximo = 0;
    private double estoqueMinimo = 0;
    private double estoque = 0;
    
    private double margem = 0;
    private double custoSemImposto = 0;
    private double custoComImposto = 0;    
    private double precovenda = 0;    
    
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private boolean descontinuado = false;
    private String ncm;
    private String cest;
    
    private int piscofinsCstDebito = 0;
    private int piscofinsCstCredito = 0;
    private int piscofinsNaturezaReceita = -1;
    
    private int icmsCstEntrada = 60;
    private double icmsAliqEntrada = 0;
    private double icmsReducaoEntrada = 0;
    
    private int icmsCstEntradaForaEstado = 60;
    private double icmsAliqEntradaForaEstado = 0;
    private double icmsReducaoEntradaForaEstado = 0;
    
    private int icmsCstSaida = 60;
    private double icmsAliqSaida = 0;
    private double icmsReducaoSaida = 0;

    private int icmsCstSaidaForaEstado = 60;
    private double icmsAliqSaidaForaEstado = 0;
    private double icmsReducaoSaidaForaEstado = 0;

    private int icmsCstSaidaForaEstadoNF = 60;
    private double icmsAliqSaidaForaEstadoNF = 0;
    private double icmsReducaoSaidaForaEstadoNF = 0;
    
    private String icmsDebitoId;
    private String icmsCreditoId;
    private String icmsCreditoForaEstadoId;
    
    private double atacadoPreco = 0;
    private double atacadoPorcentagem = 0;
    private String codigoSped = "";
    
    private boolean sugestaoCotacao;
    private boolean sugestaoPedido;
    
    private String fornecedorFabricante;
    
    private String pautaFiscalId;
    private boolean vendaPdv = true;
    private String idComprador;
    
    private String uf;
    private String codigoGIA;
    private TipoProduto tipoProduto =  TipoProduto.MERCADORIA_REVENDA;
    private boolean fabricacaoPropria = false;
    
    public String getImportSistema() {
        return importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public String getImportId() {
        return importId;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }
    
    public String getEan() {
        return ean;
    }

    public int getQtdEmbalagemCotacao() {
        return qtdEmbalagemCotacao;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public boolean isBalanca() {
        return eBalanca;
    }

    public int getValidade() {
        return validade;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public String getDescricaoGondola() {
        return descricaoGondola;
    }

    public String getCodMercadologico1() {
        return codMercadologico1;
    }

    public String getCodMercadologico2() {
        return codMercadologico2;
    }

    public String getCodMercadologico3() {
        return codMercadologico3;
    }

    public String getCodMercadologico4() {
        return codMercadologico4;
    }

    public String getCodMercadologico5() {
        return codMercadologico5;
    }

    public String getIdFamiliaProduto() {
        return idFamiliaProduto;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public double getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public double getEstoque() {
        return estoque;
    }

    public double getMargem() {
        return margem;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public double getPrecovenda() {
        return precovenda;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public String getNcm() {
        return ncm;
    }

    public String getCest() {
        return cest;
    }

    public int getPiscofinsCstDebito() {
        return piscofinsCstDebito;
    }

    public int getPiscofinsCstCredito() {
        return piscofinsCstCredito;
    }

    public int getPiscofinsNaturezaReceita() {
        return piscofinsNaturezaReceita;
    }

    public int getIcmsCst() {
        return icmsCstSaida;
    }

    public double getIcmsAliq() {
        return icmsAliqSaida;
    }

    public double getIcmsReducao() {
        return icmsReducaoSaida;
    }
    
    public int getIcmsCstEntrada() {
        return icmsCstEntrada;
    }

    public double getIcmsAliqEntrada() {
        return icmsAliqEntrada;
    }

    public double getIcmsReducaoEntrada() {
        return icmsReducaoEntrada;
    }

    public int getIcmsCstSaida() {
        return icmsCstSaida;
    }

    public double getIcmsAliqSaida() {
        return icmsAliqSaida;
    }

    public double getIcmsReducaoSaida() {
        return icmsReducaoSaida;
    }

    public double getAtacadoPreco() {
        return atacadoPreco;
    }

    public double getAtacadoPorcentagem() {
        return atacadoPorcentagem;
    }

    public String getCodigoSped() {
        return codigoSped;
    }

    public String getIcmsDebitoId() {
        return icmsDebitoId;
    }

    public String getIcmsCreditoId() {
        return icmsCreditoId;
    }
    
    public String getIcmsCreditoForaEstadoId() {
        return icmsCreditoForaEstadoId;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public void setEan(String ean) {
        this.ean = ean == null ? "" : ean;
    }

    public void setQtdEmbalagemCotacao(int qtdEmbalagemCotacao) {
        this.qtdEmbalagemCotacao = qtdEmbalagemCotacao;
    }
    
    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public void seteBalanca(boolean eBalanca) {
        this.eBalanca = eBalanca;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public void setDescricaoGondola(String descricaoGondola) {
        this.descricaoGondola = descricaoGondola;
    }

    public void setCodMercadologico1(String codMercadologico1) {
        this.codMercadologico1 = codMercadologico1 != null ? codMercadologico1 : "";
    }

    public void setCodMercadologico2(String codMercadologico2) {
        this.codMercadologico2 = codMercadologico2 != null ? codMercadologico2 : "";
    }

    public void setCodMercadologico3(String codMercadologico3) {
        this.codMercadologico3 = codMercadologico3 != null ? codMercadologico3 : "";
    }

    public void setCodMercadologico4(String codMercadologico4) {
        this.codMercadologico4 = codMercadologico4 != null ? codMercadologico4 : "";
    }

    public void setCodMercadologico5(String codMercadologico5) {
        this.codMercadologico5 = codMercadologico5 != null ? codMercadologico5 : "";
    }

    public void setIdFamiliaProduto(String idFamiliaProduto) {
        this.idFamiliaProduto = idFamiliaProduto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public void setEstoqueMaximo(double estoqueMaximo) {
        this.estoqueMaximo = estoqueMaximo;
    }

    public void setEstoqueMinimo(double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public void setEstoque(double estoque) {
        this.estoque = estoque;
    }

    public void setMargem(double margem) {
        this.margem = margem;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = custoSemImposto;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = custoComImposto;
    }

    public void setPrecovenda(double precovenda) {
        this.precovenda = precovenda;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }
    
    public void setSituacaoCadastro(int situacaoCadastro) {
        this.situacaoCadastro = SituacaoCadastro.getById(situacaoCadastro);
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public void setPiscofinsCstDebito(int piscofinsCstDebito) {
        this.piscofinsCstDebito = piscofinsCstDebito;
    }
    public void setPiscofinsCstDebito(String piscofinsCstDebito) {
        this.piscofinsCstDebito = Utils.stringToInt(piscofinsCstDebito);
    }

    public void setPiscofinsCstCredito(int piscofinsCstCredito) {
        this.piscofinsCstCredito = piscofinsCstCredito;
    }
    public void setPiscofinsCstCredito(String piscofinsCstCredito) {
        this.piscofinsCstCredito = Utils.stringToInt(piscofinsCstCredito);
    }

    public void setPiscofinsNaturezaReceita(int piscofinsNaturezaReceita) {
        this.piscofinsNaturezaReceita = piscofinsNaturezaReceita;
    }    
    public void setPiscofinsNaturezaReceita(String piscofinsNaturezaReceita) {
        this.piscofinsNaturezaReceita = Utils.stringToInt(piscofinsNaturezaReceita);
    }

    public void setIcmsCst(String icmsCst) {
        this.setIcmsCst(Utils.stringToInt(icmsCst));
    }
    
    public void setIcmsCst(int icmsCst) {
        this.icmsCstEntrada = icmsCst;
        this.icmsCstSaida = icmsCst;
    }

    public void setIcmsAliq(double icmsAliq) {
        this.icmsAliqEntrada = icmsAliq;
        this.icmsAliqSaida = icmsAliq;
    }

    public void setIcmsReducao(double icmsReducao) {
        this.icmsReducaoEntrada = icmsReducao;
        this.icmsReducaoSaida = icmsReducao;
    }

    public void setIcmsCstEntrada(int icmsCstEntrada) {
        this.icmsCstEntrada = icmsCstEntrada;
    }

    public void setIcmsAliqEntrada(double icmsAliqEntrada) {
        this.icmsAliqEntrada = icmsAliqEntrada;
    }

    public void setIcmsReducaoEntrada(double icmsReducaoEntrada) {
        this.icmsReducaoEntrada = icmsReducaoEntrada;
    }

    public void setIcmsCstSaida(int icmsCstSaida) {
        this.icmsCstSaida = icmsCstSaida;
    }

    public void setIcmsAliqSaida(double icmsAliqSaida) {
        this.icmsAliqSaida = icmsAliqSaida;
    }

    public void setIcmsReducaoSaida(double icmsReducaoSaida) {
        this.icmsReducaoSaida = icmsReducaoSaida;
    }

    public void setAtacadoPreco(double atacadoPreco) {
        this.atacadoPreco = MathUtils.round(atacadoPreco, 4);
    }

    public void setAtacadoPorcentagem(double atacadoPorcentagem) {
        this.atacadoPorcentagem = MathUtils.round(atacadoPorcentagem, 4);
    }

    public void setCodigoSped(String codigoSped) {
        if (codigoSped == null) {
            codigoSped = "";
        }
        this.codigoSped = codigoSped;
    }

    public void setIcmsDebitoId(String icmsDebitoId) {
        this.icmsDebitoId = icmsDebitoId;
    }
    
    public void setIcmsCreditoId(String icmsCreditoId) {
        this.icmsCreditoId = icmsCreditoId;
    }
    
    public void setIcmsCreditoForaEstadoId(String icmsCreditoForaEstadoId) {
        this.icmsCreditoForaEstadoId = icmsCreditoForaEstadoId;
    }
    
    public String[] getChave() {
        return new String[]{
            getImportSistema(),
            getImportLoja(),
            getImportId(),
            getEan()
        };
    }

    public boolean isSugestaoCotacao() {
        return sugestaoCotacao;
    }

    public void setSugestaoCotacao(boolean sugestaoCotacao) {
        this.sugestaoCotacao = sugestaoCotacao;
    }

    public boolean isSugestaoPedido() {
        return sugestaoPedido;
    }

    public void setSugestaoPedido(boolean sugestaoPedido) {
        this.sugestaoPedido = sugestaoPedido;
    }

    public boolean isDescontinuado() {
        return descontinuado;
    }

    public void setDescontinuado(boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

    public String getFornecedorFabricante() {
        return fornecedorFabricante;
    }

    public void setFornecedorFabricante(String fornecedorFabricante) {
        this.fornecedorFabricante = fornecedorFabricante;
    }

    public String getPautaFiscalId() {
        return pautaFiscalId;
    }

    public void setPautaFiscalId(String pautaFiscalId) {
        this.pautaFiscalId = pautaFiscalId;
    }

    public boolean isVendaPdv() {
        return vendaPdv;
    }

    public void setVendaPdv(boolean vendaPdv) {
        this.vendaPdv = vendaPdv;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    /**
     * @return the icmsCstEntradaForaEstado
     */
    public int getIcmsCstEntradaForaEstado() {
        return icmsCstEntradaForaEstado;
    }

    /**
     * @param icmsCstEntradaForaEstado the icmsCstEntradaForaEstado to set
     */
    public void setIcmsCstEntradaForaEstado(int icmsCstEntradaForaEstado) {
        this.icmsCstEntradaForaEstado = icmsCstEntradaForaEstado;
    }

    /**
     * @return the icmsAliqEntradaForaEstado
     */
    public double getIcmsAliqEntradaForaEstado() {
        return icmsAliqEntradaForaEstado;
    }

    /**
     * @param icmsAliqEntradaForaEstado the icmsAliqEntradaForaEstado to set
     */
    public void setIcmsAliqEntradaForaEstado(double icmsAliqEntradaForaEstado) {
        this.icmsAliqEntradaForaEstado = icmsAliqEntradaForaEstado;
    }

    /**
     * @return the icmsReducaoEntradaForaEstado
     */
    public double getIcmsReducaoEntradaForaEstado() {
        return icmsReducaoEntradaForaEstado;
    }

    /**
     * @param icmsReducaoEntradaForaEstado the icmsReducaoEntradaForaEstado to set
     */
    public void setIcmsReducaoEntradaForaEstado(double icmsReducaoEntradaForaEstado) {
        this.icmsReducaoEntradaForaEstado = icmsReducaoEntradaForaEstado;
    }

    /**
     * @return the icmsCstSaidaForaEstado
     */
    public int getIcmsCstSaidaForaEstado() {
        return icmsCstSaidaForaEstado;
    }

    /**
     * @param icmsCstSaidaForaEstado the icmsCstSaidaForaEstado to set
     */
    public void setIcmsCstSaidaForaEstado(int icmsCstSaidaForaEstado) {
        this.icmsCstSaidaForaEstado = icmsCstSaidaForaEstado;
    }

    /**
     * @return the icmsAliqSaidaForaEstado
     */
    public double getIcmsAliqSaidaForaEstado() {
        return icmsAliqSaidaForaEstado;
    }

    /**
     * @param icmsAliqSaidaForaEstado the icmsAliqSaidaForaEstado to set
     */
    public void setIcmsAliqSaidaForaEstado(double icmsAliqSaidaForaEstado) {
        this.icmsAliqSaidaForaEstado = icmsAliqSaidaForaEstado;
    }

    /**
     * @return the icmsReducaoSaidaForaEstado
     */
    public double getIcmsReducaoSaidaForaEstado() {
        return icmsReducaoSaidaForaEstado;
    }

    /**
     * @param icmsReducaoSaidaForaEstado the icmsReducaoSaidaForaEstado to set
     */
    public void setIcmsReducaoSaidaForaEstado(double icmsReducaoSaidaForaEstado) {
        this.icmsReducaoSaidaForaEstado = icmsReducaoSaidaForaEstado;
    }

    /**
     * @return the icmsCstSaidaForaEstadoNF
     */
    public int getIcmsCstSaidaForaEstadoNF() {
        return icmsCstSaidaForaEstadoNF;
    }

    /**
     * @param icmsCstSaidaForaEstadoNF the icmsCstSaidaForaEstadoNF to set
     */
    public void setIcmsCstSaidaForaEstadoNF(int icmsCstSaidaForaEstadoNF) {
        this.icmsCstSaidaForaEstadoNF = icmsCstSaidaForaEstadoNF;
    }

    /**
     * @return the icmsAliqSaidaForaEstadoNF
     */
    public double getIcmsAliqSaidaForaEstadoNF() {
        return icmsAliqSaidaForaEstadoNF;
    }

    /**
     * @param icmsAliqSaidaForaEstadoNF the icmsAliqSaidaForaEstadoNF to set
     */
    public void setIcmsAliqSaidaForaEstadoNF(double icmsAliqSaidaForaEstadoNF) {
        this.icmsAliqSaidaForaEstadoNF = icmsAliqSaidaForaEstadoNF;
    }

    /**
     * @return the icmsReducaoSaidaForaEstadoNF
     */
    public double getIcmsReducaoSaidaForaEstadoNF() {
        return icmsReducaoSaidaForaEstadoNF;
    }

    /**
     * @param icmsReducaoSaidaForaEstadoNF the icmsReducaoSaidaForaEstadoNF to set
     */
    public void setIcmsReducaoSaidaForaEstadoNF(double icmsReducaoSaidaForaEstadoNF) {
        this.icmsReducaoSaidaForaEstadoNF = icmsReducaoSaidaForaEstadoNF;
    }

    public String getCodigoGIA() {
        return codigoGIA;
    }

    public void setCodigoGIA(String codigoGIA) {
        this.codigoGIA = codigoGIA;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = TipoProduto.getById(tipoProduto);
    }
    
    public void setTipoProduto(int tipoProduto) {
        this.tipoProduto = TipoProduto.getById(tipoProduto);
    }

    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }
    
}
