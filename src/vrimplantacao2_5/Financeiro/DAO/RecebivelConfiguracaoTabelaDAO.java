package vrimplantacao2_5.Financeiro.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.Financeiro.VO.RecebivelConfiguracaoTabelaVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class RecebivelConfiguracaoTabelaDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(RecebivelConfiguracaoTabelaDAO.class.getName());

    public void salvar(RecebivelConfiguracaoTabelaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("recebivelconfiguracaotabela");

            sql.put("id", vo.getId());
            sql.put("id_recebivelconfiguracao", vo.getId_recebivelConfiguracao());
            sql.put("quantidadedia", vo.getQuantidadeDeDia());
            sql.put("utilizaregra", vo.isUtilizaRegra());
            sql.put("utilizadatacorte", vo.isUtilizaDataCorte());
            sql.put("id_tipovencimentorecebivel", vo.getId_tipoVencimentoRecebivel());
            sql.put("diasuteis", vo.isDiasUteis());
            sql.put("proximodiautil", vo.isProximoDiaUtil());

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
