package vrimplantacao2.vo.importacao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.NormaCompra;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoAtacado;
import vrimplantacao2.vo.enums.TipoProduto;

@DatabaseTable(tableName = "miglog.produto")
public class ProdutoIMP {

    /**
     * @return the teclaAssociada
     */
    public double getTeclaAssociada() {
        return teclaAssociada;
    }

    /**
     * @param teclaAssociada the teclaAssociada to set
     */
    public void setTeclaAssociada(int teclaAssociada) {
        this.teclaAssociada = teclaAssociada;
    }

    /**
     * @return the numeroparcela
     */
    public int getNumeroparcela() {
        return numeroparcela;
    }

    /**
     * @param numeroparcela the numeroparcela to set
     */
    public void setNumeroparcela(int numeroparcela) {
        this.numeroparcela = numeroparcela;
    }

    public ProdutoIMP() {
    }
    
    /**
     * ID do registro no banco temporário
     */
    @DatabaseField(generatedId = true, columnDefinition = "bigint default nextval('miglog.produto_id_seq')")
    public Long recordId;
    @DatabaseField private String importSistema;
    @DatabaseField private String importLoja;
    @DatabaseField private String importId;
    
    @DatabaseField private Date dataCadastro = new Date();
    @DatabaseField private Date dataAlteracao = new Date();
    @DatabaseField private String ean = "-2";
    @DatabaseField private int qtdEmbalagemCotacao = 1;
    @DatabaseField private int qtdEmbalagem = 1;
    @DatabaseField private String tipoEmbalagem = "UN";
    @DatabaseField private String tipoEmbalagemCotacao = null;
    @DatabaseField private boolean eBalanca = false;
    @DatabaseField private int validade = 0;
    
    @DatabaseField private String descricaoCompleta = "SEM DESCRICAO";
    @DatabaseField private String descricaoReduzida = "SEM DESCRICAO";
    @DatabaseField private String descricaoGondola = "SEM DESCRICAO";
    
    @DatabaseField private String codMercadologico1 = "";
    @DatabaseField private String codMercadologico2 = "";
    @DatabaseField private String codMercadologico3 = "";
    @DatabaseField private String codMercadologico4 = "";
    @DatabaseField private String codMercadologico5 = "";
    @DatabaseField private String idFamiliaProduto = "";
    
    @DatabaseField private double pesoBruto = 0;
    @DatabaseField private double pesoLiquido = 0;
    @DatabaseField private double estoqueMaximo = 0;
    @DatabaseField private double estoqueMinimo = 0;
    @DatabaseField private double estoque = 0;
    @DatabaseField private double troca = 0;
    @DatabaseField private int numeroparcela = 0;
    
    @DatabaseField private double margem = 0;
    @DatabaseField private double margemMinima = 0;
    @DatabaseField private double margemMaxima = 0;
    @DatabaseField private double custoSemImposto = 0;
    @DatabaseField private double custoComImposto = 0;    
    @DatabaseField private double custoAnteriorSemImposto = 0;
    @DatabaseField private double custoAnteriorComImposto = 0;
    @DatabaseField private double custoMedio = 0;
    @DatabaseField private double precovenda = 0;
    @DatabaseField private int teclaAssociada = 0;
    
    @DatabaseField private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    @DatabaseField private boolean descontinuado = false;
    @DatabaseField private String ncm;
    @DatabaseField private String cest;
    
    @DatabaseField private int piscofinsCstDebito = 0;
    @DatabaseField private int piscofinsCstCredito = 0;
    @DatabaseField private int piscofinsNaturezaReceita = -1;
    
    @DatabaseField private int icmsCstEntrada = 40;
    @DatabaseField private double icmsAliqEntrada = 0;
    @DatabaseField private double icmsReducaoEntrada = 0;
    
    @DatabaseField private int icmsCstEntradaForaEstado = 40;
    @DatabaseField private double icmsAliqEntradaForaEstado = 0;
    @DatabaseField private double icmsReducaoEntradaForaEstado = 0;
    
    @DatabaseField private int icmsCstSaida = 40;
    @DatabaseField private double icmsAliqSaida = 0;
    @DatabaseField private double icmsReducaoSaida = 0;

    @DatabaseField private int icmsCstSaidaForaEstado = 40;
    @DatabaseField private double icmsAliqSaidaForaEstado = 0;
    @DatabaseField private double icmsReducaoSaidaForaEstado = 0;

