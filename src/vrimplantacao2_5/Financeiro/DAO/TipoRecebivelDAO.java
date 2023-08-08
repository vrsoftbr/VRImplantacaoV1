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

    public void gravarAutorizadora(AutorizadoraVO vo) throws Exception {
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

    public void apagarRecebivel() throws Exception {
        try (Statement st = Conexao.createStatement()) {
            st.execute(
                    "delete from fornecedorcontato ;\n"
                    + "delete from implantacao.codant_fornecedor;\n"
                    + "delete from fornecedorprazopedido ;\n"
                    + "delete from fornecedorprazo; \n"
                    + "delete from fornecedor where id <> 1;\n"
                    + "delete from recebivelconfiguracaotabela;\n"
                    + "delete from recebivelconfiguracao;\n"
                    + "delete from tiporecebivelfinalizadora;\n"
                    + "delete from tiporecebivel ;\n"
                    + "delete from tiporecebivel;\n"
                    + "delete from tiporecebivelfinalizadora;\n"
                    + "delete from entradasaidatipoentrada;\n"
                    + "delete from tiposaidacontabilidade;\n"
                    + "delete from tiposaidanotasaidasequencia;\n"
                    + "delete from cfoptiposaida;\n"
                    + "delete from tiposaida ;\n"
                    + "delete from contabilidade.tipoentrada ;\n"
                    + "delete from contabilidade.tiposaida;\n"
                    + "delete from cfoptipoentrada;\n"
                    + "delete from tipoentrada;\n"
                    + "delete from tipoplanoconta ;\n"
                    + "delete from ativo.grupo ;\n"
                    + "delete from contabilidade.ativoimobilizado;\n"
                    + "delete from contabilidade.tiposaida;\n"
                    + "delete from contabilidade.caixavenda; \n"
                    + "delete from contabilidade.maparesumo;\n"
                    + "delete from contabilidade.tipoentrada ;\n"
                    + "delete from contabilidade.tipoabatimento;\n"
                    + "delete from contabilidade.caixadiferenca ;\n"
                    + "delete from contacontabilfinanceiro;\n"
                    + "delete from contacontabilfiscal; \n"
                    + "delete from cfop;\n"
                    + "delete from cfoptipoentrada;\n"
                    + "delete from entradasaidatiposaida;\n"
                    + "delete from tiposaida;\n"
                    + "delete from historicopadrao;\n"
                    + "delete from tipoplanoconta ;\n"
                    + "delete from pdv.tipotef ;\n"
                    + "delete from pdv.autorizadora ;\n"
                    + "delete from pdv.finalizadoraconfiguracao;\n"
                    + "delete from pdv.finalizadoralayoutretorno;\n"
                    + "delete from pdv.finalizadora;\n"
                    + "delete from pdv.ecf;\n"
                    + "delete from pdv.tipomodelo ;\n"
                    + "delete from pdv.ecflayout;\n"
                    + "delete from pdv.tecladolayout ;\n"
                    + "delete from pdv.finalizadoralayoutretorno ;\n"
                    + "delete from pdv.tecladolayoutfuncao;	\n"
                    + "delete from pdv.funcaoniveloperador f;	\n"
                    + "delete from pdv.funcao f;");
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
