package vrimplantacao2_5.service.migracao;

import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;

/**
 *
 * @author guilhermegomes
 */
public class ProdutoService {

    private ProdutoAnteriorDAO produtoAnteriorDAO;
    private ConfiguracaoBaseDadosDAO cfgDAO;

    public ProdutoService() {
        produtoAnteriorDAO = new ProdutoAnteriorDAO();
        cfgDAO = new ConfiguracaoBaseDadosDAO();
    }

    public ProdutoService(ProdutoAnteriorDAO produtoAnteriorDAO) {
        this.produtoAnteriorDAO = produtoAnteriorDAO;
    }

    public ProdutoService(ProdutoAnteriorDAO produtoAnteriorDAO, ConfiguracaoBaseDadosDAO cfgDAO) {
        this.produtoAnteriorDAO = produtoAnteriorDAO;
        this.cfgDAO = cfgDAO;
    }

    public int existeConexaoMigrada(int idConexao, String sistema) throws Exception {
        return produtoAnteriorDAO.getConexaoMigrada(idConexao, sistema);
    }

    public int verificaRegistro() throws Exception {
        return produtoAnteriorDAO.verificaRegistro();
    }

    public boolean verificaMigracaoMultiloja(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return produtoAnteriorDAO.verificaMigracaoMultiloja(lojaOrigem, sistema, idConexao);
    }

    public void copiarCodantProduto(String sistema, String lojaModelo, String lojaNova) throws Exception {
        produtoAnteriorDAO.copiarCodantProduto(sistema, lojaModelo, lojaNova);
    }

    public String getLojaModelo(int idConexao, String sistema) throws Exception {
        return produtoAnteriorDAO.getLojaModelo(idConexao, sistema);
    }

    public boolean verificaMultilojaMigrada(String lojaOrigem, String sistema, int idConexao) throws Exception {
        return produtoAnteriorDAO.verificaMultilojaMigrada(lojaOrigem, sistema, idConexao);
    }

    public boolean isLojaMatrizMigracao(int idConexao, String idLojaOrigem) throws Exception {
        return cfgDAO.verificaLojaMatrizMigracao(idConexao, idLojaOrigem);
    }

    /**
     * @return Retorna o impsistema da primeira loja migrada
     * @throws java.lang.Exception
     */
    public String getImpSistemaInicial() throws Exception {
        return produtoAnteriorDAO.getImpSistema();
    }
}
