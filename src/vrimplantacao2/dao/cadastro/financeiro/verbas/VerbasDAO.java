package vrimplantacao2.dao.cadastro.financeiro.verbas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasVO;

/**
 *
 * @author Wesley
 */
public class VerbasDAO {

    private static final Logger LOG = Logger.getLogger(VerbasDAO.class.getName());

    public void gravarVerbas(VerbasVO verbas) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("verba");
            sql.put("id_loja", verbas.getId_loja());
            sql.put("id_tiporecebimento", verbas.getId_tiporecebimento());
            sql.put("dataemissao", verbas.getDataEmissao());
            sql.put("id_fornecedor", verbas.getId_fornecedor());
            sql.put("id_divisaofornecedor", verbas.getId_divisaofornecedor());
            sql.put("id_comprador", verbas.getId_comprador());
            sql.put("mercadologico1", verbas.getMercadologico1());
            sql.put("id_tipoverba", verbas.getId_tipoverba());
            sql.put("id_situacaocadastro", verbas.getId_situacaocadastro());
            sql.put("id_situacaoverba", verbas.getId_situacaoverba());
            sql.put("representante", verbas.getRepresentante());
            sql.put("telefone", verbas.getTelefone());
            sql.put("id_tipolocalcobranca", verbas.getId_tipolocalcobranca());
            sql.put("valor", verbas.getValor());
            sql.put("observacao", verbas.getObservacao());
            sql.put("cpfrepresentante", verbas.getCpfrepresentante());
            sql.put("rgrepresentante", verbas.getRgrepresentante());
            sql.putNull("id_verbasellout");
            sql.putNull("id_notaentrada");
            sql.put("id_tipoorigemverba", verbas.getId_tipoorigemverba());
            sql.put("valorrebaixacusto", verbas.getValorrebaixacusto());
            sql.getReturning().add("id");
            LOG.fine("SQL de gravação:\n" + sql.getInsert());
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                verbas.setId(rst.getInt("id"));
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "SQL: " + sql.getInsert(), ex);
                throw ex;
            }
        }
    }

    public void gravarVencimento(VerbasVO verbas) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("verbavencimento");
            sql.put("id_verba", verbas.getId());
            sql.put("datavencimento", verbas.getDataVencimento());
            sql.put("valor", verbas.getValor());
            sql.getReturning().add("id");
            LOG.fine("SQL de gravação:\n" + sql.getInsert());
            try (ResultSet rst = stm.executeQuery(
                    sql.getInsert()
            )) {
                rst.next();
                verbas.setCodigoAtualVencimento(rst.getInt("id"));
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "SQL: " + sql.getInsert(), ex);
                throw ex;
            }
        }
    }
}
