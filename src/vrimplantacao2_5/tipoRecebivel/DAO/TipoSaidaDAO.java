package vrimplantacao2_5.tipoRecebivel.DAO;

import java.sql.Statement;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.tipoRecebivel.VO.TipoSaidaVO;

/**
 * Classe que faz a interface entre o sistema e o banco de dados.
 *
 * @author Bruno
 */
public class TipoSaidaDAO {

    private int idLojaVR = 1;
    private String importSistema = null;
    private String importLoja = null;

    private static Logger LOG = Logger.getLogger(TipoSaidaDAO.class.getName());

    public void salvar(TipoSaidaVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("public");
            sql.setTableName("tiposaida");

            sql.put("id", vo.getId());
            sql.put("descricao", vo.getDescricao());
            sql.put("id_situacaocadastro", vo.getId_situacaoCadastro().getId());
            sql.put("baixaestoque", vo.isBaixaEstoque());
            sql.put("geradevolucao", vo.isGeraDevolucao());
            sql.put("especie", vo.getEspecie());
            sql.put("transportadorproprio", vo.isTransportadorProprio());;
            sql.put("destinatariocliente", vo.isDestinatarioCliente());
            sql.put("substituicao", vo.isSubsituicao());
            sql.put("foraestado", vo.isSubsituicao());
            sql.put("consultapedido", vo.isConsultaPedido());
            sql.put("imprimeboleto", vo.isImprimeBoleto());
            sql.put("atualizaescrita", vo.isAtualizaEscrita());
            sql.put("utilizaicmscredito", vo.isUtilizaIcmsCredito());
            sql.put("naocreditaicms", vo.isNaoCreditaIcms());
            sql.put("desabilitavalor", vo.isDesabilitaValor());
            sql.put("adicionavenda", vo.isAdicionaVenda());
            sql.put("gerareceber", vo.isGeraReceber());
            sql.put("utilizaprecovenda", vo.isUtilizaPrecoVenda());
            sql.put("notaprodutor", vo.isNotaProdutor());
            sql.put("transferencia", vo.isTransferencia());
            sql.put("id_tipoentrada", vo.getId_tipoEntrada());
            sql.put("tipo", vo.getTipo());
            sql.put("calculaiva", vo.isCalculaIva());
            sql.put("utilizaicmsentrada", vo.isUtilizaIcmsEntrada());
            sql.put("id_contacontabilfiscalcredito", vo.getId_contaContabilFiscalCredito());
            sql.put("id_contacontabilfiscaldebito", vo.getId_contaContabilFiscalDebito());
            sql.put("id_historicopadrao", vo.getId_historicoPadrao());
            sql.put("entraestoque", vo.isEntraEstoque());
            sql.put("vendaindustria", vo.isVendaIndustria());
            sql.put("id_notasaidamensagem", vo.getId_notaSaidaMensagem());
            sql.put("geraconttrato", vo.isGeraContrato());
            sql.put("contabilidadepadrao", vo.isContabilidadePadrao());
            sql.put("contabiliza", vo.isContabiliza());
            sql.put("atualizatroca", vo.isAtualizaTroca());
            sql.put("creditapiscofins", vo.isCreditaPisCofins());
            sql.put("consumidorfinal", vo.isConsumidorFinal());
            sql.put("id_tipopiscofins", vo.getId_tipoPisCofins());
            sql.put("id_aliquota", vo.getId_aliquota());
            sql.put("fabricaopropria", vo.isFabricacaoPropria());
            sql.put("utilizaprecocusto", vo.isUtilizaPrecoCusto());
            sql.put("convertertodasaliquotas", vo.isConverterTodasAliquotas());
            sql.put("id_tiposaida", vo.getId_tipoSaida());
            sql.put("utilizatributoscadastrodebito", vo.isUtilizaTributoCadastroDebito());
            sql.put("geraexportacao", vo.isGeraExportacao());
            sql.put("id_produto", vo.getId_produto());
            sql.put("converteraliquota", vo.isConverterAliquota());
            sql.put("planoconta1", vo.getPlanoConta1());
            sql.put("planoconta2", vo.getPlanoConta2());
            sql.put("notamei", vo.isNotaMei());
            sql.put("utilizacustomedio", vo.isUtilizaCustoMedio());

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
