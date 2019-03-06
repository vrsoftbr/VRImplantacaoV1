package vrimplantacao2.vo.relatorio;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.NormaReposicao;
import vrimplantacao2.vo.enums.TipoEmbalagem;

public class ProdutoRelatorioVO {
    
    private int id = 0;
    private int idProduto2 = 0;
    private String impId = "";
    private String descricaoCompleta = "SEM DESCRICAO";
    private String descricaoReduzida = "SEM DESCRICAO";
    private String descricaoGondola = "SEM DESCRICAO";
    private String ean;
    private String balanca;
    private String precoVenda;
    private String custoComImposto;
    private String custoSemImposto;
    private TipoEmbalagem tipoEmbalagem = TipoEmbalagem.UN;
    private int idFamiliaProduto;
    private String familiaProduto;
    private int codMerc1;
    private int codMerc2;
    private int codMerc3;
    private int codMerc4;
    private int codMerc5;
    private String merc1;
    private String merc2;
    private String merc3;
    private String merc4;
    private String merc5;
    private String estoqueMax;
    private String estoqueMin;
    private String estoque;
    private Date datacadastro = new Date();
    private Date dataalteracao = new Date();
    private int qtdEmbalagem = 1;
    private int validade = 0;
    private String pesoBruto = "";
    private String pesoLiquido = "";
    private double margem = 0;
    private String ncm;
    private String pisCofinsDebito;
    private String pisCofinsCredito;
    private String pisCofinsNaturezaReceita;
    private boolean pesavel = false;
    private boolean vendaPdv = true;
    private String cest;
    private String icmsCst;
    private String icmsAliquotaDebito;
    private String icmsAliquotaReduzido;
    private String icmsAliquotaCredito;
    private String icmsAliquotaConsumidor;
    private NormaReposicao normaCompra = NormaReposicao.CAIXA;
    private NormaReposicao normaReposicao = NormaReposicao.CAIXA;
    private boolean sugestaoCotacao = true;
    private boolean sugestaoPedido = true;
    private int idFornecedorFabricante = 0;
    private int excecao = 0;
    private int idComprador = 1;
    private String ativo;
    private String descontinuado;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduto2() {
        return this.idProduto2;
    }

    public void setIdProduto2(int idProduto2) {
        this.idProduto2 = idProduto2;
    }

    public String getImpId() {
        return this.impId;
    }

