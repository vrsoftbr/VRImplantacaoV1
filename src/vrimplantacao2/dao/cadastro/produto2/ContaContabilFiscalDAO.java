package vrimplantacao2.dao.cadastro.produto2;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.ContaContabilFiscaVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class ContaContabilFiscalDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(ContaContabilFiscalDAO.class.getName());

    public void salvar(ContaContabilFiscaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("contacontabil");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("conta1", vo.getConta1());
            sql.put("conta2", vo.getConta2());
            sql.put("conta3", vo.getConta3());
            sql.put("conta4",vo.getConta4());
            sql.put("conta5", vo.getConta5());
            sql.put("nivel", vo.getNivel());
            sql.put("id_situacaocadastro",vo.getId_situacaoCadastro().getId());
            sql.put("contareduzida", vo.getContaReduzida());
            sql.put("resultado", vo.isResultado());
            sql.put("data", vo.getData());
            sql.put("dmpl",vo.isDmpl());
            sql.put("contacompensacao", vo.isContaCompensacao());
            sql.put("notaexplicativa", vo.getNotaExplicativa());
            
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
