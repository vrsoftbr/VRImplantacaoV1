package vrimplantacao2_5.Financeiro.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.Financeiro.VO.RecebivelConfiguracaoVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class RecebivelConfiguracaoDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(RecebivelConfiguracaoDAO.class.getName());

    public void salvar(RecebivelConfiguracaoVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("recebivelconfiguracao");

            sql.put("id", vo.getId());
            sql.put("id_loja", vo.getId_loja());
            sql.put("id_banco", vo.getId_banco());
            sql.put("id_tiporecebivel", vo.getId_tipoRecebivel());
            sql.put("id_tipovencimentorecebivel", vo.getId_tipoVencimentoRecebivel());
            sql.put("taxa", vo.getTaxa());
            sql.put("utilizaregra", vo.isUtilizaRegra());
            sql.put("utilizatabela", vo.isUtilizaTabela());
            sql.put("utilizadatacorte", vo.isUtilizaDataCorte());
            sql.put("agencia", vo.getAgencia());
            sql.put("conta", vo.getConta());
            sql.put("quantidadediafixo", vo.getQuantidadeDiaFixo());
            sql.put("diasemanacorte", vo.getDiaSemanaCorte());
            sql.put("periodocorte", vo.getPeriodoCorte());
            sql.put("datainiciocorte", vo.getDataInicioCorte());
            sql.put("outrastaxas", vo.getOutrasTaxas());
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