    public void setImpId(String impId) {
        this.impId = impId;
    }
    
    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = Utils.acertarTexto(descricaoCompleta, 60, "PRODUTO SEM DESCRICAO");
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = Utils.acertarTexto(descricaoReduzida, 22, "PRODUTO SEM DESCRICAO");
    }

    public void setDescricaoGondola(String descricaoGondola) {
        this.descricaoGondola = Utils.acertarTexto(descricaoGondola, 60, "PRODUTO SEM DESCRICAO");
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }
    
    public void setEan(String ean) {
        this.ean = ean;
    }
    
    public void setTipoEmbalagem(TipoEmbalagem tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = datacadastro;
    }
    
    public void setDataAlteracao (Date dataalteracao) {
        this.dataalteracao = dataalteracao;
    }

    public void setValidade(int validade) {
        this.validade = validade > 0 ? validade : 0;
    }

    public void setPesoBruto(String pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public void setPesoLiquido(String pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public void setMargem(double margem) {
        this.margem = margem;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public void setPisCofinsDebito(String pisCofinsDebito) {
        this.pisCofinsDebito = pisCofinsDebito;
    }

    public void setPisCofinsCredito(String pisCofinsCredito) {
        this.pisCofinsCredito = pisCofinsCredito;
    }

    public void setPisCofinsNaturezaReceita(String pisCofinsNaturezaReceita) {
        this.pisCofinsNaturezaReceita = pisCofinsNaturezaReceita;
    }

    public void setPesavel(boolean pesavel) {
        this.pesavel = pesavel;
    }

    public void setVendaPdv(boolean vendaPdv) {
        this.vendaPdv = vendaPdv;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public void setNormaCompra(NormaReposicao normaCompra) {
        this.normaCompra = normaCompra;
    }

    public void setNormaReposicao(NormaReposicao normaReposicao) {
        this.normaReposicao = normaReposicao;
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

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }
    
    public String getEan() {
        return ean;
    }

    public TipoEmbalagem getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public Date getDatacadastro() {
        return datacadastro;
    }
    
    public Date getDataAlteracao() {
        return dataalteracao;
    }

    public int getValidade() {
        return validade;
    }

    public String getPesoBruto() {
        return pesoBruto;
    }

    public String getPesoLiquido() {
        return pesoLiquido;
    }

    public double getMargem() {
        return margem;
    }

    public String getNcm() {
        return ncm;
    }

    public String getPisCofinsDebito() {
        return pisCofinsDebito;
    }

    public String getPisCofinsCredito() {
        return pisCofinsCredito;
    }

    public String getPisCofinsNaturezaReceita() {
        return pisCofinsNaturezaReceita;
    }

    public boolean isPesavel() {
        return pesavel;
    }

    public boolean isVendaPdv() {
        return vendaPdv;
    }

    public String getCest() {
        return cest;
    }

    public NormaReposicao getNormaCompra() {
        return normaCompra;
    }

    public NormaReposicao getNormaReposicao() {
        return normaReposicao;
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

    public int getIdFornecedorFabricante() {
        return idFornecedorFabricante;
    }

    public void setIdFornecedorFabricante(int idFornecedorFabricante) {
        this.idFornecedorFabricante = idFornecedorFabricante;
    }

    public int getExcecao() {
        return excecao;
    }

    public void setExcecao(int excecao) {
        this.excecao = excecao;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public int getCodMerc1() {
        return codMerc1;
    }

    public void setCodMerc1(int codMerc1) {
        this.codMerc1 = codMerc1;
    }

    public int getCodMerc2() {
        return codMerc2;
    }

    public void setCodMerc2(int codMerc2) {
        this.codMerc2 = codMerc2;
    }

    public int getCodMerc3() {
        return codMerc3;
    }

    public void setCodMerc3(int codMerc3) {
        this.codMerc3 = codMerc3;
    }

    public int getCodMerc4() {
        return codMerc4;
    }

    public void setCodMerc4(int codMerc4) {
        this.codMerc4 = codMerc4;
    }

    public int getCodMerc5() {
        return codMerc5;
    }

    public void setCodMerc5(int codMerc5) {
        this.codMerc5 = codMerc5;
    }

    public String getMerc1() {
        return merc1;
    }

    public void setMerc1(String merc1) {
        this.merc1 = merc1;
    }

    public String getMerc2() {
        return merc2;
    }

    public void setMerc2(String merc2) {
        this.merc2 = merc2;
    }

    public String getMerc3() {
        return merc3;
    }

    public void setMerc3(String merc3) {
        this.merc3 = merc3;
    }

    public String getMerc4() {
        return merc4;
    }

    public void setMerc4(String merc4) {
        this.merc4 = merc4;
    }

    public String getMerc5() {
        return merc5;
    }

    public void setMerc5(String merc5) {
        this.merc5 = merc5;
    }

    public String getEstoqueMax() {
        return estoqueMax;
    }

    public void setEstoqueMax(String estoqueMax) {
        this.estoqueMax = estoqueMax;
    }

    public String getEstoqueMin() {
        return estoqueMin;
    }

    public void setEstoqueMin(String estoqueMin) {
        this.estoqueMin = estoqueMin;
    }

    public String getEstoque() {
        return estoque;
    }

    public void setEstoque(String estoque) {
        this.estoque = estoque;
    }

    public String getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(String precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getCustoComImposto() {
        return custoComImposto;
    }

    public void setCustoComImposto(String custoComImposto) {
        this.custoComImposto = custoComImposto;
    }

    public String getCustoSemImposto() {
        return custoSemImposto;
    }

    public void setCustoSemImposto(String custoSemImposto) {
        this.custoSemImposto = custoSemImposto;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getDescontinuado() {
        return descontinuado;
    }

    public void setDescontinuado(String descontinuado) {
        this.descontinuado = descontinuado;
    }

    public int getIdFamiliaProduto() {
        return idFamiliaProduto;
    }

    public void setIdFamiliaProduto(int idFamiliaProduto) {
        this.idFamiliaProduto = idFamiliaProduto;
    }

    public String getFamiliaProduto() {
        return familiaProduto;
    }

    public void setFamiliaProduto(String familiaProduto) {
        this.familiaProduto = familiaProduto;
    }

    public String getIcmsCst() {
        return icmsCst;
    }

    public void setIcmsCst(String icmsCst) {
        this.icmsCst = icmsCst;
    }

    public String getIcmsAliquotaDebito() {
        return icmsAliquotaDebito;
    }

    public void setIcmsAliquotaDebito(String icmsAliquotaDebito) {
        this.icmsAliquotaDebito = icmsAliquotaDebito;
    }

    public String getIcmsAliquotaReduzido() {
        return icmsAliquotaReduzido;
    }

    public void setIcmsAliquotaReduzido(String icmsAliquotaReduzido) {
        this.icmsAliquotaReduzido = icmsAliquotaReduzido;
    }

    public String getIcmsAliquotaCredito() {
        return icmsAliquotaCredito;
    }

    public void setIcmsAliquotaCredito(String icmsAliquotaCredito) {
        this.icmsAliquotaCredito = icmsAliquotaCredito;
    }

    public String getIcmsAliquotaConsumidor() {
        return icmsAliquotaConsumidor;
    }

    public void setIcmsAliquotaConsumidor(String icmsAliquotaConsumidor) {
        this.icmsAliquotaConsumidor = icmsAliquotaConsumidor;
    }

    public String getBalanca() {
        return balanca;
    }

    public void setBalanca(String balanca) {
        this.balanca = balanca;
    }
}
