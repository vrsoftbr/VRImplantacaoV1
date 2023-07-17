package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.AutorizadoraVO;
import vrimplantacao2.vo.importacao.AutorizadoraIMP;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Implantacao
 */
public class AutorizadoraDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(AutorizadoraDAO.class.getName());

    public void salvar(AutorizadoraVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("autorizadora");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("utilizado", vo.isUtilizado());

            stm.execute(sql.getInsert());
        } catch (Exception e) {
            throw e;
        }
    }

    public Integer idValidos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n"
                    + "(SELECT id FROM generate_series(1, 9999)\n"
                    + "AS s(id) EXCEPT SELECT id FROM pdv.autorizadora WHERE id <= 9999) AS codigointerno ORDER BY id "
            )) {
                while (rst.next()) {

                    return rst.getInt("id");
                }
            }
        }
        return null;
    }

    public Integer ultimoId() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "max (id) + 1 as id \n"
                    + "from pdv.autorizadora   "
            )) {
                while (rst.next()) {

                    return rst.getInt("id") + 1;
                }
            }
        }
        return null;
    }

    public Integer carregarAutorizadoras() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT * FROM"
                    + "pdv.autorizadoras"
            )) {
                while (rst.next()) {

                    return rst.getInt("id") + 1;
                }
            }
        }
        return null;
    }

    /**
     * @return the idLojaVR
     */
    public int getIdLojaVR() {
        return idLojaVR;
    }

    /**
     * @param idLojaVR the idLojaVR to set
     */
    public void setIdLojaVR(int idLojaVR) {
        this.idLojaVR = idLojaVR;
    }

    /**
     * @return the importSistema
     */
    public String getImportSistema() {
        return importSistema;
    }

    /**
     * @param importSistema the importSistema to set
     */
    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    /**
     * @return the importLoja
     */
    public String getImportLoja() {
        return importLoja;
    }

    /**
     * @param importLoja the importLoja to set
     */
    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    /**
     * @return the LOG
     */
    public static Logger getLOG() {
        return LOG;
    }

    /**
     * @param aLOG the LOG to set
     */
    public static void setLOG(Logger aLOG) {
        LOG = aLOG;
    }

}