    @DatabaseField private int icmsCstSaidaForaEstadoNF = 40;
    @DatabaseField private double icmsAliqSaidaForaEstadoNF = 0;
    @DatabaseField private double icmsReducaoSaidaForaEstadoNF = 0;
    
    @DatabaseField private int icmsCstConsumidor = -1;
    @DatabaseField private double icmsAliqConsumidor;
    @DatabaseField private double icmsReducaoConsumidor;
    
    @DatabaseField private String icmsDebitoId;
    @DatabaseField private String icmsDebitoForaEstadoId;
    @DatabaseField private String icmsDebitoForaEstadoNfId;
    @DatabaseField private String icmsCreditoId;
    @DatabaseField private String icmsCreditoForaEstadoId;
    @DatabaseField private String icmsConsumidorId;
    
    @DatabaseField private double atacadoPreco = 0;
    @DatabaseField private double atacadoPorcentagem = 0;
    @DatabaseField private String codigoSped = "";
    
    @DatabaseField private boolean sugestaoCotacao;
    @DatabaseField private boolean sugestaoPedido;
    
    @DatabaseField private String fornecedorFabricante;
    
    @DatabaseField private String pautaFiscalId;
    @DatabaseField private boolean vendaPdv = true;
    @DatabaseField private String idComprador;
    
    @DatabaseField private String uf;
    @DatabaseField private String codigoGIA;
    @DatabaseField private TipoProduto tipoProduto =  TipoProduto.MERCADORIA_REVENDA;
    @DatabaseField private TipoAtacado tipoAtacado = TipoAtacado.EMBALAGEM;
    @DatabaseField private boolean fabricacaoPropria = false;
    @DatabaseField private boolean manterEAN = false;
    @DatabaseField private boolean importarEANUnitarioMenor7 = false;
    @DatabaseField private boolean emiteEtiqueta = true;
    @DatabaseField private boolean aceitaMultiplicacaoPDV = true;
    
    @DatabaseField private String divisao;
    
    @DatabaseField private String tipoEmbalagemVolume;
    @DatabaseField private double volume = 1;
    @DatabaseField private NormaCompra normaReposicao = NormaCompra.CAIXA;
    @DatabaseField private boolean vendaControlada = false;
    @DatabaseField private String setor = "";
    @DatabaseField private String prateleira = "";
    @DatabaseField private String beneficio = "";
    @DatabaseField private boolean produtoECommerce = false;
    @DatabaseField private String codigoAnp = "";
    
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
    
    public double getCustoAnteriorSemImposto() {
        return custoAnteriorSemImposto;
    }
    
