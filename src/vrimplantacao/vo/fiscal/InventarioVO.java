package vrimplantacao.vo.fiscal;

public class InventarioVO {

    public long id = 0;
    public int idProduto = 0;
    public String produto = "";
    public String data = "";
    public String dataGeracao = "";
    public double precoVenda = 0;
    public double quantidade = 0;
    public double custoComImposto = 0;
    public double custoSemImposto = 0;
    private double custoMedioComImposto = 0;
    private double custoMedioSemImposto = 0;
    public int idAliquotaCredito = 0;
    public String aliquotaCredito = "";
    public int idAliquotaDebito = 0;
    public String aliquotaDebito = "";
    public int idLoja = 0;
    public String loja = "";
    public int idTipoEmbalagem = 0;
    public double pis = 0;
    public double cofins = 0;
    private double valorIcmsSubstituicao = 0;
    private double valorIpi = 0;
    public double valorIcms = 0;
    private boolean eCodigoBarras = false;
    private long codigoAnterior = 0;

    public long getId() {
        return id;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public String getProduto() {
        return produto;
    }

    public String getData() {
        return data;
    }

    public String getDataGeracao() {
        return dataGeracao;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public double getCustoComImposto() {
        return custoComImposto;
    }

    public double getCustoSemImposto() {
        return custoSemImposto;
    }

    public int getIdAliquotaCredito() {
        return idAliquotaCredito;
    }

    public String getAliquotaCredito() {
        return aliquotaCredito;
    }

    public int getIdAliquotaDebito() {
        return idAliquotaDebito;
    }

    public String getAliquotaDebito() {
        return aliquotaDebito;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public String getLoja() {
        return loja;
    }

    public int getIdTipoEmbalagem() {
        return idTipoEmbalagem;
    }

    public double getPis() {
        return pis;
    }

    public double getCofins() {
        return cofins;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDataGeracao(String dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setCustoComImposto(double custoComImposto) {
        this.custoComImposto = custoComImposto;
    }

    public void setCustoSemImposto(double custoSemImposto) {
        this.custoSemImposto = custoSemImposto;
    }

    public void setIdAliquotaCredito(int idAliquotaCredito) {
        this.idAliquotaCredito = idAliquotaCredito;
    }

    public void setAliquotaCredito(String aliquotaCredito) {
        this.aliquotaCredito = aliquotaCredito;
    }

    public void setIdAliquotaDebito(int idAliquotaDebito) {
        this.idAliquotaDebito = idAliquotaDebito;
    }

    public void setAliquotaDebito(String aliquotaDebito) {
        this.aliquotaDebito = aliquotaDebito;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setIdTipoEmbalagem(int idTipoEmbalagem) {
        this.idTipoEmbalagem = idTipoEmbalagem;
    }

    public void setPis(double pis) {
        this.pis = pis;
    }

    public void setCofins(double cofins) {
        this.cofins = cofins;
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = valorIcms;
    }

    public double getCustoMedioComImposto() {
        return custoMedioComImposto;
    }

    public void setCustoMedioComImposto(double custoMedioComImposto) {
        this.custoMedioComImposto = custoMedioComImposto;
    }

    public double getCustoMedioSemImposto() {
        return custoMedioSemImposto;
    }

    public void setCustoMedioSemImposto(double custoMedioSemImposto) {
        this.custoMedioSemImposto = custoMedioSemImposto;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public long getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(long codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }
}