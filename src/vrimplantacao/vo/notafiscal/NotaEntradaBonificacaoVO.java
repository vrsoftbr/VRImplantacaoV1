package vrimplantacao.vo.notafiscal;

import java.io.Serializable;

public class NotaEntradaBonificacaoVO implements Serializable {

    public long idNotaEntrada = 0;
    public long idNotaEntradaItem = 0;
    public int idProduto = 0;
    public String produto = "";
    public double valorTotal = 0;
    public double valorUtilizado = 0;
    public int idTipoEmbalagem = 0;
    public int idFornecedor = 0;
    public String fornecedor = "";
    public int numeroNota = 0;
    public String dataEntrada = "";
    public double quantidade = 0;
    public double valorUnitario = 0;
    public double custoComImpostoAnterior = 0;
    public double custoSemImposto = 0;
    public double precoVenda = 0;
    public String tipoEntrada = "";
}