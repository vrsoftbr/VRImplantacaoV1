package vrimplantacao2_5.Financeiro.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.Financeiro.VO.FinalizadoraConfiguracaoVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class FinalizadoraConfiguracaoDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(FinalizadoraConfiguracaoDAO.class.getName());

    public void salvar(FinalizadoraConfiguracaoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("finalizadoraconfiguracao");

            sql.put("id", vo.getId());
            sql.put("id_loja", vo.getId_loja());
            sql.put("id_finalizadora", vo.getId_finalizadora());
            sql.put("aceitatroco", vo.isAceitaTroco());
            sql.put("aceitaretirada", vo.isAceitaRetirada());
            sql.put("aceitaabastecimento", vo.isAceitaAbastecimento());
            sql.put("aceitarecebimento", vo.isAceitaRecebimento());
            sql.put("utilizacontravale", vo.isUtilizaContraVale());
            sql.put("retiradatotal", vo.isRetiradaTotal());
            sql.put("valormaximotroco", vo.getValorMaximoTroco());
            sql.put("juros", vo.getJuros());
            sql.put("aceitaretiradacf", vo.isAceitaRetiradaCf());
            sql.put("retiradatotalcf", vo.isRetiradaTotalCf());
            sql.put("utilizado", vo.isUtilizado());
            sql.put("avisaretirada", vo.isAvisaRetirada());

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
