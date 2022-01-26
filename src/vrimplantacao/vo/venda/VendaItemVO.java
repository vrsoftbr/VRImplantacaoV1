package vrimplantacao.vo.venda;

import java.util.Date;



public class VendaItemVO {

    public long id = 0;
    public int idProduto = 0;
    public double quantidade = 0;
    public double precoVenda = 0;
    public double valorTotal = 0;
    public long idAliquota = 0;
    public boolean cancelado = false;
    public double valorCancelado = 0;
    public int idTipoCancelamento = 0;
    public int matriculaCancelamento = 0;
    public int contadorDoc = 0;
    public double valorDesconto = 0;
    public double valorAcrescimo = 0;
    public double valorDescontoCupom = 0;
    public double valorAcrescimoCupom = 0;
    public String regraCalculo = "";
    public long codigoBarras = 0;
    public String unidadeMedida = "";
    public String totalizadorParcial = "";
    public int sequencia = 0;
    public double valorAcrescimoFixo = 0;
    public double valorDescontoPromocao = 0;
    public int idOperadorBalanca = 0;
    public int codigoScanntech = 0;
    public double valorDescontoScanntech = 0;
    public double quantidadeScanntech = 0;
    public long idVenda = 0;
    public String data;
    
    public double valorBaseSubstituicaoEfetivo = 0;    
    public double valorIcmsSubstituicaoEfetivo = 0;    
}
