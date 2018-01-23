package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class ProdutoIMP {
    
    private String importSistema;
    private String importLoja;
    private String importId;
    
    private Date dataCadastro = new Date();
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
    
    private int icmsCstSaida = 60;
    private double icmsAliqSaida = 0;
    private double icmsReducaoSaida = 0;
    
    private String icmsDebitoId;
    private String icmsCreditoId;
    
    private double atacadoPreco = 0;
    private double atacadoPorcentagem = 0;
    private String codigoSped = "";
    
    private boolean sugestaoCotacao;
    private boolean sugestaoPedido;
    
    private String fornecedorFabricante;
    
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

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public void setPiscofinsCstDebito(int piscofinsCstDebito) {
        this.piscofinsCstDebito = piscofinsCstDebito;
    }

    public void setPiscofinsCstCredito(int piscofinsCstCredito) {
        this.piscofinsCstCredito = piscofinsCstCredito;
    }

    public void setPiscofinsNaturezaReceita(int piscofinsNaturezaReceita) {
        this.piscofinsNaturezaReceita = piscofinsNaturezaReceita;
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

}
