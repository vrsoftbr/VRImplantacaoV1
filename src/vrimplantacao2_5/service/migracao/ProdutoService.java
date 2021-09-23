package vrimplantacao2_5.service.migracao;

import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;

/**
 *
 * @author guilhermegomes
 */
public class ProdutoService {

    private ProdutoAnteriorDAO produtoAnteriorDAO;
    
    public ProdutoService() {
        produtoAnteriorDAO = new ProdutoAnteriorDAO();
    }

    public ProdutoService(ProdutoAnteriorDAO produtoAnteriorDAO) {
        this.produtoAnteriorDAO = produtoAnteriorDAO;
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
}
