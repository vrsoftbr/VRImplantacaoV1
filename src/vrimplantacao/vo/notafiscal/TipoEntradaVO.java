package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;

public class TipoEntradaVO {

    public int id = -1;
    public String descricao = "";
    public String tipo = "";
    public boolean atualizaCusto = false;
    public boolean atualizaEstoque = false;
    public boolean atualizaTroca = false;
    public boolean atualizaPedido = false;
    public boolean imprimeGuiaCega = false;
    public boolean imprimeDivergencia = false;
    public boolean atualizaPerda = false;
    public boolean notaProdutor = false;
    public boolean geraContrato = false;
    public boolean atualizaDataEntrada = false;
    public boolean utilizaCustoTabela = false;
    public boolean bonificacao = false;
    public boolean atualizaDivergenciaCusto = false;
    public boolean atualizaAdministracao = false;
    public boolean atualizaFiscal = false;
    public boolean atualizaPagar = false;
    public boolean atualizaEscrita = false;
    public boolean foraEstado = false;
    public boolean substituicao = false;
    public int idSituacaoCadastro = 0;
    public int idContaContabilFiscalCredito = 0;
    public int idContaContabilFiscalDebito = 0;
    public int idHistoricoPadrao = 0;
    public int idProduto = 0;
    public String especie = "";
    public List<CfopVO> vCfop = new ArrayList();
    public boolean verificaPedido = false;
}
