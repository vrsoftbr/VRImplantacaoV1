package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.TipoRecebivelVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Implantacao
 */
public class TipoRecebivelDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(TipoRecebivelDAO.class.getName());

    public Set<Integer> getIDsCadastrados() throws Exception {
        Set<Integer> cadastrados = new TreeSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery("SELECT id from tiporecebivel")) {
                while (rst.next()) {
                    cadastrados.add(rst.getInt("id"));
                }
            }
        }
        return cadastrados;
    }

    public Map<Integer, TipoRecebivelVO> getRecebivelExistentes() throws Exception {
        Map<Integer, TipoRecebivelVO> result = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from tiporecebivel t \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    TipoRecebivelVO vo = new TipoRecebivelVO();
                    vo.setId(rst.getInt("id"));
                    result.put(vo.getId(), vo);
                }
            }
        }

        return result;
    }

    public void salvar(TipoRecebivelVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("tiporecebivel");

            sql.put("id", ultimoId());
            sql.put("descricao", vo.getDescricao());
            sql.put("percentual", vo.getPercentual());
            sql.put("id_tipotef", vo.getId_TipoTef() <=0 ? null : vo.getId_TipoTef());
            sql.put("id_tipoticket", vo.getId_TipoTicket() <=0 ? null: vo.getId_TipoTicket());
            sql.put("gerarecebimento", vo.isGeraRecebimento());
            sql.put("id_contacontabilfiscaldebito", vo.getId_contaContabilFiscalDebito() <= 0 ? null : vo.getId_contaContabilFiscalDebito());
            sql.put("id_contacontabilfiscalcredito", vo.getId_ContaContabilFiscalCredito() <= 0 ? null : vo.getId_ContaContabilFiscalCredito());
            sql.put("id_historicopadrao", vo.getId_HistoricoPadrao() <=0 ? null : vo.getId_HistoricoPadrao());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro().getId());
            sql.put("id_tipovistaprazo", vo.getId_TipoPrazo());
            sql.put("id_tipocartaotef", vo.getId_TipoCartaoTef() <=0 ? null : vo.getId_TipoCartaoTef());
            sql.put("id_fornecedor", vo.getId_Fornecedor() <= 0 ? null : vo.getId_Fornecedor());
            sql.put("tef", vo.isTef());
            sql.put("id_tiporecebimento", vo.getId_tiporecebimento() <=0 ? null : vo.getId_tiporecebimento());
            sql.put("contabiliza", vo.isContabiliza());
            sql.put("id_contacontabilfinanceiro", vo.getId_ContaContabilFinanceiro() == null ? 1 : vo.getId_ContaContabilFinanceiro());

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
                    + "from tiporecebivel   "
            )) {
                while (rst.next()) {

                    return rst.getInt("id")+1;
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
