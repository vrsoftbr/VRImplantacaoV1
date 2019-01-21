package vrimplantacao2.vo.importacao;

import vrimplantacao2.utils.MathUtils;

/**
 * Item da nota fiscal a ser importada.
 * @author Leandro
 */
public class NotaFiscalItemIMP {
    
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
    private double valorBaseIpi = 0;
    private double valorIpi = 0;
    //PIS
    private int pisCofinsCst;
    private double pisCofinsValor = 0;
    private int tipoNaturezaReceita = -1;
    //PAUTA FISCAL
    private String idAliquotaPautaFiscal;
    private double ivaPorcentagem;
    private double ivaPauta;

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
    
}
