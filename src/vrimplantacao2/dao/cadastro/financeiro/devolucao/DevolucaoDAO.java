package vrimplantacao2.dao.cadastro.financeiro.devolucao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoVO;

/**
 *
 * @author Wesley
 */
public class DevolucaoDAO {

    private static final Logger LOG = Logger.getLogger(DevolucaoDAO.class.getName());

    public void gravarDevolucao(DevolucaoVO devolucao) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("receberdevolucao");
            sql.put("id_loja", devolucao.getId_loja());
            sql.put("id_fornecedor", devolucao.getId_fornecedor());
            sql.put("numeronota", devolucao.getNumeroNota());
            sql.put("dataemissao", devolucao.getDataEmissao());
            sql.put("datavencimento", devolucao.getDataVencimento());
            sql.put("valor", devolucao.getValor());
            sql.put("observacao", devolucao.getObservacao());
            sql.put("id_situacaoreceberdevolucao", devolucao.getId_situacaoreceberdevolucao());
            sql.put("id_tipolocalcobranca", devolucao.getId_tipolocalcobranca());
            sql.put("id_tipodevolucao", devolucao.getId_tipodevolucao());
            sql.put("lancamentomanual", devolucao.isLancamentomanual());
            sql.put("valorpagarfornecedor", devolucao.getValorpagarfornecedor());
            sql.putNull("id_notasaida");
            sql.putNull("id_boleto");
            sql.put("justificativa", devolucao.getJustificativa());
            sql.put("numeroparcela", devolucao.getNumeroparcela());
            sql.put("exportado", devolucao.isExportado());
            sql.put("datahoraalteracao", devolucao.getDatahoraalteracao());
            sql.put("dataexportacao", devolucao.getDataexportacao());
            sql.put("valorabatimento", devolucao.getValorabatimento());
            sql.put("valorliquido", devolucao.getValorliquido());
            sql.put("valorjurosatraso", devolucao.getValorjurosatraso());
            sql.getReturning().add("id");
            LOG.fine("SQL de gravação:\n" + sql.getInsert());
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                devolucao.setId(rst.getInt("id"));
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "SQL: " + sql.getInsert(), ex);
                throw ex;
            }
        }
    }
}
