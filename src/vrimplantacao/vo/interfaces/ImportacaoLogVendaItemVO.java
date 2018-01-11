package vrimplantacao.vo.interfaces;

public class ImportacaoLogVendaItemVO {

    public int idVenda = 0;
    public int sequencia = 0;
    public int numeroCupom = 0;
    public int ecf = 0;
    public int idProduto = 0;
    public double quantidade = 0;
    public double precoVenda = 0;
    public double valorTotal = 0;
    public int idAliquota = 0;
    public String aliquota = "";
    public boolean cancelado = false;
    public double valorCancelado = 0;
    public int contadorDoc = 0;
    public double valorDesconto = 0;
    public double valorAcrescimo = 0;
    public double valorDescontoCupom = 0;
    public double valorAcrescimoCupom = 0;
    public String regraCalculo = "";
    public long codigoBarras = 0;
    public String unidadeMedida = "";
    public String totalizadorParcial = "";
    public String numeroSerie = "";
    public int casasDecimaisQuantidade = 0;
    public int casasDecimaisValor = 0;
    public long codigoAnterior = 0;
    public String descricaoProduto = "";
    public int numeroEcf = 0;
    
    public int idTipoCancelamento = 0;    
    public int matriculaCancelamento = 0;    
}