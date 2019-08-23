package vrimplantacao2.vo.importacao;

import vrimplantacao2.utils.MathUtils;

/**
 * Item da nota fiscal a ser importada.
 * @author Leandro
 */
public class NotaFiscalItemIMP {
    
    private String id;
    private NotaFiscalIMP notaFiscal;
    private int numeroItem;
    private String idProduto;
    private String ncm;
    private String cest;
    private String cfop;
    private String descricao;
    private String unidade;
    private String ean;

    private double quantidadeEmbalagem = 1;
    private double quantidade = 0;
    private double valorTotalProduto = 0;
    private double valorDesconto = 0;
    private double valorFrete = 0;
    private double valorIsento = 0;
    private double valorOutras = 0;
    //ICMS
    private String idIcms;
    private int icmsCst = 0;
    private double icmsAliquota = 0;
    private double icmsReduzido = 0;
    private double icmsBaseCalculo = 0;
    private double icmsValor = 0;
    private double icmsBaseCalculoST = 0;
    private double icmsValorST = 0;
    //IPI
    private double ipiValorBase = 0;
    private double ipiValor = 0;
    //PIS
    private int pisCofinsCst;
    private double pisCofinsValor = 0;
    private int tipoNaturezaReceita = -1;
    //PAUTA FISCAL
    private String idAliquotaPautaFiscal;
    private double ivaPorcentagem;
    private double ivaPauta;

    public String getId() {
        return id;
    }

    public void setId(String... ids) {
        StringBuilder builder = new StringBuilder();
        for (String key: ids) {
            builder.append(key);
        }
        this.id = builder.toString();
    }
    
    public NotaFiscalItemIMP(NotaFiscalIMP notaFiscal) {
        this.notaFiscal = notaFiscal;
    }
    
    public double getValorUnidade() {
        return MathUtils.round(this.valorTotalProduto / (this.quantidade * this.quantidadeEmbalagem), 2);
    }
    
    public double getValorTotal() {
        return this.valorTotalProduto - this.valorDesconto + this.valorFrete + this.valorOutras;
    };

    public NotaFiscalIMP getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscalIMP notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public int getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(int numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getCest() {
        return cest;
    }

    public void setCest(String cest) {
        this.cest = cest;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public double getQuantidadeEmbalagem() {
        return quantidadeEmbalagem;
    }

    public void setQuantidadeEmbalagem(double quantidadeEmbalagem) {
        this.quantidadeEmbalagem = quantidadeEmbalagem;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorTotalProduto() {
        return valorTotalProduto;
    }

    public void setValorTotalProduto(double valorTotalProduto) {
        this.valorTotalProduto = valorTotalProduto;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public double getValorIsento() {
        return valorIsento;
    }

    public void setValorIsento(double valorIsento) {
        this.valorIsento = valorIsento;
    }

    public double getValorOutras() {
        return valorOutras;
    }

    public void setValorOutras(double valorOutras) {
        this.valorOutras = valorOutras;
    }

    public String getIdIcms() {
        return idIcms;
    }

    public void setIdIcms(String idIcms) {
        this.idIcms = idIcms;
    }

    public int getIcmsCst() {
        return icmsCst;
    }

    public void setIcmsCst(int icmsCst) {
        this.icmsCst = icmsCst;
    }

    public double getIcmsAliquota() {
        return icmsAliquota;
    }

    public void setIcmsAliquota(double icmsAliquota) {
        this.icmsAliquota = icmsAliquota;
    }

    public double getIcmsReduzido() {
        return icmsReduzido;
    }

    public void setIcmsReduzido(double icmsReduzido) {
        this.icmsReduzido = icmsReduzido;
    }

    public double getIcmsBaseCalculo() {
        return icmsBaseCalculo;
    }

    public void setIcmsBaseCalculo(double icmsBaseCalculo) {
        this.icmsBaseCalculo = icmsBaseCalculo;
    }

    public double getIcmsValor() {
        return icmsValor;
    }

    public void setIcmsValor(double icmsValor) {
        this.icmsValor = icmsValor;
    }

    public double getIcmsBaseCalculoST() {
        return icmsBaseCalculoST;
    }

    public void setIcmsBaseCalculoST(double icmsBaseCalculoST) {
        this.icmsBaseCalculoST = icmsBaseCalculoST;
    }

    public double getIcmsValorST() {
        return icmsValorST;
    }

    public void setIcmsValorST(double icmsValorST) {
        this.icmsValorST = icmsValorST;
    }

    public double getIpiValorBase() {
        return ipiValorBase;
    }

    public void setIpiValorBase(double ipiValorBase) {
        this.ipiValorBase = ipiValorBase;
    }

    public double getIpiValor() {
        return ipiValor;
    }

    public void setIpiValor(double ipiValor) {
        this.ipiValor = ipiValor;
    }

    public int getPisCofinsCst() {
        return pisCofinsCst;
    }

    public void setPisCofinsCst(int pisCofinsCst) {
        this.pisCofinsCst = pisCofinsCst;
    }

    public double getPisCofinsValor() {
        return pisCofinsValor;
    }

    public void setPisCofinsValor(double pisCofinsValor) {
        this.pisCofinsValor = pisCofinsValor;
    }

    public int getTipoNaturezaReceita() {
        return tipoNaturezaReceita;
    }

    public void setTipoNaturezaReceita(int tipoNaturezaReceita) {
        this.tipoNaturezaReceita = tipoNaturezaReceita;
    }

    public String getIdAliquotaPautaFiscal() {
        return idAliquotaPautaFiscal;
    }

    public void setIdAliquotaPautaFiscal(String idAliquotaPautaFiscal) {
        this.idAliquotaPautaFiscal = idAliquotaPautaFiscal;
    }

    public double getIvaPorcentagem() {
        return ivaPorcentagem;
    }

    public void setIvaPorcentagem(double ivaPorcentagem) {
        this.ivaPorcentagem = ivaPorcentagem;
    }

    public double getIvaPauta() {
        return ivaPauta;
    }

    public void setIvaPauta(double ivaPauta) {
        this.ivaPauta = ivaPauta;
    }
    
}
