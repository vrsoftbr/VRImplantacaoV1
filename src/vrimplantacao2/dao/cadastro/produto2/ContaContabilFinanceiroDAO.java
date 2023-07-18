package vrimplantacao2.dao.cadastro.produto2;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ContaContabilFinanceiroVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class ContaContabilFinanceiroDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(ContaContabilFinanceiroDAO.class.getName());

    public void salvar(ContaContabilFinanceiroVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("contacontabilfinanceiro");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro().getId());
            sql.put("id_contacontabilfiscal",vo.getId_contaContabilFiscal());

            stm.execute(sql.getInsert());
        } catch (Exception e) {
            throw e;
        }
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
