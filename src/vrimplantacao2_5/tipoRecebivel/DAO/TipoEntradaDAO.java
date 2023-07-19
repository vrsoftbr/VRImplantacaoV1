package vrimplantacao2_5.tipoRecebivel.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.tipoRecebivel.VO.TipoEntradaVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class TipoEntradaDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(TipoEntradaDAO.class.getName());
    

    public void salvar(TipoEntradaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("tipoentrada");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro().getId());
            sql.put("tipo", vo.getTipo());
            sql.put("atualizacusto", vo.isAtualizaCusto());
            sql.put("atualizaestoque", vo.isAtualizaEstoque());
            sql.put("atualizapedido", vo.isAtualizaPedido());
            sql.put("imprimeguiacega", vo.isImprimeGuiaCega());
            sql.put("imprimedivergencia", vo.isImprimeDivergencia());
            sql.put("atualizaperda", vo.isAtualizaPerda());
            sql.put("notaproduto", vo.isNotaProdutor());
            sql.put("geracontrato", vo.isGeraContrato());
            sql.put("atualizadataentrada", vo.isAtualizaDataEntrada());
            sql.put("utilizacustotabela", vo.isUtilizaCustoTabela());
            sql.put("bonificacao", vo.isBonificacao());
            sql.put("atualizadivergenciacusto", vo.isAtualizaDivergenciaCusto());
            sql.put("atualizaadministracao", vo.isAtualizaAdministracao());
            sql.put("atualizafiscal", vo.isAtualizaFiscal());
            sql.put("atualizapagar", vo.isAtualizaPagar());
            sql.put("atualizatroca", vo.isAtualizaTroca());
            sql.put("serie", vo.getSerie());
            sql.put("especie", vo.getEspecie());
            sql.put("atualizaescrita", vo.isAtualizaEscrita());
            sql.put("id_contacontabilfiscaldebito", vo.getId_contaContabilFiscalDebito());
            sql.put("id_historicopadrao", vo.getId_historicoPadrao());
            sql.put("id_contacontabilfiscalcredito", vo.getId_contaContabilFiscalCredito());
            sql.put("substituicao", vo.isSubstituicao());
            sql.put("foraestado", vo.isForaEstado());
            sql.put("id_produto", vo.getId_produto() == 0 ? null : vo.getId_produto());
            sql.put("verificapedido", vo.isVerificaPedido());
            sql.put("planoconta1", vo.getPlanoConta1());
            sql.put("planoconta2", vo.getPlanoConta2());
            sql.put("geraverba", vo.isGeraVerba());
            sql.put("contabilidadepadrao", vo.isContabilidadePadrao());
            sql.put("contabiliza", vo.isContabiliza());
            sql.put("creditapiscofins", vo.isCreditaPisCofins());
            sql.put("id_tipobasecalculocredito", vo.getId_tipoBaseCalculoCredito());
            sql.put("naocreditaicms", vo.isNaoCreditaIcms());
            sql.put("descargapalete", vo.isDescargaPalete());
            sql.put("ativoimobilizado", vo.isAtivoImobilizado());
            sql.put("id_ativogrupo", vo.getId_ativoGrupo());
            sql.put("utilizacentrocusto", vo.isUtilizaCentroCusto());
            sql.put("id_aliquota", vo.getId_aliquota());
            sql.put("notamei", vo.isNotaMei());
            sql.put("contabilizacontroller360", vo.isContabilizaController360());
            sql.put("utilizacustoorigem", vo.isUtilizaCustoOrigem());

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
