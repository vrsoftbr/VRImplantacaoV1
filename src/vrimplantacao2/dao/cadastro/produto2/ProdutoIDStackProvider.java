package vrimplantacao2.dao.cadastro.produto2;

import java.util.Set;
import vrimplantacao2.utils.collection.IDStack;

/**
 * Classe responsável por fornecer a listagem dos dados ao produtoIDStack.
 * @author Leandro
 */
public class ProdutoIDStackProvider {
    
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    
    /**
     * Retorna todos os IDs vagos da balança.
     * @return IDs vagos.
     * @throws Exception 
     */
    public IDStack getIDsVagosBalanca() throws Exception {
        return produtoDAO.getIDsVagosBalanca();
    }

    /**
     * Retorna todos os IDs vagos normais.
     * @return IDs vagos.
     * @throws Exception 
     */
    public IDStack getIDsVagosNormais() throws Exception {
        return produtoDAO.getIDsVagosNormais();
    }

    /**
     * Retorna todos os IDs cadastrados.
     * @return IDs utilizados.
     * @throws Exception 
     */
    public Set<Integer> getIDsCadastrados() throws Exception {
        return produtoDAO.getIDsCadastrados();
    }
    
}
