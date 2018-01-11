package vrimplantacao.dao.fiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrimplantacao.vo.notafiscal.CfopVO;
import vrimplantacao.vo.notafiscal.TipoSaidaVO;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;

public class CfopDAO {

    public String gerarEntrada(int i_tipoEntrada, boolean i_foraEstado, boolean i_substituido) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT c.cfop");
        sql.append(" FROM cfop c");
        sql.append(" INNER JOIN cfoptipoentrada cte ON cte.cfop = c.cfop");
        sql.append(" WHERE cte.id_tipoentrada = " + i_tipoEntrada);
        sql.append(" AND c.foraestado = " + i_foraEstado);
        sql.append(" AND c.substituido = " + i_substituido);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Não foi possível gerar o código fiscal!");
        }

        return rst.getString("cfop");
    }

    public List<TipoSaidaVO> carregarTipoSaida(String i_cfop) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT ts.* FROM cfoptiposaida cts");
        sql.append(" INNER JOIN tiposaida ts ON cts.id_tiposaida = ts.id");
        sql.append(" WHERE cts.cfop = '" + i_cfop + "' AND ts.id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

        rst = stm.executeQuery(sql.toString());

        List<TipoSaidaVO> vTipoSaida = new ArrayList<TipoSaidaVO>();

        while (rst.next()) {
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

            vTipoSaida.add(oTipoSaida);
        }

        if (vTipoSaida.isEmpty()) {
            throw new VRException("Tipo saída para cfop " + i_cfop + " não encontrado!");
        }

        return vTipoSaida;
    }
    
    public String gerarSaida(int i_tipoSaida, boolean i_foraEstado, boolean i_substituido) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT cfop.cfop FROM cfop");
        sql.append(" INNER JOIN cfoptiposaida ON cfoptiposaida.cfop = cfop.cfop");
        sql.append(" WHERE cfoptiposaida.id_tiposaida = " + i_tipoSaida);
        sql.append(" AND cfop.foraestado = " + i_foraEstado);
        sql.append(" AND cfop.substituido = " + i_substituido);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Não foi possível gerar o código fiscal!");
        }

        return rst.getString("cfop");
    }

    public CfopVO carregar(String i_cfop) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM cfop WHERE cfop = '" + i_cfop + "'");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("CFOP " + i_cfop + " não encontrado!");
        }

        CfopVO oCfop = new CfopVO();
        oCfop.id = rst.getLong("id");
        oCfop.cfop = rst.getString("cfop");
        oCfop.descricao = rst.getString("descricao");
        oCfop.idTipoEntradaSaida = rst.getInt("id_tipoentradasaida");
        oCfop.foraEstado = rst.getBoolean("foraestado");
        oCfop.substituido = rst.getBoolean("substituido");
        oCfop.bonificado = rst.getBoolean("bonificado");
        oCfop.devolucaoFornecedor = rst.getBoolean("devolucao");
        oCfop.devolucaoCliente = rst.getBoolean("devolucaocliente");
        oCfop.geraIcms = rst.getBoolean("geraicms");
        oCfop.vendaEcf = rst.getBoolean("vendaecf");
        oCfop.servico = rst.getBoolean("servico");

        stm.close();

        return oCfop;
    }
}
