package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.TipoTefVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Implantacao
 */
public class TipoTefDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(TipoTefDAO.class.getName());


    public void salvar(TipoTefVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            
            sql.setSchema("pdv");
            sql.setTableName("tipotef");

            sql.put("id", idValidos());
            sql.put("descricao", vo.getDescricao());
            sql.put("tipocomunicacao",vo.getTipocomunicao());
            sql.put("bandeira",vo.getBandeira());
            sql.put("imprimecupom", vo.isImprimeCupom());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro().getId());
            sql.put("numeroparcela",vo.getNumeroParcela());
            sql.put("id_autorizadora",vo.getId_autorizadora());
            
            stm.execute(sql.getInsert());
            System.out.println(sql.getInsert());
        } catch (Exception e) {
            throw e;
        }
    }
           

    public Integer ultimoId() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "max (id) + 1 as id \n"
                    + "from pdv.tipotef   "
            )) {
                while (rst.next()) {

                    return rst.getInt("id")+1;
                }
            }
        }
        return null;
    }

     public Integer idValidos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n"
                    + "(SELECT id FROM generate_series(1, 9999)\n"
                    + "AS s(id) EXCEPT SELECT id FROM pdv.tipotef WHERE id <= 9999) AS codigointerno ORDER BY id "
            )) {
                while (rst.next()) {

                    return rst.getInt("id");
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
