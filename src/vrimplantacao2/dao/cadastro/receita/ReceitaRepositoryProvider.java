/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.receita;

import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaAnteriorVO;

/**
 *
 * @author lucasrafael
 */
public class ReceitaRepositoryProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ProdutoAnteriorDAO produtoDAO;
    private final ReceitaAnteriorDAO anteriorDAO;

    public ReceitaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.produtoDAO = new ProdutoAnteriorDAO();
        this.anteriorDAO = new ReceitaAnteriorDAO();
    }
    
    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }
    
    public void setMessage(String message) throws Exception {
        ProgressBar.setStatus(message);
    }
    
    public void setMessage(String message, int size) throws Exception {
        setMessage(message);
        ProgressBar.setMaximum(size);
    }
    
    public void setMessage() throws Exception {
        ProgressBar.next();
    }

    public Map<String, Integer> getProdutos() throws Exception {
        return produtoDAO.getAnteriores(sistema, loja);
    }

    public Map<String, ReceitaAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(sistema, loja);
    }
    
}