    public double getCustoAnteriorComImposto() {
        return custoAnteriorComImposto;
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

    public String getIcmsConsumidorId() {
        return icmsConsumidorId;
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
    
    public void setCustoAnteriorSemImposto(double custoAnteriorSemImposto) {
        this.custoAnteriorSemImposto = custoAnteriorSemImposto;
    }
    
    public void setCustoAnteriorComImposto(double custoAnteriorComImposto) {
        this.custoAnteriorComImposto = custoAnteriorComImposto;
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

    public int getIcmsCstConsumidor() {
        return icmsCstConsumidor;
    }

    public void setIcmsCstConsumidor(int icmsCstConsumidor) {
        this.icmsCstConsumidor = icmsCstConsumidor;
    }

    public double getIcmsAliqConsumidor() {
        return icmsAliqConsumidor;
    }

    public void setIcmsAliqConsumidor(double icmsAliqConsumidor) {
        this.icmsAliqConsumidor = icmsAliqConsumidor;
    }

    public double getIcmsReducaoConsumidor() {
        return icmsReducaoConsumidor;
    }

    public void setIcmsReducaoConsumidor(double icmsReducaoConsumidor) {
        this.icmsReducaoConsumidor = icmsReducaoConsumidor;
    }

    public double getTroca() {
        return troca;
    }

    public void setTroca(double troca) {
        this.troca = troca;
    }

    public String getIcmsDebitoForaEstadoId() {
        return icmsDebitoForaEstadoId;
    }

    public void setIcmsDebitoForaEstadoId(String icmsDebitoForaEstadoId) {
        this.icmsDebitoForaEstadoId = icmsDebitoForaEstadoId;
    }

    public String getIcmsDebitoForaEstadoNfId() {
        return icmsDebitoForaEstadoNfId;
    }

    public void setIcmsDebitoForaEstadoNfId(String icmsDebitoForaEstadoNfId) {
        this.icmsDebitoForaEstadoNfId = icmsDebitoForaEstadoNfId;
    }

    public void setIcmsConsumidorId(String icmsConsumidorId) {
        this.icmsConsumidorId = icmsConsumidorId;
    }

    public void setManterEAN(boolean manterEAN) {
        this.manterEAN = manterEAN;
    }
    
    public boolean isManterEAN() {
        return this.manterEAN;
    }

    public String getTipoEmbalagemCotacao() {
        return tipoEmbalagemCotacao;
    }

    public void setTipoEmbalagemCotacao(String tipoEmbalagemCotacao) {
        this.tipoEmbalagemCotacao = tipoEmbalagemCotacao;
    }

    public boolean isEmiteEtiqueta() {
        return emiteEtiqueta;
    }

    public void setEmiteEtiqueta(boolean emiteEtiqueta) {
        this.emiteEtiqueta = emiteEtiqueta;
    }

    public boolean isAceitaMultiplicacaoPDV() {
        return aceitaMultiplicacaoPDV;
    }

    public void setAceitaMultiplicacaoPDV(boolean aceitaMultiplicacaoPDV) {
        this.aceitaMultiplicacaoPDV = aceitaMultiplicacaoPDV;
    }

    public String getDivisao() {
        return divisao;
    }

    public void setDivisao(String divisao) {
        this.divisao = divisao;
    }

    public String getTipoEmbalagemVolume() {
        return tipoEmbalagemVolume;
    }

    public void setTipoEmbalagemVolume(String tipoEmbalagemVolume) {
        this.tipoEmbalagemVolume = tipoEmbalagemVolume;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
     * @return the margemMinima
     */
    public double getMargemMinima() {
        return margemMinima;
    }

    /**
     * @param margemMinima the margemMinima to set
     */
    public void setMargemMinima(double margemMinima) {
        this.margemMinima = margemMinima;
    }

    public NormaCompra getNormaReposicao() {
        return normaReposicao;
    }

    public void setNormaReposicao(NormaCompra normaReposicao) {
        this.normaReposicao = normaReposicao;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(String prateleira) {
        this.prateleira = prateleira;
    }

    /**
     * Venda de produtos alcoólicos ou controlados.
     * @return 
     */
    public boolean isVendaControlada() {
        return vendaControlada;
    }

    /**
     * Venda de produtos alcoólicos ou controlados.
     * @param vendaControlada 
     */
    public void setVendaControlada(boolean vendaControlada) {
        this.vendaControlada = vendaControlada;
    }
    
    /**
     * Venda de produtos alcoólicos ou controlados.
     * @param produtoControlado 
     */
    public void setProdutoControlado(String produtoControlado) {
        this.vendaControlada = Utils.stringToBool(produtoControlado != null ? produtoControlado.toUpperCase() : "F");
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }

    public boolean isProdutoECommerce() {
        return produtoECommerce;
    }

    public void setProdutoECommerce(boolean produtoECommerce) {
        this.produtoECommerce = produtoECommerce;
    }

    public String getCodigoAnp() {
        return codigoAnp;
    }

    public void setCodigoAnp(String codigoAnp) {
        this.codigoAnp = codigoAnp;
    }

    public boolean isImportarEANUnitarioMenor7() {
        return importarEANUnitarioMenor7;
    }

    public void setImportarEANUnitarioMenor7(boolean importarEANUnitarioMenor7) {
        this.importarEANUnitarioMenor7 = importarEANUnitarioMenor7;
    }

    public double getMargemMaxima() {
        return margemMaxima;
    }

    public void setMargemMaxima(double margemMaxima) {
        this.margemMaxima = margemMaxima;
    }

    public TipoAtacado getTipoAtacado() {
        return tipoAtacado;
    }

    public void setTipoAtacado(TipoAtacado tipoAtacado) {
        this.tipoAtacado = tipoAtacado;
    }

    public double getCustoMedio() {
        return custoMedio;
    }

    public void setCustoMedio(double custoMedio) {
        this.custoMedio = custoMedio;
    }
}
