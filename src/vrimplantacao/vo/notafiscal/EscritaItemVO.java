package vrimplantacao.vo.notafiscal;

import java.io.Serializable;

public class EscritaItemVO implements Serializable {

    public long id;
    public int idProduto = 0;
    public int idAliquota = 0;
    public String aliquota = "";
    public String produto = "";
    public int idTipoEmbalagem = 0;
    public double quantidade = 0;
    public double valorTotal = 0;
    public double valorIpi = 0;
    public double valorBaseCalculo = 0;
    public double valorIcms = 0;
    public double valorBaseSubstituicao = 0;
    public double valorIcmsSubstituicao = 0;
    public String cfop = "";
    public int idTipoPisCofins = 0;
    public String tipoPisCofins = "";
    public double valorIsento = 0;
    public double valorOutras = 0;
    public double valorDesconto = 0;
    public double valorAcrescimo = 0;
    public double valorCancelado = 0;
    public boolean cancelado = false;
    public int situacaoTributaria = 0;
    public double valorFrete = 0;
    public double valorOutrasDespesas = 0;
    public int tipoNaturezaReceita = -1;
}