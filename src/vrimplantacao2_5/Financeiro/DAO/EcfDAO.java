package vrimplantacao2_5.Financeiro.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.Financeiro.VO.EcfVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class EcfDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(EcfDAO.class.getName());

    public void salvar(EcfVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("pdv");
            sql.setTableName("ecf");

            sql.put("id", vo.getId());
            sql.put("id_loja", vo.getId_loja());
            sql.put("ecf", vo.getEcf());
            sql.put("descricao", vo.getDescricao());
            sql.put("id_tipomarca", vo.getId_tipoMarca());
            sql.put("id_tipomodelo", vo.getId_tipoModelo());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro());
            sql.put("numeroserie", vo.getNumeroSerie());
            sql.put("mfadicional", vo.getMfAdicional());
            sql.put("numerousuario", vo.getNumeroUsuario());
            sql.put("tipoecf", vo.getTipoEcf());
            sql.put("versaosb", vo.getVersaoSb());
            sql.put("datahoragravacaosb", vo.getDatHoraGravacaoSb());
            sql.put("datahoracadastro", vo.getDataHoraCadastro());
            sql.put("incidenciadesconto", vo.isIncidenciaDesconto());
            sql.put("versaobiblioteca", vo.getVersaoBiblioteca());
            sql.put("geranfpaulista", vo.isGeraNfPaulista());
            sql.put("id_tipoestado", vo.getId_tipoEstado());
            sql.put("versao", vo.getVersao());
            sql.put("datamovimento", vo.getDataMovimento());
            sql.put("cargagdata", vo.isCargaGData());
            sql.put("cargaparam", vo.isCargaParam());
            sql.put("cargalayout", vo.isCargaLayout());
            sql.put("cargaimagem", vo.isCargaImagem());
            sql.put("id_tipolayoutnotapaulista", vo.getId_tipoLayoutNotaPaulista());
            sql.put("touch", vo.isTouch());
            sql.put("alteradopaf", vo.isAlteradoPaf());
            sql.put("horamovimento", vo.getHoraMovimento());
            sql.put("id_modelopdv", vo.getId_modeloPdv());

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
