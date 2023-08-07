package vrimplantacao2_5.Financeiro.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.cadastro.AutorizadoraVO;
import vrimplantacao2.vo.cadastro.TipoRecebivelVO;
import vrimplantacao2.vo.cadastro.TipoTefVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class TipoRecebivelDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(TipoRecebivelDAO.class.getName());

    public void gravarTipoTef(TipoTefVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("tipotef");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("tipocomunicacao", vo.getTipocomunicao());
            sql.put("bandeira", vo.getBandeira());
            sql.put("imprimecupom", vo.isImprimeCupom());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro());
            sql.put("numeroparcela", vo.getNumeroParcela());
            sql.put("id_autorizadora", vo.getId_autorizadora());

            stm.execute(sql.getInsert());

        } catch (Exception e) {
            throw e;
        }
    }
    
    public void gravarAutorizadora(AutorizadoraVO vo) throws Exception{
        try(Statement stm = Conexao.createStatement()){
            SQLBuilder sql =  new SQLBuilder();
            
            sql.setSchema("pdv");
            sql.setTableName("autorizadora");
            
            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("utilizado", vo.isUtilizado());
                    
            
            stm.execute(sql.getInsert());
        
        }catch (Exception e){
            throw e;
        }
    }

    public void salvar(TipoRecebivelVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("contabilidade");
            sql.setTableName("ativoimobilizado");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("percentual", vo.getPercentual());
            sql.put("id_tipotef", vo.getId_TipoTef());
            sql.put("id_tipoticket", vo.getId_TipoTicket());
            sql.put("gerarecebimento", vo.isGeraRecebimento());
            sql.put("id_contacontabilfiscaldebito", vo.getId_contaContabilFiscalDebito());
            sql.put("id_contacontabilfiscalcredito", vo.getId_ContaContabilFiscalCredito());
            sql.put("id_historicopadrao", vo.getId_HistoricoPadrao());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro());
            sql.put("id_tipovistaprazo", vo.getId_TipoPrazo());
            sql.put("id_tipocartaotef", vo.getId_TipoCartaoTef());
            sql.put("id_fornecedor", vo.getId_Fornecedor());
            sql.put("tef", vo.isTef());
            sql.put("id_tiporecebimento", vo.getId_tiporecebimento());
            sql.put("contabiliza", vo.isContabiliza());
            sql.put("id_contacontabilfinanceiro", vo.getId_ContaContabilFinanceiro());

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
