package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import vrimplantacao.vo.notafiscal.CfopVO;
import vrimplantacao.vo.notafiscal.TipoEntradaVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class TipoEntradaDAO {

    public TipoEntradaVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM tipoentrada WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Tipo entrada " + i_id + " não encontrado!");
        }

        TipoEntradaVO oTipoEntrada = new TipoEntradaVO();
        oTipoEntrada.id = rst.getInt("id");
        oTipoEntrada.descricao = rst.getString("descricao");
        oTipoEntrada.tipo = rst.getString("tipo");
        oTipoEntrada.atualizaCusto = rst.getBoolean("atualizacusto");
        oTipoEntrada.atualizaEstoque = rst.getBoolean("atualizaestoque");
        oTipoEntrada.atualizaPedido = rst.getBoolean("atualizapedido");
        oTipoEntrada.imprimeGuiaCega = rst.getBoolean("imprimeguiacega");
        oTipoEntrada.imprimeDivergencia = rst.getBoolean("imprimedivergencia");
        oTipoEntrada.atualizaPerda = rst.getBoolean("atualizaperda");
        oTipoEntrada.notaProdutor = rst.getBoolean("notaprodutor");
        oTipoEntrada.geraContrato = rst.getBoolean("geracontrato");
        oTipoEntrada.atualizaDataEntrada = rst.getBoolean("atualizadataentrada");
        oTipoEntrada.utilizaCustoTabela = rst.getBoolean("utilizacustotabela");
        oTipoEntrada.bonificacao = rst.getBoolean("bonificacao");
        oTipoEntrada.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oTipoEntrada.atualizaDivergenciaCusto = rst.getBoolean("atualizadivergenciacusto");
        oTipoEntrada.atualizaAdministracao = rst.getBoolean("atualizaadministracao");
        oTipoEntrada.atualizaFiscal = rst.getBoolean("atualizafiscal");
        oTipoEntrada.atualizaPagar = rst.getBoolean("atualizapagar");
        oTipoEntrada.atualizaTroca = rst.getBoolean("atualizatroca");
        oTipoEntrada.especie = rst.getString("especie");
        oTipoEntrada.atualizaEscrita = rst.getBoolean("atualizaescrita");
        oTipoEntrada.idContaContabilFiscalCredito = rst.getObject("id_contacontabilfiscalcredito") == null ? -1 : rst.getInt("id_contacontabilfiscalcredito");
        oTipoEntrada.idHistoricoPadrao = rst.getObject("id_historicopadrao") == null ? -1 : rst.getInt("id_historicopadrao");
        oTipoEntrada.idContaContabilFiscalDebito = rst.getObject("id_contacontabilfiscaldebito") == null ? -1 : rst.getInt("id_contacontabilfiscaldebito");
        oTipoEntrada.foraEstado = rst.getBoolean("foraestado");
        oTipoEntrada.substituicao = rst.getBoolean("substituicao");
        oTipoEntrada.idProduto = rst.getObject("id_produto") == null ? -1 : rst.getInt("id_produto");
        oTipoEntrada.verificaPedido = rst.getBoolean("verificapedido");

        sql = new StringBuilder();
        sql.append("SELECT c.id, c.cfop, c.descricao, c.foraestado, c.substituido, c.id_tipoentradasaida");
        sql.append(" FROM cfoptipoentrada cte");
        sql.append(" INNER JOIN cfop c ON c.cfop = cte.cfop");
        sql.append(" WHERE cte.id_tipoentrada = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            CfopVO oCfop = new CfopVO();
            oCfop.id = rst.getLong("id");
            oCfop.cfop = rst.getString("cfop");
            oCfop.descricao = rst.getString("descricao");
            oCfop.foraEstado = rst.getBoolean("foraestado");
            oCfop.substituido = rst.getBoolean("substituido");
            oCfop.idTipoEntradaSaida = rst.getInt("id_tipoentradasaida");

            oTipoEntrada.vCfop.add(oCfop);
        }

        return oTipoEntrada;
    }
}
