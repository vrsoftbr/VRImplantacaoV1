package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;

public class TipoSaidaVO {

    public int id = 0;
    public String descricao = "";
    public String tipo = "";
    public boolean atualizaEscrita = false;
    public boolean geraDevolucao = false;
    public boolean geraReceber = false;
    public boolean consultaPedido = false;
    public boolean destinatarioCliente = false;
    public boolean transportadorProprio = false;
    public boolean baixaEstoque = false;
    public boolean desabilitaValor = false;
    public boolean notaProdutor = false;
    public boolean imprimeBoleto = false;
    public boolean naoCreditaIcms = false;
    public boolean calculaIva = false;
    public boolean utilizaPrecoVenda = false;
    public boolean utilizaIcmsCredito = false;
    public boolean utilizaIcmsEntrada = false;
    public boolean foraEstado = false;
    public boolean substituicao = false;
    public boolean transferencia = false;
    public boolean adicionaVenda = false;
    public boolean entraEstoque = false;
    public boolean vendaIndustria = false;
    public int idContaContabilFiscalCredito = 0;
    public int idContaContabilFiscalDebito = 0;
    public int idHistoricoPadrao = 0;
    public int idTipoEntrada = 0;
    public int idNotaSaidaMensagem = 0;
    public String serie = "";
    public String especie = "";
    public int idSituacaoCadastro = 0;
    public String situacaoCadastro = "";
    public List<CfopVO> vCfop = new ArrayList();
    public boolean geraContrato = false;
}