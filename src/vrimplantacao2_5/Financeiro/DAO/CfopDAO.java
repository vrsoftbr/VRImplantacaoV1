package vrimplantacao2_5.Financeiro.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao.vo.administrativo.TipoEntradaSaida;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.Financeiro.VO.CfopVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class CfopDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(CfopDAO.class.getName());

    public void salvar(CfopVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("cfop");

            sql.put("id", vo.getId());
            sql.put("cfop", vo.getCfop());
            sql.put("descricao", vo.getDescricao());
            sql.put("foraestado", vo.isForaEstado());
            sql.put("substituido", vo.isSubstituido());
            sql.put("id_tipoentradasaida", vo.getTipoEntradaSaida());
            sql.put("geraicms", vo.isGeraIcms());
            sql.put("bonificado", vo.isBonificado());
            sql.put("devolucao", vo.isDevolucao());
            sql.put("vendaecf", vo.isVendaEcf());
            sql.put("devolucaocliente", vo.isDevolucaoCliente());
            sql.put("servico", vo.isServico());
            sql.put("fabricacaopropria", vo.isFabricacaoPropria());
            sql.put("exportacao", vo.isExportacao());

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
