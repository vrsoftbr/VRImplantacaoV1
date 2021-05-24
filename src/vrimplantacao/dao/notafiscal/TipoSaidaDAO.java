package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vr.core.parametro.versao.Versao;
import vrimplantacao.vo.notafiscal.CfopVO;
import vrimplantacao.vo.notafiscal.TipoSaidaVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class TipoSaidaDAO {
    
    private final Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());

    public TipoSaidaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM tiposaida WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo saída " + i_id + " não encontrado!");
        }

        TipoSaidaVO oTipoSaida = new TipoSaidaVO();
        oTipoSaida.id = rst.getInt("id");
        oTipoSaida.descricao = rst.getString("descricao");
        //oTipoSaida.serie = rst.getString("serie");
        if (versao.igualOuMaiorQue(3,17,10)) {
            oTipoSaida.especie = rst.getString("especie");
        }
        oTipoSaida.idHistoricoPadrao = rst.getObject("id_historicopadrao") == null ? -1 : rst.getInt("id_historicopadrao");
        oTipoSaida.tipo = rst.getString("tipo");
        oTipoSaida.idContaContabilFiscalCredito = rst.getObject("id_contacontabilfiscalcredito") == null ? -1 : rst.getInt("id_contacontabilfiscalcredito");
        oTipoSaida.idContaContabilFiscalDebito = rst.getObject("id_contacontabilfiscaldebito") == null ? -1 : rst.getInt("id_contacontabilfiscaldebito");
        oTipoSaida.idTipoEntrada = rst.getObject("id_tipoentrada") == null ? -1 : rst.getInt("id_tipoentrada");
        oTipoSaida.idNotaSaidaMensagem = rst.getObject("id_notasaidamensagem") == null ? -1 : rst.getInt("id_notasaidamensagem");
        oTipoSaida.atualizaEscrita = rst.getBoolean("atualizaescrita");
        oTipoSaida.adicionaVenda = rst.getBoolean("adicionavenda");
        oTipoSaida.baixaEstoque = rst.getBoolean("baixaestoque");
        oTipoSaida.entraEstoque = rst.getBoolean("entraestoque");
        oTipoSaida.calculaIva = rst.getBoolean("calculaiva");
        oTipoSaida.consultaPedido = rst.getBoolean("consultapedido");
        oTipoSaida.desabilitaValor = rst.getBoolean("desabilitavalor");
        oTipoSaida.destinatarioCliente = rst.getBoolean("destinatariocliente");
        oTipoSaida.geraDevolucao = rst.getBoolean("geradevolucao");
        oTipoSaida.foraEstado = rst.getBoolean("foraestado");
        oTipoSaida.geraReceber = rst.getBoolean("gerareceber");
        oTipoSaida.substituicao = rst.getBoolean("substituicao");
        oTipoSaida.imprimeBoleto = rst.getBoolean("imprimeboleto");
        oTipoSaida.naoCreditaIcms = rst.getBoolean("naocreditaicms");
        oTipoSaida.notaProdutor = rst.getBoolean("notaprodutor");
        oTipoSaida.transferencia = rst.getBoolean("transferencia");
        oTipoSaida.transportadorProprio = rst.getBoolean("transportadorproprio");
        oTipoSaida.utilizaIcmsCredito = rst.getBoolean("utilizaicmscredito");
        oTipoSaida.utilizaIcmsEntrada = rst.getBoolean("utilizaicmsentrada");
        oTipoSaida.utilizaPrecoVenda = rst.getBoolean("utilizaprecovenda");
        oTipoSaida.vendaIndustria = rst.getBoolean("vendaindustria");
        oTipoSaida.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oTipoSaida.geraContrato = rst.getBoolean("geracontrato");

        sql = new StringBuilder();
        sql.append("SELECT c.id, c.cfop, c.descricao, c.foraestado, c.substituido, c.id_tipoentradasaida");
        sql.append(" FROM cfoptiposaida cts");
        sql.append(" INNER JOIN cfop c ON c.cfop = cts.cfop");
        sql.append(" WHERE cts.id_tiposaida = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            CfopVO oCfop = new CfopVO();
            oCfop.id = rst.getLong("id");
            oCfop.cfop = rst.getString("cfop");
            oCfop.descricao = rst.getString("descricao");
            oCfop.idTipoEntradaSaida = rst.getInt("id_tipoentradasaida");
            oCfop.foraEstado = rst.getBoolean("foraestado");
            oCfop.substituido = rst.getBoolean("substituido");

            oTipoSaida.vCfop.add(oCfop);
        }

        return oTipoSaida;
    }

    public TipoSaidaVO carregar(String i_cfop) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT b.* FROM cfoptiposaida a INNER JOIN tiposaida b ON (a.id_tiposaida = b.id) WHERE cfop = '" + i_cfop + "'");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo saida para CFOP " + i_cfop + " não encontrado!");
        }

        TipoSaidaVO oTipoSaida = new TipoSaidaVO();
        oTipoSaida.id = rst.getInt("id");
        oTipoSaida.descricao = rst.getString("descricao");
        oTipoSaida.tipo = rst.getString("tipo");
        oTipoSaida.notaProdutor = rst.getBoolean("notaprodutor");
        oTipoSaida.utilizaIcmsCredito = rst.getBoolean("utilizaicmscredito");
        oTipoSaida.geraContrato = rst.getBoolean("geracontrato");
        oTipoSaida.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oTipoSaida.especie = rst.getString("especie");
        oTipoSaida.atualizaEscrita = rst.getBoolean("atualizaescrita");
        oTipoSaida.idContaContabilFiscalCredito = rst.getObject("id_contacontabilfiscalcredito") == null ? -1 : rst.getInt("id_contacontabilfiscalcredito");
        oTipoSaida.idHistoricoPadrao = rst.getObject("id_historicopadrao") == null ? -1 : rst.getInt("id_historicopadrao");
        oTipoSaida.idContaContabilFiscalDebito = rst.getObject("id_contacontabilfiscaldebito") == null ? -1 : rst.getInt("id_contacontabilfiscaldebito");
        oTipoSaida.foraEstado = rst.getBoolean("foraestado");
        oTipoSaida.substituicao = rst.getBoolean("substituicao");
        oTipoSaida.destinatarioCliente = rst.getBoolean("destinatarioCliente");

        sql = new StringBuilder();
        sql.append("SELECT * FROM cfop c WHERE c.cfop = '" + i_cfop + "'");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            CfopVO oCfop = new CfopVO();
            oCfop.id = rst.getLong("id");
            oCfop.cfop = rst.getString("cfop");
            oCfop.descricao = rst.getString("descricao");
            oCfop.foraEstado = rst.getBoolean("foraestado");
            oCfop.substituido = rst.getBoolean("substituido");
            oCfop.idTipoEntradaSaida = rst.getInt("id_tipoentradasaida");
            
            oTipoSaida.vCfop.add(oCfop);
        }

        return oTipoSaida;
    }
}
