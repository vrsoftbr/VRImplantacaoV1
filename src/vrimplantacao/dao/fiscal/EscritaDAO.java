package vrimplantacao.dao.fiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.dao.LogTransacaoDAO;
import vrimplantacao.vo.Formulario;
import vrimplantacao.vo.TipoTransacao;
import vrimplantacao.vo.notafiscal.EscritaItemVO;
import vrimplantacao.vo.notafiscal.EscritaVO;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class EscritaDAO {
    
    public static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Deprecated
    public void excluir(long i_id) throws Exception {
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            EscritaVO oEscrita = carregar(i_id);

            if (oEscrita.conferido) {
                throw new VRException("A nota nº " + oEscrita.numeroNota + " está conferida no fiscal e não pode ser estornada!");
            }

            stm.execute("DELETE FROM escritaitem WHERE id_escrita = " + i_id);
            stm.execute("DELETE FROM escrita WHERE id = " + i_id);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public EscritaVO carregar(long i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM escrita WHERE id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Nota fiscal não encontrada!");
        }

        EscritaVO oEscrita = new EscritaVO();
        oEscrita.id = rst.getLong("id");
        oEscrita.idLoja = rst.getInt("id_loja");
        oEscrita.numeroNota = rst.getInt("numeronota");
        oEscrita.data = Util.formatDataGUI(rst.getDate("data"));
        oEscrita.dataEmissao = Util.formatDataGUI(rst.getDate("dataemissao"));
        oEscrita.idFornecedor = rst.getObject("id_fornecedor") == null ? -1 : rst.getInt("id_fornecedor");
        oEscrita.idClienteEventual = (rst.getObject("id_clienteeventual") == null ? -1 : rst.getInt("id_clienteeventual"));
        oEscrita.idEstado = rst.getInt("id_estado");
        oEscrita.idTipoEntradaSaida = rst.getInt("id_tipoentradasaida");
        oEscrita.idTipoEntrada = rst.getObject("id_tipoentrada") == null ? -1 : rst.getInt("id_tipoentrada");
        oEscrita.idTipoSaida = rst.getObject("id_tiposaida") == null ? -1 : rst.getInt("id_tiposaida");
        oEscrita.serie = rst.getString("serie");
        oEscrita.modelo = rst.getString("modelo");
        oEscrita.idContaContabilFiscalCredito = rst.getObject("id_contacontabilfiscalcredito") == null ? -1 : rst.getInt("id_contacontabilfiscalcredito");
        oEscrita.idContaContabilFiscalDebito = rst.getObject("id_contacontabilfiscaldebito") == null ? -1 : rst.getInt("id_contacontabilfiscaldebito");
        oEscrita.idHistoricoPadrao = rst.getObject("id_historicopadrao") == null ? -1 : rst.getInt("id_historicopadrao");
        oEscrita.ecf = rst.getObject("ecf") == null ? -1 : rst.getInt("ecf");
        oEscrita.observacao = rst.getString("observacao");
        oEscrita.chaveNfe = rst.getString("chavenfe");
        oEscrita.idTipoFreteNotaFiscal = rst.getInt("id_tipofretenotafiscal");
        oEscrita.valorIpi = rst.getDouble("valoripi");
        oEscrita.valorIcms = rst.getDouble("valoricms");
        oEscrita.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
        oEscrita.valorBaseCalculo = rst.getDouble("valorbasecalculo");
        oEscrita.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
        oEscrita.valorCancelamento = rst.getDouble("valorcancelamento");
        oEscrita.valorFrete = rst.getDouble("valorfrete");
        oEscrita.valorAcrescimo = rst.getDouble("valoracrescimo");
        oEscrita.valorOutrasDespesas = rst.getDouble("valoroutrasdespesas");
        oEscrita.valorDesconto = rst.getDouble("valordesconto");
        oEscrita.valorContabil = rst.getDouble("valorcontabil");
        oEscrita.cupomFiscal = rst.getBoolean("cupomfiscal");
        oEscrita.conferido = rst.getBoolean("conferido");
        oEscrita.especie = rst.getString("especie");
        oEscrita.idTipoNota = rst.getInt("id_tiponota");
        oEscrita.cancelado = rst.getBoolean("cancelado");
        oEscrita.cupomFiscal = rst.getBoolean("cupomfiscal");
        oEscrita.idSituacaoNfe = rst.getInt("id_situacaonfe");
        oEscrita.idNotaSaida = rst.getObject("id_notasaida") == null ? -1 : rst.getLong("id_notasaida");
        oEscrita.aplicaIcmsIpi = rst.getBoolean("aplicaicmsipi");
        oEscrita.idFornecedorProdutorRural = rst.getObject("id_fornecedorprodutorrural") == null ? -1 : rst.getInt("id_fornecedorprodutorrural");
        oEscrita.informacaoComplementar = rst.getString("informacaocomplementar");

        sql = new StringBuilder();
        sql.append("SELECT ei.*, p.descricaocompleta AS produto, p.id_tipoembalagem, tipopiscofins.descricao AS tipopiscofins,");
        sql.append(" a.descricao AS aliquota");
        sql.append(" FROM escritaitem ei");
        sql.append(" INNER JOIN produto p ON p.id = ei.id_produto");
        sql.append(" INNER JOIN tipopiscofins ON tipopiscofins.id = ei.id_tipopiscofins");
        sql.append(" INNER JOIN aliquota AS a ON a.id = ei.id_aliquota");
        sql.append(" WHERE ei.id_escrita = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            EscritaItemVO oItem = new EscritaItemVO();
            oItem.id = rst.getInt("id");
            oItem.idProduto = rst.getInt("id_produto");
            oItem.idAliquota = rst.getInt("id_aliquota");
            oItem.aliquota = rst.getString("aliquota");
            oItem.produto = rst.getString("produto");
            oItem.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
            oItem.quantidade = rst.getDouble("quantidade");
            oItem.valorTotal = rst.getDouble("valortotal");
            oItem.valorIpi = rst.getDouble("valoripi");
            oItem.valorIcms = rst.getDouble("valoricms");
            oItem.valorBaseCalculo = rst.getDouble("valorbasecalculo");
            oItem.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
            oItem.valorBaseSubstituicao = rst.getDouble("valorbasesubstituicao");
            oItem.cfop = rst.getString("cfop");
            oItem.idTipoPisCofins = rst.getInt("id_tipopiscofins");
            oItem.tipoPisCofins = rst.getString("tipopiscofins");
            oItem.valorIsento = rst.getDouble("valorisento");
            oItem.valorOutras = rst.getDouble("valoroutras");
            oItem.valorDesconto = rst.getDouble("valordesconto");
            oItem.valorAcrescimo = rst.getDouble("valoracrescimo");
            oItem.valorCancelado = rst.getDouble("valorcancelado");
            oItem.cancelado = rst.getBoolean("cancelado");
            oItem.situacaoTributaria = rst.getInt("situacaotributaria");
            oItem.valorFrete = rst.getDouble("valorfrete");
            oItem.valorOutrasDespesas = rst.getDouble("valoroutrasdespesas");
            oItem.tipoNaturezaReceita = rst.getObject("tiponaturezareceita") == null ? -1 : rst.getInt("tiponaturezareceita");

            oEscrita.vItem.add(oItem);
        }

        stm.close();

        return oEscrita;
    }
    
    public void salvar(EscritaVO i_escrita) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id FROM escrita WHERE id = " + i_escrita.id);

            if (rst.next()) {
                sql = new StringBuilder();
                sql.append("UPDATE escrita SET ");
                sql.append(" data = '" + Util.formatDataBanco(i_escrita.data) + "',");
                sql.append(" dataemissao = '" + Util.formatDataBanco(i_escrita.dataEmissao) + "',");
                sql.append(" numeronota = " + i_escrita.numeroNota + ",");
                sql.append(" id_fornecedor = " + (i_escrita.idFornecedor == -1 ? "NULL" : i_escrita.idFornecedor) + ",");
                sql.append(" id_clienteeventual = " + (i_escrita.idClienteEventual == -1 ? "NULL" : i_escrita.idClienteEventual) + ",");
                sql.append(" id_tipoentradasaida = " + i_escrita.idTipoEntradaSaida + ",");
                sql.append(" id_tipoentrada = " + (i_escrita.idTipoEntrada == -1 ? "NULL" : i_escrita.idTipoEntrada) + ",");
                sql.append(" id_tiposaida = " + (i_escrita.idTipoSaida == -1 ? "NULL" : i_escrita.idTipoSaida) + ",");
                sql.append(" id_estado = " + i_escrita.idEstado + ",");
                sql.append(" valoripi = " + i_escrita.valorIpi + ",");
                sql.append(" valoricms = " + i_escrita.valorIcms + ",");
                sql.append(" valoricmssubstituicao = " + i_escrita.valorIcmsSubstituicao + ",");
                sql.append(" valorbasecalculo = " + i_escrita.valorBaseCalculo + ",");
                sql.append(" valorbasesubstituicao = " + i_escrita.valorBaseSubstituicao + ",");
                sql.append(" valorcancelamento = " + i_escrita.valorCancelamento + ",");
                sql.append(" valorfrete = " + i_escrita.valorFrete + ",");
                sql.append(" valoracrescimo = " + i_escrita.valorAcrescimo + ",");
                sql.append(" valoroutrasdespesas = " + i_escrita.valorOutrasDespesas + ",");
                sql.append(" valordesconto = " + i_escrita.valorDesconto + ",");
                sql.append(" valorcontabil = " + i_escrita.valorContabil + ",");
                sql.append(" observacao = '" + i_escrita.observacao + "',");
                sql.append(" chavenfe = '" + i_escrita.chaveNfe + "',");
                sql.append(" serie = '" + i_escrita.serie + "',");
                sql.append(" modelo = '" + i_escrita.modelo + "',");
                sql.append(" id_contacontabilfiscaldebito = " + (i_escrita.idContaContabilFiscalDebito == -1 ? "NULL" : i_escrita.idContaContabilFiscalDebito) + ",");
                sql.append(" id_contacontabilfiscalcredito = " + (i_escrita.idContaContabilFiscalCredito == -1 ? "NULL" : i_escrita.idContaContabilFiscalCredito) + ",");
                sql.append(" id_historicopadrao = " + (i_escrita.idHistoricoPadrao == -1 ? "NULL" : i_escrita.idHistoricoPadrao) + ",");
                sql.append(" id_loja = " + i_escrita.idLoja + ",");
                sql.append(" ecf = " + (i_escrita.ecf == -1 ? "NULL" : i_escrita.ecf) + ",");
                sql.append(" id_tipofretenotafiscal = " + i_escrita.idTipoFreteNotaFiscal + ",");
                sql.append(" conferido = " + i_escrita.conferido + ",");
                sql.append(" especie = '" + i_escrita.especie + "',");
                sql.append(" id_tiponota = " + i_escrita.idTipoNota + ", ");
                sql.append(" cancelado = " + i_escrita.cancelado + ", ");
                sql.append(" cupomfiscal = " + i_escrita.cupomFiscal + ", ");
                sql.append(" aplicaicmsipi = " + i_escrita.aplicaIcmsIpi + ", ");
                sql.append(" informacaocomplementar = '" + i_escrita.informacaoComplementar + "',");
                sql.append(" id_situacaonfe = " + i_escrita.idSituacaoNfe);
                sql.append(" WHERE id = " + i_escrita.id);

                stm.execute(sql.toString());

                new LogTransacaoDAO().gerar(Formulario.FISCAL_ESCRITURACAO_NOTA_FISCAL, TipoTransacao.ALTERACAO, i_escrita.numeroNota, "", i_escrita.id);

            } else {
                //i_escrita.idLoja = Global.idLoja;

                sql = new StringBuilder();
                sql.append("INSERT INTO escrita (id_loja, data, dataemissao, numeronota, id_fornecedor, id_clienteeventual, id_tipoentradasaida,");
                sql.append(" id_tipoentrada, id_tiposaida, id_estado, valoripi, valoricms, valoricmssubstituicao, valorbasecalculo,");
                sql.append(" valorbasesubstituicao, valorcancelamento, valorfrete, valoracrescimo, valoroutrasdespesas, valordesconto, valorcontabil, observacao, complemento,");
                sql.append(" serie, modelo, id_contacontabilfiscaldebito, id_contacontabilfiscalcredito, id_historicopadrao, ecf, id_fornecedorprodutorrural,");
                sql.append(" chavenfe, id_notaentrada, id_notadespesa, id_notasaida, id_tipofretenotafiscal, conferido, especie, id_tiponota, cancelado, cupomfiscal, id_situacaonfe, aplicaicmsipi,informacaocomplementar) VALUES (");
                sql.append(i_escrita.idLoja + ", ");
                sql.append("'" + Util.formatDataBanco(i_escrita.data) + "', ");
                sql.append("'" + Util.formatDataBanco(i_escrita.dataEmissao) + "', ");
                sql.append(i_escrita.numeroNota + ", ");
                sql.append((i_escrita.idFornecedor == -1 ? "NULL" : i_escrita.idFornecedor) + ", ");
                sql.append((i_escrita.idClienteEventual == -1 ? "NULL" : i_escrita.idClienteEventual) + ", ");
                sql.append(i_escrita.idTipoEntradaSaida + ", ");
                sql.append((i_escrita.idTipoEntrada == -1 ? "NULL" : i_escrita.idTipoEntrada) + ", ");
                sql.append((i_escrita.idTipoSaida == -1 ? "NULL" : i_escrita.idTipoSaida) + ", ");
                sql.append(i_escrita.idEstado + ", ");
                sql.append(i_escrita.valorIpi + ", ");
                sql.append(i_escrita.valorIcms + ", ");
                sql.append(i_escrita.valorIcmsSubstituicao + ", ");
                sql.append(i_escrita.valorBaseCalculo + ", ");
                sql.append(i_escrita.valorBaseSubstituicao + ", ");
                sql.append(i_escrita.valorCancelamento + ", ");
                sql.append(i_escrita.valorFrete + ", ");
                sql.append(i_escrita.valorAcrescimo + ", ");
                sql.append(i_escrita.valorOutrasDespesas + ", ");
                sql.append(i_escrita.valorDesconto + ", ");
                sql.append(i_escrita.valorContabil + ", ");
                sql.append("'" + i_escrita.observacao + "', ");
                sql.append(i_escrita.complemento + ", ");
                sql.append("'" + i_escrita.serie + "', ");
                sql.append("'" + i_escrita.modelo + "', ");
                sql.append((i_escrita.idContaContabilFiscalDebito == -1 ? "NULL" : i_escrita.idContaContabilFiscalDebito) + ", ");
                sql.append((i_escrita.idContaContabilFiscalCredito == -1 ? "NULL" : i_escrita.idContaContabilFiscalCredito) + ", ");
                sql.append((i_escrita.idHistoricoPadrao == -1 ? "NULL" : i_escrita.idHistoricoPadrao) + ", ");
                sql.append((i_escrita.ecf == -1 ? "NULL" : i_escrita.ecf) + ", ");
                sql.append((i_escrita.idFornecedorProdutorRural == -1 ? "NULL" : i_escrita.idFornecedorProdutorRural) + ", ");
                sql.append("'" + i_escrita.chaveNfe + "', ");
                sql.append((i_escrita.idNotaEntrada == -1 ? "NULL" : i_escrita.idNotaEntrada) + ", ");
                sql.append((i_escrita.idNotaDespesa == -1 ? "NULL" : i_escrita.idNotaDespesa) + ", ");
                sql.append((i_escrita.idNotaSaida == -1 ? "NULL" : i_escrita.idNotaSaida) + ", ");
                sql.append(i_escrita.idTipoFreteNotaFiscal + ", ");
                sql.append(i_escrita.conferido + ", ");
                sql.append("'" + i_escrita.especie + "', ");
                sql.append(i_escrita.idTipoNota + ", ");
                sql.append(i_escrita.cancelado + ", ");
                sql.append(i_escrita.cupomFiscal + ", ");
                sql.append(i_escrita.idSituacaoNfe + ", ");
                sql.append(i_escrita.aplicaIcmsIpi + ", ");
                sql.append("'" + i_escrita.informacaoComplementar + "')");

                stm.execute(sql.toString());

                rst = stm.executeQuery("SELECT CURRVAL('escrita_id_seq') AS id");
                rst.next();

                i_escrita.id = rst.getLong("id");

                new LogTransacaoDAO().gerar(Formulario.FISCAL_ESCRITURACAO_NOTA_FISCAL, TipoTransacao.INCLUSAO, i_escrita.numeroNota, "LOJA " + i_escrita.idLoja, i_escrita.id);
            }

            stm.execute("DELETE FROM escritaitem WHERE id_escrita = " + i_escrita.id);

            for (EscritaItemVO oItem : i_escrita.vItem) {
                sql = new StringBuilder();
                sql.append("INSERT INTO escritaitem (id_escrita, id_produto, id_aliquota, quantidade, valortotal, valoripi, valorbasecalculo, valoricms,");
                sql.append(" valorbasesubstituicao, valoricmssubstituicao, cfop, id_tipopiscofins, valorisento, valoroutras, valordesconto, valoracrescimo,");
                sql.append(" valorcancelado, cancelado, situacaotributaria, valorfrete, valoroutrasdespesas, tiponaturezareceita) VALUES(");
                sql.append(i_escrita.id + ", ");
                sql.append(oItem.idProduto + ", ");
                sql.append(oItem.idAliquota + ", ");
                sql.append(oItem.quantidade + ", ");
                sql.append(oItem.valorTotal + ", ");
                sql.append(oItem.valorIpi + ", ");
                sql.append(oItem.valorBaseCalculo + ", ");
                sql.append(oItem.valorIcms + ", ");
                sql.append(oItem.valorBaseSubstituicao + ", ");
                sql.append(oItem.valorIcmsSubstituicao + ", ");
                sql.append("'" + oItem.cfop + "', ");
                sql.append(oItem.idTipoPisCofins + ", ");
                sql.append(oItem.valorIsento + ", ");
                sql.append(oItem.valorOutras + ", ");
                sql.append(oItem.valorDesconto + ", ");
                sql.append(oItem.valorAcrescimo + ", ");
                sql.append(oItem.valorCancelado + ", ");
                sql.append(oItem.cancelado + ", ");
                sql.append(oItem.situacaoTributaria + ", ");
                sql.append(oItem.valorFrete + ", ");
                sql.append(oItem.valorOutrasDespesas + ", ");
                sql.append((oItem.tipoNaturezaReceita == -1 ? "NULL" : oItem.tipoNaturezaReceita) + ")");

                stm.execute(sql.toString());
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public List<Integer> getIdsPorData(int idLoja, Date dt) throws Exception {
        List<Integer> result = new ArrayList<>();
        
        try (Statement st = Conexao.createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	id\n" +
                    "from\n" +
                    "	escrita e\n" +
                    "where\n" +
                    "	e.\"data\" = '" + SQL_DATE_FORMAT.format(dt) + "' and\n" +
                    "	e.id_loja = " + idLoja + "\n" +
                    "order by\n" +
                    "	id desc"
            )) {
                while (rs.next()) {
                    result.add(rs.getInt("id"));
                }
            }
        }
        
        return result;
    }

    public void excluirId(int idEscrita) throws Exception {
        try (Statement st = Conexao.createStatement()) {
            st.execute(
                    "do $$\n" +
                    "declare\n" +
                    "	v_id integer = " + idEscrita + ";\n" +
                    "begin\n" +
                    "	UPDATE escritanotasaidacomplemento SET \n" +
                    "		id_escritanotasaidacomplementada = null\n" +
                    "	where\n" +
                    "		id_escritanotasaidacomplementada = v_id;\n" +
                    "	\n" +
                    "	UPDATE escrita set\n" +
                    "		id_escritanotasaidacomplemento = null\n" +
                    "	where\n" +
                    "		id_escritanotasaidacomplemento = v_id;\n" +
                    "\n" +
                    "	delete from escritanotasaidacupom where id_escrita = v_id;\n" +
                    "	delete from escritagnre where id_escrita = v_id;\n" +
                    "	delete from escritaobservacaofiscalajuste where id_escritaobservacaofiscal in (select id from escritaobservacaofiscal where id_escrita = v_id);\n" +
                    "	delete from escritaobservacaofiscal where id_escrita = v_id;\n" +
                    "	delete from escritadadoscomplementaresenergiaeletrica where id_escrita = v_id;\n" +
                    "	delete from escritacentrocusto where id_escrita = v_id;\n" +
                    "	delete from escritaoutrosvalores where id_escrita = v_id;\n" +
                    "	delete from escritanotasaidanotaentrada where id_escrita = v_id;\n" +
                    "	delete from escritaitem where id_escrita = v_id; \n" +
                    "	delete from escrita where id = v_id;\n" +
                    "\n" +
                    "end;\n" +
                    "$$"
            );
        }
    }
}
